package edu.gatech.mas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.gatech.mas.model.Student;

public class StudentInfoActivity extends Activity {

	public static String USERNAME_TAG = "username";
	private Button mPrivateChatButton;
	private Student mReceiver;
	private Student mUser;
	private TextView nameTextView;
	private TextView statusTextView;
	private TextView distanceTextView;
	private TextView aboutTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_info);

		Intent intent = getIntent();
		mUser = intent.getParcelableExtra("user");
		mReceiver = intent.getParcelableExtra("receiver");

		mPrivateChatButton = (Button) findViewById(R.id.private_chat_button);
		mPrivateChatButton.setText("Chat with " + mReceiver.getFirstName());
		mPrivateChatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ChatActivity.class);
				i.putExtra("receiver", mReceiver);
				i.putExtra("user", mUser);
				startActivity(i);
			}
		});

		nameTextView = (TextView) findViewById(R.id.studentNameLabel);
		nameTextView.setText(mReceiver.getFirstName() + " "
				+ mReceiver.getLastName());

		statusTextView = (TextView) findViewById(R.id.studentStatusLabel);
		statusTextView.setText(mReceiver.getStatus().toString());

		distanceTextView = (TextView) findViewById(R.id.studentDistanceLabel);
		if (mReceiver.getLocation() != null) // here we should compute the
												// disctance
			distanceTextView.setText("On Campus");

		aboutTextView = (TextView) findViewById(R.id.studentAboutLabel);
		aboutTextView.setText(mReceiver.getAbout());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
