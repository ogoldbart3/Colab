package edu.gatech.mas;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.gatech.mas.api.ChatMarkMessage;
import edu.gatech.mas.api.ChatPostMessage;
import edu.gatech.mas.api.IChatCallback;
import edu.gatech.mas.model.Message;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.tools.LocalStorageHandler;

/**
 * Activity responsible for displaying and handling chat between users.
 * 
 * @author Pawel
 * 
 */
public class ChatActivity extends Activity implements IChatCallback {

	/** User of the app (also sender of the message) */
	public Student mSender = null;

	/** Other student, receiver of chat messages */
	public Student mReceiver = null;

	/** String that contains content of the message */
	private EditText mMessageText;

	/** String that contains content of the historical messages between users */
	private EditText mMessageHistoryText;

	private Button sendMessageButton;
	private LocalStorageHandler localstoragehandler;
	private Cursor dbCursor;

	/** Broadcast receiver of chat messages */
	private PollMessageReceiver pollMessageReceiver = new PollMessageReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.messaging_screen); // messaging_screen);

		mMessageHistoryText = (EditText) findViewById(R.id.messageHistory);

		mMessageText = (EditText) findViewById(R.id.message);

		mMessageText.requestFocus();

		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);

		Bundle extras = this.getIntent().getExtras();

		mSender = extras.getParcelable("user");
		mReceiver = extras.getParcelable("receiver");
		String msg = "";

		setTitle("Messaging with " + mReceiver.getFirstName());

		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(mSender.getUsername(),
				mReceiver.getUsername());

		// load a historical messages between sender and receiver
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				noOfScorer++;

				if (dbCursor.getString(2).equals(mSender.getUsername()))
					this.appendToMessageHistory(mSender.getFirstName(),
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
				message = mMessageText.getText();
				if (message.length() > 0) {
					mMessageText.setText("");

					new ChatPostMessage(ChatActivity.this).execute(
							message.toString(),
							String.valueOf(mSender.getUid()),
							String.valueOf(mReceiver.getUid()));
				}
			}
		});

		mMessageText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) {
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
		});

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

	/**
	 * Receiver of broadcast messages that contain chat messages.
	 * 
	 * @author Pawel
	 */
	public class PollMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extra = intent.getExtras();
			Message message = extra.getParcelable(Message.MESSAGE_TAG);

			// TODO Pawel: refactor
			if (message != null && message.isRead() == 0) {
				new ChatMarkMessage().execute(message.getMessageId(),
						mSender.getUid(), mReceiver.getUid());

				// check if we are valid receiver of the message
				if (mSender.getUid() == message.getSentTo()) {
					appendToMessageHistory(mReceiver.getFirstName(),
							message.getMessageText());
					localstoragehandler.insert(mSender.getUsername(),
							mSender.getUsername(), message.getMessageText());

				} else {
					String messageText = message.getMessageText()
							.substring(
									0,
									Math.min(15, message.getMessageText()
											.length() - 1));
					Toast.makeText(
							ChatActivity.this,
							message.getUserId() + " says '" + messageText + "'",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

	@Override
	public void displayChatMessage(String result) {
		localstoragehandler.insert(mSender.getUsername(),
				mReceiver.getUsername(), result);
		appendToMessageHistory(mSender.getFirstName(), result);
	}

	public void appendToMessageHistory(String username, String message) {
		if (username != null && message != null) {
			mMessageHistoryText.setTypeface(null, Typeface.BOLD);
			mMessageHistoryText.append(username + ":\n");
			mMessageHistoryText.setTypeface(null, Typeface.NORMAL);
			mMessageHistoryText.append(message + "\n\n");
		}
	}

}
