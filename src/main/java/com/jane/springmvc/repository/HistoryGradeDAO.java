package com.jane.springmvc.repository;

import com.jane.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDAO extends CrudRepository<HistoryGrade, Integer> {
    public Iterable<HistoryGrade> findGradeByStudentId(int studentId);
    public void deleteByStudentId(int studentId);
}
