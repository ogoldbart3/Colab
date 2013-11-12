package edu.gatech.mas.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.model.Course;
import edu.gatech.mas.model.Student;

public class ClassGetStudents extends AsyncTask<String, Integer, List<Course>> {
	
	private IClassCallback mCallback;

	public ClassGetStudents(IClassCallback callback) {
		mCallback = callback;
	}
	
	List<Course> courses = new ArrayList<Course>();
	
	@Override
	protected List<Course> doInBackground(String... params) {

		
		HttpClient mClient = new DefaultHttpClient();

		String userId = params[0];
		String apiCourses = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
				+ userId + "/course/";

		List<Course> resultCourses = new LinkedList<Course>();
		try {
			HttpGet request = new HttpGet();
			for (Course course : courses) {
				String finalUrl = apiCourses + course.getId() + "/friend";
				URI finalAPI = new URI(finalUrl);

				request.setURI(finalAPI);
				request.setHeader("Cookie", ClassListActivity.getSessionName() + "=" + ClassListActivity.getSessionId());

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
						student.setUsername(jsonObject.getString("studentGt"));
						student.setFirstName(jsonObject
								.getString("studentFirst"));
						student.setLastName(jsonObject.getString("studentLast"));
						student.setPhone(jsonObject.getString("studentPhone"));
						student.setAbout(jsonObject.getString("aboutMe"));

						switch (Integer
								.parseInt(jsonObject.getString("status"))) {
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
		return resultCourses;
	}

	@Override
	protected void onPostExecute(List<Course> result) {
		mCallback.setStudentsForCourse(result);
	}


	public void setCourses(List<Course> mCourses) {
		this.courses = mCourses;
	}
}