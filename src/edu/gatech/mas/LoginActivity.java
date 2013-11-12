package edu.gatech.mas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

/**
 * This activity is a start activity and it is responsible for user
 * authentication. It is integrated with the central authentication service
 * (CAS) from Georgia Tech (so that our users - students, don't have to
 * register, but can simply use their Georgia Tech credentials). CAS doesn't
 * have API, it does the authentication only through the browser. So here after
 * user launches the app and presses login button, the browser is automatically
 * opened with page with CAS login. After successful authentication, CAS issues
 * session id, which is returned to our app. This session id is used during
 * usage of the app, especially during calls to our API that requires CAS
 * session id. 
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
