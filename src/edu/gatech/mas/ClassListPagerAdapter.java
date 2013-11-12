package edu.gatech.mas;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;

/**
 * An adapter class that returns a ClassObjectFragment, representing an object
 * in the collection.
 * 
 * @author Pawel
 */
class ClassListPagerAdapter extends FragmentStatePagerAdapter {

	/** List of courses displayed on separate pages */
	private List<Course> mCourseList;

	/** Current user of the app */
	private Student mUser;

	public ClassListPagerAdapter(FragmentManager fm) {
		super(fm);
		mCourseList = new ArrayList<Course>();
		mUser = new Student();
	}

	@Override
	public Fragment getItem(int i) {
		return ClassListFragment.newInstance(mCourseList.get(i), mUser);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mCourseList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mCourseList.get(position).getName();
	}

	void setCourseListWithStudents(List<Student> students) {
		Course c = new Course();
		c.setStudents(students);
		mCourseList.set(0, c);
	}

	void setCourseList(List<Course> courses) {
		this.mCourseList = courses;
	}

	public void setUser(Student user) {
		this.mUser = user;
	}

	// TODO Pawel: implement update students of a class
	public void setStudents(List<Student> students) {

	}

}