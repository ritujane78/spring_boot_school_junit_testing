package com.jane.springmvc.repository;

import com.jane.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradeDAO extends CrudRepository<MathGrade, Integer> {
    public Iterable<MathGrade> findGradeByStudentId(int studentId);

    public void deleteByStudentId(int studentId);
}
