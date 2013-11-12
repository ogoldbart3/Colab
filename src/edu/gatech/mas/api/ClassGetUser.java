package edu.gatech.mas.api;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.model.Student;

public class ClassGetUser extends AsyncTask<String, Integer, Student> {
	private IClassCallback mCallback;

	public ClassGetUser(IClassCallback callback) {
		mCallback = callback;
	}

	@Override
	protected Student doInBackground(String... params) {

		String apiUser = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/";
		HttpClient mClient = new DefaultHttpClient();
		String result = "";
		Student student = new Student();
		try {
			URI api = new URI(apiUser);
			HttpGet request = new HttpGet();
			request.setURI(api);
			request.setHeader("Cookie", ClassListActivity.getSessionName()
					+ "=" + ClassListActivity.getSessionId());

			HttpResponse response = mClient.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);

			try {
				JSONObject jsonObject = new JSONObject(result);
				student.setUid(Integer.valueOf(jsonObject
						.getString("studentId")));
				System.out.println("UID 2: "
						+ jsonObject.getString("studentId"));
				student.setUsername(jsonObject.getString("studentGt"));
				student.setFirstName(jsonObject.getString("studentFirst"));
				student.setLastName(jsonObject.getString("studentLast"));
				student.setPhone(jsonObject.getString("studentPhone"));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection: " + e.toString());
			e.printStackTrace();
		}
		return student;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onPostExecute(Student result) {
		mCallback.setUserInfo(result);
	}
}