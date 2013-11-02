package edu.gatech.mas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.gatech.mas.interfaces.IAppManager;
import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.Message;
import edu.gatech.mas.model.MessageInfo;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.service.IMService;
import edu.gatech.mas.tools.FriendController;
import edu.gatech.mas.tools.LocalStorageHandler;

public class Messaging extends Activity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	public Student mUser = null;

	private static int receiverId = 4; // TODO change for real data
	public static int getReceiverId()
	{
		return receiverId;
	}
	
	public Student mReceiver = null;
	public String username;
	private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	private IAppManager imService;
	private FriendInfo friend = new FriendInfo();
	private LocalStorageHandler localstoragehandler;
	private Cursor dbCursor;

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			imService = ((IMService.IMBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			imService = null;
			Toast.makeText(Messaging.this, R.string.local_service_stopped,
					Toast.LENGTH_SHORT).show();
		}
	};

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
		mReceiver = extras.getParcelable("student");
		friend.userName = extras.getString(FriendInfo.USERNAME);
		friend.ip = extras.getString(FriendInfo.IP);
		friend.port = extras.getString(FriendInfo.PORT);
		String msg = extras.getString(MessageInfo.MESSAGETEXT);

		if (mReceiver != null) {
			System.out.println("user is not null in messaging: "
					+ mReceiver.getUid() + ", username: "
					+ mReceiver.getUsername());
		} else
			System.out.println("user is null in messaging!");

		setTitle("Messaging with " + friend.userName);
		// EditText friendUserName = (EditText)
		// findViewById(R.id.friendUserName);
		// friendUserName.setText(friend.userName);

		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(friend.userName, IMService.USERNAME);

		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				noOfScorer++;

				this.appendToMessageHistory(dbCursor.getString(2),
						dbCursor.getString(3));
				dbCursor.moveToNext();
			}
		}
		localstoragehandler.close();

		if (msg != null) {
			this.appendToMessageHistory(friend.userName, msg);
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
					.cancel((friend.userName + msg).hashCode());
		}

		sendMessageButton.setOnClickListener(new OnClickListener() {
			CharSequence message;
			Handler handler = new Handler();

			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length() > 0) {
					/* appendToMessageHistory(imService.getUsername(),
							message.toString());

					localstoragehandler.insert(imService.getUsername(),
							friend.userName, message.toString());
					*/
					messageText.setText("");

					new PostMessageToDb().execute(message.toString());
					/*
					Thread thread = new Thread() {
						public void run() {
							try {
								if (imService.sendMessage(
										imService.getUsername(),
										friend.userName, message.toString()) == null) {
									handler.post(new Runnable() {
										public void run() {
											Toast.makeText(
													getApplicationContext(),
													R.string.message_cannot_be_sent,
													Toast.LENGTH_LONG).show();
											// showDialog(MESSAGE_CANNOT_BE_SENT);
										}
									});
								}
							} catch (UnsupportedEncodingException e) {
								Toast.makeText(getApplicationContext(),
										R.string.message_cannot_be_sent,
										Toast.LENGTH_LONG).show();

								e.printStackTrace();
							}
						}
					};
					thread.start();*/
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

	class MarkAsReadInDb extends AsyncTask<Integer, Integer, Boolean>
	{

		@Override
		protected Boolean doInBackground(Integer... params) {
			int messageId = params[0];
			HttpClient httpClient = new DefaultHttpClient();
			String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/4/chat/"+receiverId;

			try {
				// Add your data
				URI uri = new URI(api);
				HttpPost httppost = new HttpPost(uri);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("messageRead", String.valueOf(messageId)));

				httppost.setHeader("Cookie", ClassListActivity.getSessionName()
						+ "=" + ClassListActivity.getSessionId());
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpClient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				System.out.println("Updating " + messageId);
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
	class PostMessageToDb extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			String message = params[0];
			HttpClient httpclient = new DefaultHttpClient();
			String api = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/user/"+mUser.getUid()+"/chat/"+receiverId;

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
			return true;
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
		unregisterReceiver(messageReceiver);
		unregisterReceiver(pollMessageReceiver);
		unbindService(mConnection);

		FriendController.setActiveFriend(null);

	}

	@Override
	protected void onResume() {
		super.onResume();
		//bindService(new Intent(Messaging.this, IMService.class), mConnection,
		//		Context.BIND_AUTO_CREATE);

		IntentFilter i = new IntentFilter();
		//i.addAction(IMService.TAKE_MESSAGE);
		i.addAction(ChatService.TAKE);
		//registerReceiver(messageReceiver, i);
		registerReceiver(pollMessageReceiver, i);
		

		//FriendController.setActiveFriend(friend.userName);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extra = intent.getExtras();
			String username = extra.getString(MessageInfo.USERID);
			String message = extra.getString(MessageInfo.MESSAGETEXT);

			if (username != null && message != null) {
				if (friend.userName.equals(username)) {
					appendToMessageHistory(username, message);
					// localstoragehandler.insert(username,
						//	imService.getUsername(), message);

				} else {
					if (message.length() > 15) {
						message = message.substring(0, 15);
					}
					Toast.makeText(Messaging.this,
							username + " says '" + message + "'",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

	private MessageReceiver messageReceiver = new MessageReceiver();

	public class PollMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extra = intent.getExtras();
			String test = extra.getString("test");
			System.out.println("Receving message" + test);
			Message message = extra.getParcelable(Message.MESSAGE_TAG);
			if (message != null) {
				// TODO delete this mReceiver
				if (mReceiver.getUid() == 0 || mReceiver.getUid() == message.getSentTo()) {
					System.out.println("Message: " + message.toString());
					
					appendToMessageHistory("User " + message.getUserId(), message.getMessageText());
					new MarkAsReadInDb().execute(message.getMessageId());
					//localstoragehandler.insert(username,
					//		imService.getUsername(), message);

				} else {
					String messageText = message.getMessageText().substring(0,
							15);
					if (message.getMessageText().length() > 15) {
						messageText = messageText.substring(0, 15);
					}
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
