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
class ClassListPagerAdapter extends FragmentStatePagerAdapter {

	private List<Course> courseList;
	private FriendInfo[] mFriends;

	public ClassListPagerAdapter(FragmentManager fm) {
		super(fm);
		courseList = new ArrayList<Course>();
		generateRandomCourses(); // TODO: replace that with courses from
									// T-Square
	}

	private void generateRandomCourses() {
		for (int i = 0; i < 6; i++) {
			Course c = new Course();
			c.setName("Course " + i);
			for (int j = 0; j <= i + 1; j++) {
				Student s = new Student();
				s.setStatus(Status.ONLINE);
				s.setUsername(generateString());
				c.getStudents().add(s);

			}
			courseList.add(c);
		}
	}

	public static String generateString() {
		Random random = new Random();
		String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int length = 6;
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(random.nextInt(characters.length()));
		}
		return new String(text);
	}

	@Override
	public Fragment getItem(int i) {
		return ClassListFragment.newInstance(courseList.get(i));
	}

	@Override
	public int getCount() {
		return courseList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return courseList.get(position).getName();
	}

	void setCourseList(FriendInfo[] friends) {
		for (Course courInfo : courseList) {
			for (int i = 0; i < friends.length; i++) {
				if (courInfo.getStudents().size() <= i)
					break;
				Student currentStudent= courInfo.getStudents().get(i);
				currentStudent.setUsername(friends[i].userName);
				currentStudent.setIp(friends[i].ip);
				currentStudent.setPort(friends[i].port);
			}
		}
	}

}