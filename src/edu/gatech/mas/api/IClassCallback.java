package edu.gatech.mas.api;

import java.util.List;

import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;

/**
 * Interface responsible for passing a result of AsyncTask to calling class.
 * 
 * @author Pawel
 */
public interface IClassCallback {

	/**
	 * Set user of the app.
	 * @param user Georgia Tech student.
	 */
	void setUser(Student user);

	/**
	 * Set courses of a given student.
	 * @param courses lsit of courses
	 */
	void setCourses(List<Course> courses);

	/**
	 * Set students participating in same classes as user
	 * @param courses
	 */
	void updateCoursesWithStudents(List<Course> courses);
}
