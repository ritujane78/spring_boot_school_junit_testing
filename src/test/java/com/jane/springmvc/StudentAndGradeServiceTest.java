package com.jane.springmvc;

import com.jane.springmvc.models.*;
import com.jane.springmvc.repository.HistoryGradeDAO;
import com.jane.springmvc.repository.MathGradeDAO;
import com.jane.springmvc.repository.ScienceGradeDAO;
import com.jane.springmvc.repository.StudentDAO;
import com.jane.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private MathGradeDAO mathGradeDAO;

    @Autowired
    private ScienceGradeDAO scienceGradeDAO;

    @Autowired
    private HistoryGradeDAO historyGradeDAO;

    @Value("${sql.script.create.student}")
    private String sqlCreateStudent;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlCreateMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlCreateScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlCreateHistoryGrade;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setup() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }

    @Test
    public void createStudentService() {
//        studentService.createStudent("Ritu", "Bafna", "ritubafna@abc_school.com");

        CollegeStudent student = studentDAO.findByEmailAddress("ritubafna@abc_school.com");

        assertEquals("ritubafna@abc_school.com", student.getEmailAddress());
    }

    @Test
    public void isStudentCheckNull(){
        assertTrue(studentService.checkIfStudentIsPresent(1));

        assertFalse(studentService.checkIfStudentIsPresent(0));
    }

    @AfterEach
    public void tearDown() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);

    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> student = studentDAO.findById(1);
        Optional<MathGrade> deleteMathGrade = mathGradeDAO.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradeDAO.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradeDAO.findById(1);

        assertTrue(student.isPresent(), "Student not found");
        assertTrue(deleteMathGrade.isPresent(), "Math grade not found");
        assertTrue(deleteScienceGrade.isPresent(), "Science grade not found");
        assertTrue(deleteHistoryGrade.isPresent(), "History grade not found");

        studentService.deleteStudent(1);


        student = studentDAO.findById(1);
        deleteMathGrade = mathGradeDAO.findById(1);
        deleteScienceGrade = scienceGradeDAO.findById(1);
        deleteHistoryGrade = historyGradeDAO.findById(1);

        assertFalse(student.isPresent(), "Student should not be present");
        assertFalse(deleteMathGrade.isPresent(), "Math grade should not be present");
        assertFalse(deleteScienceGrade.isPresent(), "Science grade should not be present");
        assertFalse(deleteHistoryGrade.isPresent(), "History grade should not be present");
    }

    @Test
    @Sql("/insertData.sql")
    public void getGradebookService(){
        Iterable<CollegeStudent> collegeStudentIterable = studentService.getGradebook();

        List<CollegeStudent> collegeStudentList = new ArrayList<>();

        for(CollegeStudent collegeStudent : collegeStudentIterable){
            collegeStudentList.add(collegeStudent);
        }
        assertEquals(5, collegeStudentList.size());
    }

    @Test
    public void createGradeService(){
        // Create the grade
        assertTrue(studentService.createGrade(85.5, 1, "math"));
        assertTrue(studentService.createGrade(80.5, 1, "science"));
        assertTrue(studentService.createGrade(80.5, 1, "history"));

        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(1);

        // Verify there are grades
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student should have 2 math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student should have 2 science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student should have 2 history grades");
    }
    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-85.5, 1, "math"));
        assertFalse(studentService.createGrade(85.5, 2, "math"));
        assertFalse(studentService.createGrade(85.5, 1, "literature"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(1, studentService.deleteGrade(1, "math"), "the Student id whose grade  is returned");
        assertEquals(1, studentService.deleteGrade(1, "science"), "the Student id whose grade  is returned");
        assertEquals(1, studentService.deleteGrade(1, "history"), "the Student id whose grade  is returned");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        assertEquals(0, studentService.deleteGrade(0,"math"), "No Student should have 0  id");
        assertEquals(0, studentService.deleteGrade(1,"literature"), "No Student should have literature class");
    }

    @Test
    public  void studentInformationTest(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.getInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId(), "Student id should be 1");
        assertEquals("Ritu", gradebookCollegeStudent.getFirstname(), "First name should be Ritu");
        assertEquals("Bafna", gradebookCollegeStudent.getLastname(), "Last name should be Bafna");
        assertEquals("ritubafna@abc_school.com", gradebookCollegeStudent.getEmailAddress() , "Email address should be Ritu");
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1, "Math grade should have 1 grade result");
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1, "Science grade should have 1 grade result");
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1, "History grade should have 1 grade result");
    }

    @Test
    public void studentInformationReturnNullTest(){
        assertNull(studentService.getInformation(0));
    }

}
