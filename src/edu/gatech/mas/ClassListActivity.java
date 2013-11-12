package edu.gatech.mas;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.mas.api.ClassGetCourses;
import edu.gatech.mas.api.ClassGetUser;
import edu.gatech.mas.api.IClassCallback;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.service.GPSLocationService;

/**
 * Class responsible for displaying all classes of the user. Each class contains
 * list of students that are also taking that class. User can set up his
 * visibility for each class (visible/invisible), also check-in into a new
 * location where he studies e.g. library.
 * 
 * @author Pawel
 */
public class ClassListActivity extends FragmentActivity implements
		IClassCallback {

	/**
	 * Adapter for a ClassObjectFragment, representing a course object in the
	 * collection.
	 */
	private ClassListPagerAdapter mClassListPagerAdapter;

	/**
	 * View pager for tabs.
	 */
	private ViewPager mViewPager;

	private Button mLocationButton;
	private Button mStatusButton;
	private Button mChatButton; // TODO Pawel: implement group chat

	private static Student mUser;
	private List<Course> mCourses = new ArrayList<Course>();

	/** Session name returned after successful authentication to CAS system */
	private static String sessionName;
	/** Session id, essential to make valid calls t API */
	private static String sessionId;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_class_list);

		Intent intent = getIntent();

		// Get session data used for communicating with API
		Uri data = intent.getData();
		sessionName = data.getQueryParameter("sessionName");
		sessionId = data.getQueryParameter("sessionId");

		// initialize pager adapter
		mClassListPagerAdapter = new ClassListPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mClassListPagerAdapter);

		mLocationButton = (Button) findViewById(R.id.enableLocationButton);
		mLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getBaseContext().startService(
						new Intent(ClassListActivity.this,
								GPSLocationService.class));
			}
		});

		mStatusButton = (Button) findViewById(R.id.chooseStatusButton);
		mStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				;
				if (mStatusButton.getText().equals(getResources().getString(R.string.status_offline)))
					mStatusButton.setText(getResources().getString(R.string.status_online));
				else
					mStatusButton.setText(getResources().getString(R.string.status_offline));

			}
		});

		/**
		 * TODO Pawel: add notifications about incoming new chat messages
		 */
		startService(new Intent(ClassListActivity.this, ChatService.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
		mClassListPagerAdapter.notifyDataSetChanged();

		/**
		 * Get information about current user from server.
		 */
		new ClassGetUser(ClassListActivity.this).execute();
	}

	@Override
	public void updateCoursesWithStudents(List<Course> result) {
		mClassListPagerAdapter.setCourseList(mCourses);
		mClassListPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void setUser(Student user) {
		mUser = user;
		setTitle();

		// update fragment with a new data
		mClassListPagerAdapter.setUser(user);
		mClassListPagerAdapter.notifyDataSetChanged();

		// after receiving user information, get his classes
		new ClassGetCourses(ClassListActivity.this).execute(String.valueOf(user
				.getUid()));
	}

	@Override
	public void setCourses(List<Course> courses) {
		this.mCourses = courses;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void setTitle() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
				&& mUser != null) {
			ActionBar actionBar = getActionBar();
			actionBar.setTitle("Hello " + mUser.getFirstName() + "!");
		}
	}

	public static String getSessionName() {
		return sessionName;
	}

	public static String getSessionId() {
		return sessionId;
	}

	public static Student getCurrentUser() {
		return mUser;
	}
}
