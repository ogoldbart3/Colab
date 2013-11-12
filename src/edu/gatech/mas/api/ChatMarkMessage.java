package edu.gatech.mas.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import edu.gatech.mas.ClassListActivity;

public class ChatMarkMessage extends AsyncTask<Integer, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(Integer... params) {
		int messageId = params[0];
		int uid = params[1];
		int receiverId = params[2];
		HttpClient httpClient = new DefaultHttpClient();
		String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"+uid+"/chat/"+
				+ receiverId;

		try {
			// Add your data
			URI uri = new URI(api);
			HttpPost httppost = new HttpPost(uri);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("messageRead", String
					.valueOf(messageId)));

			httppost.setHeader("Cookie", ClassListActivity.getSessionName()
					+ "=" + ClassListActivity.getSessionId());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			httpClient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}

}
