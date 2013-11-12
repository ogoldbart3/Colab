package edu.gatech.mas.api;

import java.util.List;

import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;

public interface IClassCallback {

	void setUserInfo(Student user);
	void setCourses(List<Course> courses);
	void setStudentsForCourse(List<Course> courses);
}
