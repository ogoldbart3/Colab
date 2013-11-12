package edu.gatech.mas.api;

import java.net.URI;
import java.util.ArrayList;
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

import android.os.AsyncTask;
import android.util.Log;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.model.Course;

public class ClassGetCourses extends AsyncTask<String, Integer, List<Course>> {

	private IClassCallback mCallback;

	public ClassGetCourses(IClassCallback callback) {
		mCallback = callback;
	}

	String uid;

	@Override
	protected List<Course> doInBackground(String... params) {

		HttpClient mClient = new DefaultHttpClient();

		uid = params[0];
		String apiCourses = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
				+ uid + "/course";

		List<Course> courses = new ArrayList<Course>();
		try {
			URI api = new URI(apiCourses);
			HttpGet request = new HttpGet();
			request.setURI(api);
			request.setHeader("Cookie", ClassListActivity.getSessionName()
					+ "=" + ClassListActivity.getSessionId());

			HttpResponse response = mClient.execute(request);
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
			try {

				JSONArray JsonArrayForResult = new JSONArray(str);

				for (int i = 0; i < JsonArrayForResult.length(); i++) {
					JSONObject jsonObject = JsonArrayForResult.getJSONObject(i);
					Course course = new Course();
					course.setName(jsonObject.getString("courseName"));
					course.setId(Integer.valueOf(jsonObject
							.getString("courseId")));
					courses.add(course);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection: " + e.toString());
			e.printStackTrace();
		}
		return courses;
	}

	@Override
	protected void onPostExecute(List<Course> result) {
		ClassGetStudents fs = new ClassGetStudents(mCallback);
		fs.setCourses(result);
		fs.execute(String.valueOf(uid));
		mCallback.setCourses(result);
	}
}