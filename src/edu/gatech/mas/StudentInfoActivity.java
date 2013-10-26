package edu.gatech.mas;

import java.util.Vector;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.gatech.mas.interfaces.IAppManager;
import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.Student;
import edu.gatech.mas.service.IMService;

public class StudentInfoActivity extends Activity {

	public static String USERNAME_TAG = "username";
	private Button mPrivateChatButton;
	private Student mStudent;

	private IAppManager imService;

	private Vector<FriendInfo> friends = null;

	public void setFriendList(Vector<FriendInfo> friends) {
		this.friends = friends;
	}

	public int getCount() {

		return friends.size();
	}

	public FriendInfo getItem(String mUsername2) {

		for (FriendInfo friend : friends) {
			if (friend.userName.equals(mUsername2))
				return friend;
		}
		return null;
	}

	public long getItemId(int position) {

		return 0;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			imService = ((IMService.IMBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			imService = null;
			Toast.makeText(StudentInfoActivity.this,
					R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(StudentInfoActivity.this, IMService.class));
		setContentView(R.layout.activity_student_info);

		Intent intent = getIntent();
		mStudent = intent.getParcelableExtra("student");

		mPrivateChatButton = (Button) findViewById(R.id.private_chat_button);
		mPrivateChatButton
				.setText("Chat with " + mStudent.getUsername() + ", port: "
						+ mStudent.getPort() + ", ip: " + mStudent.getIp());
		mPrivateChatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (imService == null) {
					System.out.println("imService is null");
				} else if (imService.isNetworkConnected() == false) {
					System.out.println("network is not connected");
				} else {

					Thread loginThread = new Thread() {
						private Handler handler = new Handler();

						@Override
						public void run() {
							Vector<FriendInfo> result = null;

							Intent i = new Intent(getApplicationContext(),
									Messaging.class);
							i.putExtra(FriendInfo.USERNAME,
									mStudent.getUsername());
							i.putExtra(FriendInfo.PORT, mStudent.getPort());
							i.putExtra(FriendInfo.IP, mStudent.getIp());
							startActivity(i);
							System.out.println(result);
						}
					};
					loginThread.start();
				}
			}
		});

	}

	@Override
	protected void onPause() {
		unbindService(mConnection);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(StudentInfoActivity.this, IMService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}
}
