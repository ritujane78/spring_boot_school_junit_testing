package com.jane.springmvc;

import com.jane.springmvc.models.CollegeStudent;
import com.jane.springmvc.models.GradebookCollegeStudent;
import com.jane.springmvc.models.MathGrade;
import com.jane.springmvc.repository.MathGradeDAO;
import com.jane.springmvc.repository.StudentDAO;
import com.jane.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;
    @Autowired
    private CollegeStudent collegeStudent;

    @Autowired
    private static MockHttpServletRequest request;

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private MathGradeDAO mathGradeDAO;

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

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();

        request.setParameter("firstName", "Jane");
        request.setParameter("lastName", "Test");
        request.setParameter("emailAddress", "janetest@abc_school.com");
    }

    @BeforeEach
    public void beforeEach() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }
    @AfterEach
    public void tearDown() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void getStudentHTTPrRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Ritu", "Bafna", "ritubafna@abc_school.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Jane", "Test", "janetest@abc_school.com");
        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(students);

        assertIterableEquals(students, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @Test
    public void createStudentHTTPRequest() throws Exception {
        CollegeStudent studentOne = new CollegeStudent("Ritu", "Bafna", "ritubafna@abc_school.com");
        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne));
        when(studentCreateServiceMock.getGradebook()).thenReturn(students);

        assertIterableEquals(students, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", request.getParameter("firstname"))
                        .param("lastname", request.getParameter("lastname"))
                        .param("emailAddress", request.getParameter("emailAddress")))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent student = studentDAO.findByEmailAddress("janetest@abc_school.com");

        assertNotNull(student, "Student should not be null");
    }
    @Test
    public void deleteStudent() throws Exception {
        Optional<CollegeStudent> student = studentDAO.findById(1);
        assertTrue(student.isPresent(), "Student not found");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}", 1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        assertFalse(studentDAO.findById(1).isPresent(), "Student not found");

    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void getStudentInformationHTTPRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent(), "Student not found");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/studentInformation/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");
    }

    @Test
    public void getStudentInformationHTTPRequestErrorPage() throws Exception {
        assertFalse(studentDAO.findById(0).isPresent(), "Student found");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/studentInformation/{id}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createValidGradeHTTPRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent(), "Student not found");

        GradebookCollegeStudent student = studentAndGradeService.getInformation(1);

        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.50")
                        .param("gradeType", "math")
                        .param("studentId", "1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        student = studentAndGradeService.getInformation(1);

        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());

    }

    @Test
    public void createValidGradeHTTPRequestErrorPage() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.50")
                        .param("gradeType", "math")
                        .param("studentId", "0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");

    }

    @Test
    public void createInValidGradeWhenGradeTypeDoesntExistHTTPRequest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.50")
                        .param("gradeType", "literature")
                        .param("studentId", "0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");

    }

    @Test
    public void deleteValidGradeHTTPRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDAO.findById(1);

        assertTrue(mathGrade.isPresent(), "MathGrade not found");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        mathGrade = mathGradeDAO.findById(1);

        assertFalse((mathGrade.isPresent()), "MathGrade not found");

    }

    @Test
    public void deleteValidGradeHTTPRequestGradeIdDoesntExist() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDAO.findById(2);
        assertFalse((mathGrade.isPresent()), "MathGrade found");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/grades/{id}/{gradeType}", 2, "math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void deleteInvalidGradeTypeHTTPRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/grades/{id}/{gradeType}", 1, "literature"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }
}
