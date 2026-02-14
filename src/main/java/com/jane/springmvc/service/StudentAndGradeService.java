package com.jane.springmvc.service;

import com.jane.springmvc.models.*;
import com.jane.springmvc.repository.HistoryGradeDAO;
import com.jane.springmvc.repository.MathGradeDAO;
import com.jane.springmvc.repository.ScienceGradeDAO;
import com.jane.springmvc.repository.StudentDAO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {
    @Autowired
    StudentDAO studentDAO;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    private MathGradeDAO mathGradeDAO;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradeDAO scienceGradeDAO;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private HistoryGradeDAO historyGradeDAO;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstName, String lastName, String emailAddress) {
        CollegeStudent collegeStudent = new CollegeStudent(firstName, lastName, emailAddress);
//        collegeStudent.setId(1);
        studentDAO.save(collegeStudent);
    }

    public boolean checkIfStudentIsPresent(int id) {
        Optional<CollegeStudent> collegeStudent = studentDAO.findById(id);
        if(collegeStudent.isPresent()){
            return true;
        }
        return false;
    }

    public void deleteStudent(int id) {
        if(checkIfStudentIsPresent(id)){
            studentDAO.deleteById(id);
            mathGradeDAO.deleteByStudentId(id);
            scienceGradeDAO.deleteByStudentId(id);
            historyGradeDAO.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudentIterable = studentDAO.findAll();
        return collegeStudentIterable;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if(!checkIfStudentIsPresent(studentId)){
            return false;
        }
        if(grade > 0 && grade <=100){
            if(gradeType.equals("math")){
                mathGrade = new MathGrade();
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDAO.save(mathGrade);
                return true;
            }
            if(gradeType.equals("science")){
                scienceGrade = new ScienceGrade();
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradeDAO.save(scienceGrade);
                return true;
            }
            if(gradeType.equals("history")){
                historyGrade = new HistoryGrade();
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                historyGradeDAO.save(historyGrade);
                return true;
            }
        }
        return false;
    }
    public  int deleteGrade(int id, String gradeType) {
        int studentId = 0;
        if(gradeType.equals("math")){
            Optional<MathGrade> grade = mathGradeDAO.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            mathGradeDAO.deleteById(id);
        } else if(gradeType.equals("science")){
            Optional<ScienceGrade> grade = scienceGradeDAO.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            scienceGradeDAO.deleteById(id);
        } else if(gradeType.equals("history")){
            Optional<HistoryGrade> grade = historyGradeDAO.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            historyGradeDAO.deleteById(id);
        }
        return  studentId;
    }

    public GradebookCollegeStudent getInformation(int id) {
        if (!checkIfStudentIsPresent(id)) {
            return null;
        }
        Optional<CollegeStudent> collegeStudent = studentDAO.findById(id);

        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(id);

        List<Grade>  mathGradesList = new ArrayList<>();
        List<Grade> scienceGradesList = new ArrayList<>();
        List<Grade> historyGradesList = new ArrayList<>();
        mathGrades.forEach(mathGradesList::add);
        scienceGrades.forEach(scienceGradesList::add);
        historyGrades.forEach(historyGradesList::add);

        studentGrades.setMathGradeResults(mathGradesList);
        studentGrades.setScienceGradeResults(scienceGradesList);
        studentGrades.setHistoryGradeResults(historyGradesList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(collegeStudent.get().getId(),
                collegeStudent.get().getFirstname(), collegeStudent.get().getLastname(), collegeStudent.get().getEmailAddress(), studentGrades);

        return gradebookCollegeStudent;



    }
    public void configureStudentInformation(int id, Model m){
        GradebookCollegeStudent gradebookCollegeStudent = getInformation(id);
        m.addAttribute("student", gradebookCollegeStudent);
        if(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getMathGradeResults()));
        }else {
            m.addAttribute("mathAverage", "N/A");
        }
        if(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()));
        } else {
            m.addAttribute("scienceAverage", "N/A");
        }
        if (gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() > 0){
            m.addAttribute("historyAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()));
        }else {
            m.addAttribute("historyAverage", "N/A");
        }
    }
}
