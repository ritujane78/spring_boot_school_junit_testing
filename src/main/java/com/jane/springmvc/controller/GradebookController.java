package com.jane.springmvc.controller;

import com.jane.springmvc.models.CollegeStudent;
import com.jane.springmvc.models.Gradebook;
import com.jane.springmvc.models.GradebookCollegeStudent;
import com.jane.springmvc.repository.StudentDAO;
import com.jane.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	StudentAndGradeService studentAndGradeService;



	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@PostMapping("/")
	public  String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
		studentAndGradeService.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id, Model m) {
		if(!studentAndGradeService.checkIfStudentIsPresent(id)){
			return "error";
		}
		studentAndGradeService.deleteStudent(id);
		Iterable<CollegeStudent> students = studentAndGradeService.getGradebook();
		m.addAttribute("students", students);
		return "index";

	}


	@GetMapping("/studentInformation/{id}")
		public String studentInformation(@PathVariable int id, Model m) {
			if(!studentAndGradeService.checkIfStudentIsPresent(id)){
				return "error";
			}
			studentAndGradeService.configureStudentInformation(id, m);

			return "studentInformation";
		}


		@PostMapping("/grades")
	private String createGrades(@RequestParam("grade") double grade,
							  @RequestParam("gradeType") String gradeType,
							  @RequestParam("studentId")int id, Model m){
			if(!studentAndGradeService.checkIfStudentIsPresent(id)){
				return "error";
			}
			boolean success = studentAndGradeService.createGrade(grade,id, gradeType);

			if(!success){
				return "error";
			}
			studentAndGradeService.configureStudentInformation(id, m);


			return "studentInformation";
		}

		@GetMapping("/grades/{id}/{gradeType}")
		public String deleteGrades(@PathVariable int id, @PathVariable String gradeType, Model m){
			int studentId = studentAndGradeService.deleteGrade(id, gradeType);
			if(studentId == 0){
				return "error";
			}

			studentAndGradeService.configureStudentInformation(studentId, m);

			return "studentInformation";
		}

}
