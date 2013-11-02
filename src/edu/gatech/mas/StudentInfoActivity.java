package edu.gatech.mas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.mas.model.Student;

public class StudentInfoActivity extends Activity {

	public static String USERNAME_TAG = "username";
	private Button mPrivateChatButton;
	private Student mStudent;
	private Student mUser;


	public long getItemId(int position) {

		return 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_info);

		Intent intent = getIntent();
		mStudent = intent.getParcelableExtra("receiver");
		mUser = intent.getParcelableExtra("user");

		mPrivateChatButton = (Button) findViewById(R.id.private_chat_button);
		mPrivateChatButton
				.setText("Chat with " + mStudent.toString());
		mPrivateChatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						Messaging.class);
				i.putExtra("receiver", mStudent);
				i.putExtra("user", mUser);
				startActivity(i);
			}
		});
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
