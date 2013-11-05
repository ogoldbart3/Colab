package edu.gatech.mas;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.gatech.mas.model.Message;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.tools.LocalStorageHandler;

public class Messaging extends Activity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	public Student mUser = null;

	public Student mReceiver = null;
	public String username;
	private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	private LocalStorageHandler localstoragehandler;
	private Cursor dbCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.messaging_screen); // messaging_screen);

		messageHistoryText = (EditText) findViewById(R.id.messageHistory);

		messageText = (EditText) findViewById(R.id.message);

		messageText.requestFocus();

		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);

		Bundle extras = this.getIntent().getExtras();

		mUser = extras.getParcelable("user");
		mReceiver = extras.getParcelable("receiver");
		String msg = "";

		setTitle("Messaging with " + mReceiver.getFirstName());

		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(mUser.getUsername(),
				mReceiver.getUsername());

		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				noOfScorer++;

				if (dbCursor.getString(2).equals(mUser.getUsername()))
					this.appendToMessageHistory(mUser.getFirstName(),
							dbCursor.getString(3));
				else
					this.appendToMessageHistory(mReceiver.getFirstName(),
							dbCursor.getString(3));

				dbCursor.moveToNext();
			}
		}
		localstoragehandler.close();

		if (msg != null && msg != "") {
			this.appendToMessageHistory(mReceiver.getFirstName(), msg);
		}

		sendMessageButton.setOnClickListener(new OnClickListener() {
			CharSequence message;

			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length() > 0) {
					messageText.setText("");

					new PostMessageToDb().execute(message.toString());
				}
			}
		});

		messageText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) {
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
		});

	}

	class MarkAsReadInDb extends AsyncTask<Integer, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			int messageId = params[0];
			HttpClient httpClient = new DefaultHttpClient();
			String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/4/chat/"
					+ mReceiver.getUid();

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

	class PostMessageToDb extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			String message = params[0];
			HttpClient httpclient = new DefaultHttpClient();
			String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"
					+ mUser.getUid() + "/chat/" + mReceiver.getUid();

			System.out.println("Sending message:  " + message + ", to: " + api);
			try {
				// Add your data
				URI uri = new URI(api);
				HttpPost httppost = new HttpPost(uri);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("message", message
						.trim()));

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
			localstoragehandler.insert(mUser.getUsername(),
					mReceiver.getUsername(), result);
			appendToMessageHistory(mUser.getFirstName(), result);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id) {
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
			break;
		}

		if (message == -1) {
			return null;
		} else {
			return new AlertDialog.Builder(Messaging.this)
					.setMessage(message)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
								}
							}).create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(pollMessageReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter i = new IntentFilter();
		i.addAction(ChatService.TAKE);
		registerReceiver(pollMessageReceiver, i);

	}

	public class PollMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extra = intent.getExtras();
			Message message = extra.getParcelable(Message.MESSAGE_TAG);

			if (message != null && message.isRead() == 0) {
				new MarkAsReadInDb().execute(message.getMessageId());

				if (mUser.getUid() == message.getSentTo()) {

					appendToMessageHistory(mReceiver.getFirstName(),
							message.getMessageText());
					localstoragehandler.insert(mUser.getUsername(),
							mUser.getUsername(), message.getMessageText());

				} else {
					String messageText = message.getMessageText()
							.substring(
									0,
									Math.min(15, message.getMessageText()
											.length() - 1));
					Toast.makeText(
							Messaging.this,
							message.getUserId() + " says '" + messageText + "'",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

	private PollMessageReceiver pollMessageReceiver = new PollMessageReceiver();

	public void appendToMessageHistory(String username, String message) {
		if (username != null && message != null) {
			messageHistoryText.append(username + ":\n");
			messageHistoryText.append(message + "\n");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (localstoragehandler != null) {
			localstoragehandler.close();
		}
		if (dbCursor != null) {
			dbCursor.close();
		}
	}

}
