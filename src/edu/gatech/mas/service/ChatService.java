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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.model.Message;

/**
 * Chat service is responsible for fetching messages in json format from server
 * (by polling), parsing json format to {@link Message} and broadcasting this
 * message to the receiver of chat message.
 * 
 * @author Pawel
 * 
 */
public class ChatService extends Service {

	private static String TAG = ChatService.class.getSimpleName();
	public static final String TAKE = "Take_Message";
	private static Timer mTimer = new Timer();

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		startService();
	}

	private void startService() {
		mTimer.scheduleAtFixedRate(new mainTask(), 5000, 5000);
	}

	private class mainTask extends TimerTask {
		public void run() {
			// run async task that fetches messages from server
			new FetchMessage().execute();
		}
	}

	/**
	 * Get chat messages from server in async task.
	 * 
	 * @author Pawel
	 * 
	 */
	public class FetchMessage extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			// prepare a http get request
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

				// execute http request
				HttpResponse response = mClient.execute(request);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);

				ArrayList<Message> messages = parseMessage(result);

				if (messages != null && messages.size() > 0) {
					// if result is not null, broadcast it to the listeners
					broadCastMessage(messages);
				}

			} catch (Exception e) {
				Log.e(TAG, "Error in http connection: " + e.toString());
				e.printStackTrace();
			}

			return result;
		}
	}

	/**
	 * Broadcast chat message to listeners.
	 * 
	 * @param messages
	 *            list of newly received chat messages
	 */
	private void broadCastMessage(ArrayList<Message> messages) {
		for (Message message : messages) {
			Intent i = new Intent(TAKE);
			i.putExtra(Message.MESSAGE_TAG, message);
			sendBroadcast(i);
		}

	}

	/**
	 * Parse messages from json format and create {@link Message} objects out of
	 * them.
	 * 
	 * @param result
	 *            json object with messages
	 * @return list of parsed messages
	 */
	private ArrayList<Message> parseMessage(String result) {

		ArrayList<Message> newMessages = new ArrayList<Message>();
		if (result != null && result.length() > 0)
			try {
				JSONArray JsonArrayForResult = new JSONArray(result);
				for (int i = 0; i < JsonArrayForResult.length(); i++) {
					Message newMessage = new Message();
					JSONObject jsonObject = JsonArrayForResult.getJSONObject(i);
					newMessage.setMessageId(Integer.parseInt(jsonObject
							.getString("id")));
					newMessage.setRead(0);
					newMessage.setMessageText(jsonObject
							.getString("messagetext"));
					newMessage.setUserId(Integer.parseInt(jsonObject
							.getString("fromuid")));
					newMessage.setSentTo(Integer.parseInt(jsonObject
							.getString("touid")));
					newMessage.setRead(Integer.parseInt(jsonObject
							.getString("isRead")));
					newMessages.add(newMessage);
				}
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing json result: " + e.toString());
				e.printStackTrace();
			}
		return newMessages;
	}

	public void onDestroy() {
		super.onDestroy();
	}
}