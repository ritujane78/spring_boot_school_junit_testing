package com.jane.springmvc.repository;

import com.jane.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;

public interface StudentDAO extends CrudRepository<CollegeStudent, Integer> {
    public CollegeStudent findByEmailAddress(String emailAddress);
}
