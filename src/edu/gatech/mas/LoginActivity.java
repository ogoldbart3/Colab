package edu.gatech.mas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

/**
 * This activity is responsible for user authentication, it is integrated with
 * the central authentication service (CAS) from Georgia Tech (so that our users
 * - students, don't have to register, but to use our app can simply use their
 * Georgia Tech credentials. There's no API to CAS, it does the authenticaion
 * only through the browser. So the idea is with the launch of application
 * automaticaly open browser with page with CAS login, and if successful get a
 * session ticket from the browsers.
 * 
 * @author Pawel
 * 
 */
public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/** Called when the user clicks the Send button */
	public void performLogin(View view) {

		Intent myIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("http://dev.m.gatech.edu/login?url=colab://loggedin&sessionTransfer=window"));
		startActivity(myIntent);
	}
}
