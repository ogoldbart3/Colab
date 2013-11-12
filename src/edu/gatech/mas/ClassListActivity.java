package edu.gatech.mas;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
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
 * Class that contains a tab swipe activity with classes of a logged in student.
 * 
 * @author Pawel
 */
public class ClassListActivity extends FragmentActivity implements
		IClassCallback {

	private static String TAG = "ClassListActivity";
	/**
	 * This adapter returns a ClassObjectFragment, representing an object in the
	 * collection/
	 */
	private ClassListPagerAdapter mClassListPagerAdapter;
	/**
	 * View pager for tabs.
	 */
	private ViewPager mViewPager;

	private Button mLocationButton;
	private Button mStatusButton;
	private Button mChatButton;

	private DefaultHttpClient mClient;

	private static Student mUser;

	private List<Course> mCourses = new ArrayList<Course>();

	private static String sessionName;
	private static String sessionId;
	private static boolean alreadyFetched = false;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public ClassListActivity() {
		mUser = new Student();
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
				mStatusButton.setText("test");
				System.out.println(R.string.status_online);
				if (mStatusButton.getText().equals("Status: offline"))
					mStatusButton.setText("Status: online");
				else
					mStatusButton.setText("Status: offline");

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
		 * Get username from server and set it as title in action bar
		 */
		new ClassGetUser(ClassListActivity.this).execute();
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


	@Override
	public void setStudentsForCourse(List<Course> result) {
		mClassListPagerAdapter.setCourseList(mCourses);
		mClassListPagerAdapter.notifyDataSetChanged();
		alreadyFetched = true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void setUserInfo(Student user) {
		mUser = user;
		
		mClassListPagerAdapter.setUser(mUser);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setTitle("Hello " + mUser.getFirstName() + "!");
		}

		if (alreadyFetched)
			mClassListPagerAdapter.notifyDataSetChanged();
		
		new ClassGetCourses(ClassListActivity.this).execute(String
				.valueOf(mUser.getUid()));
	}

	@Override
	public void setCourses(List<Course> courses) {
		this.mCourses = courses;
	}

}
