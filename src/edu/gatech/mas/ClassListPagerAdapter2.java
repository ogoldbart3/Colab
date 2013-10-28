package edu.gatech.mas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.Status;
import edu.gatech.mas.model.Student;

/**
 * An adapter class that returns a ClassObjectFragment, representing an object
 * in the collection.
 * 
 * @author Pawel
 */
class ClassListPagerAdapter2 extends FragmentStatePagerAdapter {

	private List<Course> courseList;
	
	private Student user;

	public ClassListPagerAdapter2(FragmentManager fm) {
		super(fm);
		courseList = new ArrayList<Course>();
		user = new Student();
	}

	@Override
	public Fragment getItem(int i) {
		return ClassListFragment.newInstance(courseList.get(i), user);
	}

	@Override
	public int getItemPosition(Object item) {
		Fragment f = (ClassListFragment) item;
		
		int position = 0;
		
		if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
	}
	
	@Override
	public int getCount() {
		return courseList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return courseList.get(position).getName();
	}
	
	void setCourseList(List<Course> courses)
	{
		this.courseList = courses;
	}

}