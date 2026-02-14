package com.jane.springmvc.repository;

import com.jane.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradeDAO extends CrudRepository<ScienceGrade, Integer> {
    public Iterable<ScienceGrade> findGradeByStudentId(int studentId);
    public void deleteByStudentId(int studentId);
}
