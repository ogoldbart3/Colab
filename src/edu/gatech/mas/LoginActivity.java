package edu.gatech.mas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Class that handles login into the app. 
 * @author Pawel
 *
 */
public class LoginActivity extends Activity {

	private Button mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mLoginButton = (Button) findViewById(R.id.btnLogin);
		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClassListActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
