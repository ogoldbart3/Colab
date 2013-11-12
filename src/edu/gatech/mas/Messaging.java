package edu.gatech.mas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.gatech.mas.api.IChatCallback;
import edu.gatech.mas.api.ChatMarkMessage;
import edu.gatech.mas.api.ChatPostMessage;
import edu.gatech.mas.model.Message;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.ChatService;
import edu.gatech.mas.tools.LocalStorageHandler;

public class Messaging extends Activity implements IChatCallback {

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

					new ChatPostMessage(Messaging.this).execute(
							message.toString(), String.valueOf(mUser.getUid()),
							String.valueOf(mReceiver.getUid()));
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
				new ChatMarkMessage().execute(message.getMessageId(),
						mUser.getUid(), mReceiver.getUid());

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

	public void displayChatMessage(String result) {
		localstoragehandler.insert(mUser.getUsername(),
				mReceiver.getUsername(), result);
		appendToMessageHistory(mUser.getFirstName(), result);
	}

}
