package edu.gatech.mas.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.model.Message;

/**
 * Service that gets the chat messages.
 * 
 * @author Pawel
 * 
 */
public class ChatService extends Service {

	public static final String TAKE = "Take_Message";
	private static Timer timer = new Timer();
	private Context ctx;

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		ctx = this;
		startService();
	}

	private void startService() {
		timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
	}

	private class mainTask extends TimerTask {
		public void run() {
			new FetchMessage().execute();
		}
	}

	public class FetchMessage extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			HttpClient mClient = new DefaultHttpClient();
			String result = "";
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("type", "unread"));
				String parameterString = URLEncodedUtils.format(nameValuePairs,
						"utf-8");

				URI api = new URI(
						"http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
								+ ClassListActivity.getCurrentUser().getUid()
								+ "/chat?" + parameterString);
				HttpGet request = new HttpGet();
				request.setURI(api);
				request.setHeader("Cookie", ClassListActivity.getSessionName()
						+ "=" + ClassListActivity.getSessionId());

				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);

				ArrayList<Message> messages = parseMessage(result);
				
				if (messages != null && messages.size() > 0) {
					broadCastMessage(messages);
				}

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
				e.printStackTrace();
			}

			return result;
		}
	}

	private void broadCastMessage(ArrayList<Message> messages) {
		for (Message message : messages) {
			Intent i = new Intent(TAKE);
			i.putExtra("test", "test");
			i.putExtra(Message.MESSAGE_TAG, message);
			sendBroadcast(i);
			System.out.println("Broadcasting messages: " + message.toString());	
		}

		/*
		 * String activeFriend = FriendController.getActiveFriend(); if
		 * (activeFriend == null || activeFriend.equals(username) == false) {
		 * localstoragehandler.insert(username,this.getUsername(),
		 * message.toString()); showNotification(username, message); }
		 */
	}

	private ArrayList<Message> parseMessage(String result) {
		ArrayList<Message> newMessages = new ArrayList<Message>();
		try {

			JSONArray JsonArrayForResult = new JSONArray(result);
			for (int i = 0; i < JsonArrayForResult.length(); i++) {
				Message newMessage = new Message();
				JSONObject jsonObject = JsonArrayForResult.getJSONObject(i);
				newMessage.setMessageId(Integer.parseInt(jsonObject
						.getString("id")));
				newMessage.setRead(0);
				newMessage.setMessageText(jsonObject.getString("messagetext"));
				newMessage.setUserId(Integer.parseInt(jsonObject
						.getString("fromuid")));
				newMessage.setSentTo(Integer.parseInt(jsonObject
						.getString("touid")));
				newMessages.add(newMessage);
			}
			for (Message message : newMessages) {
				System.out.println("Recieved message: " + message.toString());
			}
		} catch (JSONException e) {
			return null;
		}
		return newMessages;
	}

	public void onDestroy() {
		super.onDestroy();
		System.out.println("destroying timer + " + System.currentTimeMillis());
	}
}