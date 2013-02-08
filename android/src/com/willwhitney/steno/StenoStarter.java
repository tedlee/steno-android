package com.willwhitney.steno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class StenoStarter extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final View loginButton = findViewById(R.id.login_button);
        final View logoutButton = findViewById(R.id.logout_button);
        final EditText usernameField = (EditText) findViewById(R.id.username);
        loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Steno", "Starting service.");
		        Intent serviceIntent = new Intent(StenoStarter.this, StenoService.class);
		        serviceIntent.putExtra("username", usernameField.getText().toString());
		        startService(serviceIntent);

		        loginButton.setVisibility(View.GONE);
		        logoutButton.setVisibility(View.VISIBLE);
		        usernameField.setEnabled(false);
			}
        });

        logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Steno", "Stopping service.");
		        Intent serviceIntent = new Intent(StenoStarter.this, StenoService.class);
		        serviceIntent.putExtra("terminate", true);
		        startService(serviceIntent);

		        logoutButton.setVisibility(View.GONE);
		        loginButton.setVisibility(View.VISIBLE);
		        usernameField.setEnabled(true);
			}
        });

	}

}
