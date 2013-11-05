package edu.gatech.mas;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.service.GPSLocationService;

/**
 * Class that contains a tab swipe activity with classes of a logged in student.
 * 
 * @author Pawel
 */
public class ClassListActivity extends FragmentActivity {

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

	private static List<Course> mCourses = new ArrayList<Course>();

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

		// Get session data later used for communicating with API
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
		 * TODO: add notifications about incoming new messages to chat service
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
		new FetchUserName().execute();
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

	public class FetchUserName extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			String apiUser = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/";
			mClient = new DefaultHttpClient();
			String result = "";
			try {
				URI api = new URI(apiUser);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", sessionName + "=" + sessionId);

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);

				try {
					JSONObject jsonObject = new JSONObject(result);
					mUser.setUid(Integer.valueOf(jsonObject
							.getString("studentId")));
					System.out.println("UID 2: "
							+ jsonObject.getString("studentId"));
					mUser.setUsername(jsonObject.getString("studentGt"));
					mUser.setFirstName(jsonObject.getString("studentFirst"));
					mUser.setLastName(jsonObject.getString("studentLast"));
					mUser.setPhone(jsonObject.getString("studentPhone"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				mClassListPagerAdapter.setUser(mUser);
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
				e.printStackTrace();
			}

			return result;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onPostExecute(String result) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Hello " + mUser.getFirstName() + "!");
			}

			if (alreadyFetched)
				mClassListPagerAdapter.notifyDataSetChanged();
			new FetchCourses().execute();

		}
	}

	public class FetchCourses extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			mClient = new DefaultHttpClient();

			String apiCourses = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
					+ mUser.getUid() + "/course";

			try {
				URI api = new URI(apiCourses);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", sessionName + "=" + sessionId);

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				String str = EntityUtils.toString(entity);
				List<Course> courses = new ArrayList<Course>();
				try {

					JSONArray JsonArrayForResult = new JSONArray(str);

					for (int i = 0; i < JsonArrayForResult.length(); i++) {
						JSONObject jsonObject = JsonArrayForResult
								.getJSONObject(i);
						Course course = new Course();
						course.setName(jsonObject.getString("courseName"));
						course.setId(Integer.valueOf(jsonObject
								.getString("courseId")));
						courses.add(course);
					}
					mCourses = courses;
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			new FetchStudentsOfCourse().execute();
		}
	}

	public class FetchStudentsOfCourse extends
			AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			mClient = new DefaultHttpClient();

			String apiCourses = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
					+ mUser.getUid() + "/course/";

			try {
				HttpGet request = new HttpGet();
				for (Course course : mCourses) {
					String finalUrl = apiCourses + course.getId() + "/friend";
					URI finalAPI = new URI(finalUrl);

					request.setURI(finalAPI);
					request.setHeader("Cookie", sessionName + "=" + sessionId);

					HttpResponse response = mClient.execute(request);
					HttpEntity entity = response.getEntity();
					String str = EntityUtils.toString(entity);
					try {

						List<Student> studentsArray = new ArrayList<Student>();
						JSONArray JsonArrayForResult = new JSONArray(str);

						for (int i = 0; i < JsonArrayForResult.length(); i++) {
							JSONObject jsonObject = JsonArrayForResult
									.getJSONObject(i);
							Student student = new Student();
							student.setUid(Integer.parseInt(jsonObject
									.getString("studentId")));
							student.setUsername(jsonObject
									.getString("studentGt"));
							student.setFirstName(jsonObject
									.getString("studentFirst"));
							student.setLastName(jsonObject
									.getString("studentLast"));
							student.setPhone(jsonObject
									.getString("studentPhone"));
							student.setAbout(jsonObject.getString("aboutMe"));

							switch (Integer.parseInt(jsonObject
									.getString("status"))) {
							case 2:
								student.setStatus(edu.gatech.mas.model.Status.AWAY);
								break;
							case 1:
								student.setStatus(edu.gatech.mas.model.Status.ONLINE);
								break;
							default:
								student.setStatus(edu.gatech.mas.model.Status.OFFLINE);
								break;
							}
							Location loc = new Location("dummyProvider");
							loc.setLatitude(33.77);
							loc.setLongitude(-84.38);
							student.setLocation(loc);
							studentsArray.add(student);
						}
						course.setStudents(studentsArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mClassListPagerAdapter.setCourseList(mCourses);
			mClassListPagerAdapter.notifyDataSetChanged();
			alreadyFetched = true;
		}
	}

}
