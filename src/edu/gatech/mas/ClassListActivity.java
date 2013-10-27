package edu.gatech.mas;

import java.io.UnsupportedEncodingException;
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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.gatech.mas.interfaces.IAppManager;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.GPSLocationService;
import edu.gatech.mas.service.IMService;
import edu.gatech.mas.tools.FriendController;

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

	private FriendInfo[] friends = null;

	private DefaultHttpClient mClient;

	private String sessionName;
	private String sessionId;

	public static final String apiUsername = "http://dev.m.gatech.edu/user/";
	public static final String apiUid = "http://dev.m.gatech.edu/d/hpun3/w/colab/c/api/user/";

	private String mUsername;

	private IAppManager mService = null;

	private boolean isReceiverRegistered;

	public MessageReceiver messageReceiver = new MessageReceiver();

	private List<Course> mCourses = new ArrayList<Course>();

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((IMService.IMBinder) service).getService();

			// Currently we have two models for a student - Student and Friend.
			// Later on Friend has to be merged with Student. Currently all info
			// is in Friend.
			FriendInfo[] friends = FriendController.getFriendsInfo(); // imService.getLastRawFriendList();
			if (friends != null) {
				ClassListActivity.this.updateData(friends); // parseFriendInfo(friendList);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			Toast.makeText(ClassListActivity.this,
					R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
		}
	};
	private int uid;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(new Intent(ClassListActivity.this, IMService.class));
		bindService(new Intent(ClassListActivity.this, IMService.class),
				mConnection, Context.BIND_AUTO_CREATE);
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
	}

	@Override
	protected void onPause() {
		if (isReceiverRegistered) {
			try {
				unregisterReceiver(messageReceiver);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Unable to deregister device!");
			}
			isReceiverRegistered = false;
		}
		unbindService(mConnection);
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();

		/**
		 * Get username from server and set it as title in action bar
		 */
		new FetchUserName().execute();
	}

	public void updateData(FriendInfo[] friends) {
		if (friends != null) {
			setFriendList(friends);
			mClassListPagerAdapter.setCourseList(friends);
		}
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i("Broadcast receiver ", "received a message");
			Bundle extra = intent.getExtras();
			if (extra != null) {
				String action = intent.getAction();
				if (action.equals(IMService.FRIEND_LIST_UPDATED)) {

					for (FriendInfo friend : FriendController.getFriendsInfo()) {
						System.out.println("friend: " + friend.userName
								+ ", status: " + friend.status.ordinal());
					}

					// taking friend List from broadcast
					ClassListActivity.this.updateData(FriendController
							.getFriendsInfo());
				}
			}
		}
	};

	public class FetchUId extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			mClient = new DefaultHttpClient();
			String result = "";
			try {
				URI api = new URI(apiUid);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", sessionName + "=" + sessionId);
				System.out.println("Cookie uid: " + sessionName + "="
						+ sessionId);

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);

				try {
					JSONObject jsonObject = new JSONObject(result);
					uid = Integer.valueOf(jsonObject.getString("userid"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				System.out.println("UID: " + uid);
				// setUId(Integer.parseInt(result));
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			new FetchCourses().execute();
		}

	}

	public class FetchUserName extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			mClient = new DefaultHttpClient();
			String result = "";
			try {
				URI api = new URI(apiUsername);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", sessionName + "=" + sessionId);
				System.out.println("Cookie: " + sessionName + "=" + sessionId);

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				setUsername(result);
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
				actionBar.setTitle(result);
			}

			System.out.println("Getting student id ... ");
			new FetchUId().execute();

			Thread loginThread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					String result = null;
					try {

						if (mService != null) {
							result = mService.authenticateUser("testt");
						} else
							System.out.println("Service is null");
					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}
					if (result == null) {
						/*
						 * Authentication failed, inform the user
						 */
						handler.post(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Students list is null",
										Toast.LENGTH_SHORT).show();
							}
						});

					} else {

						/*
						 * If authentication was successful, get the list of
						 * other students in the class
						 */
						handler.post(new Runnable() {
							public void run() {
								Toast.makeText(
										getApplicationContext(),
										"Receving list of students in the class ...",
										Toast.LENGTH_SHORT).show();
								IntentFilter i = new IntentFilter();
								i.addAction(IMService.FRIEND_LIST_UPDATED);
								registerReceiver(messageReceiver, i);
								isReceiverRegistered = true;
							}
						});
					}
				}
			};
			loginThread.start();
		}
	}

	public class FetchCourses extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			mClient = new DefaultHttpClient();

			String apiCourses = "http://dev.m.gatech.edu/d/hpun3/w/colab/c/api/user/"
					+ uid + "/course";

			try {
				URI api = new URI(apiCourses);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", sessionName + "=" + sessionId);

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				String str = EntityUtils.toString(entity);
				try {

					JSONArray JsonArrayForResult = new JSONArray(str);

					for (int i = 0; i < JsonArrayForResult.length(); i++) {
						JSONObject jsonObject = JsonArrayForResult
								.getJSONObject(i);
						Course course = new Course();
						course.setName(jsonObject.getString("courseName"));
						course.setId(Integer.valueOf(jsonObject
								.getString("courseId")));
						mCourses.add(course);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				for (Course c : mCourses) {
					System.out.println(c.toString());
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

			String apiCourses = "http://dev.m.gatech.edu/d/hpun3/w/colab/c/api/user/"
					+ uid + "/course/";

			try {
				HttpGet request = new HttpGet();
				for (Course course : mCourses) {
					String finalUrl = apiCourses + course.getId() + "/friend";
					System.out.println("Final api: " + finalUrl);
					URI finalAPI = new URI(finalUrl);

					request.setURI(finalAPI);
					request.setHeader("Cookie", sessionName + "=" + sessionId);

					HttpResponse response = mClient.execute(request);
					HttpEntity entity = response.getEntity();
					String str = EntityUtils.toString(entity);
					System.out.println("response: " + str);
					try {

						List<Student> studentsArray = new ArrayList<Student>();
						JSONArray JsonArrayForResult = new JSONArray(str);

						for (int i = 0; i < JsonArrayForResult.length(); i++) {
							JSONObject jsonObject = JsonArrayForResult
									.getJSONObject(i);
							Student student = new Student();
							student.setFirstName(jsonObject
									.getString("studentFirst"));
							student.setLastName(jsonObject
									.getString("studentLast"));
							studentsArray.add(student);
						}
						course.setStudents(studentsArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				for (Course course : mCourses) {
					System.out.println(course.getId() + " , courseName: " + course.getName());
					for (Student student : course.getStudents()) {
						System.out.println("\tStudent: " + student.getFirstName() + " " + student.getLastName());
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
		}
	}

	public void setFriendList(FriendInfo[] friends) {
		this.friends = friends;
	}

	public int getCount() {

		return friends.length;
	}

	public FriendInfo getItem(int position) {

		return friends[position];
	}

	public long getItemId(int position) {

		return 0;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		this.mUsername = username;
	}

	private void setUId(int result) {
		this.uid = result;
	}

}
