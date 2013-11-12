package edu.gatech.mas.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import edu.gatech.mas.ClassListActivity;

/**
 * AsyncTask responsible for posting a chat message to db.
 * 
 * @author Pawel
 * 
 */
public class ChatPostMessage extends AsyncTask<String, String, String> {

	/** Callback activity */
	private IChatCallback mCallback;

	public ChatPostMessage(IChatCallback callback) {
		mCallback = callback;
	}

	@Override
	protected String doInBackground(String... params) {

		String message = params[0];
		String senderId = params[1];
		String receiverId = params[2];
		HttpClient httpclient = new DefaultHttpClient();
		String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
				+ senderId + "/chat/" + receiverId;

		System.out.println("Sending message:  " + message + ", to: " + api);
		try {
			// Add your data
			URI uri = new URI(api);
			HttpPost httppost = new HttpPost(uri);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs
					.add(new BasicNameValuePair("message", message.trim()));

			httppost.setHeader("Cookie", ClassListActivity.getSessionName()
					+ "=" + ClassListActivity.getSessionId());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println("result of posting to db: " + result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return message;
	}

	@Override
	protected void onPostExecute(String result) {
		mCallback.displayChatMessage(result);
	}
}
