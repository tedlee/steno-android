package com.willwhitney.steno;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class StenoStarter extends Activity {

	public static final String NEW_UTTERANCE_KEY = "NEW_UTTERANCE";

	private TextView mostRecentContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final View loginButton = findViewById(R.id.login_button);
        final View logoutButton = findViewById(R.id.logout_button);
        final EditText usernameField = (EditText) findViewById(R.id.username);

        mostRecentContent = (TextView) findViewById(R.id.most_recent_content);

        loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Steno", "Starting service.");
		        Intent serviceIntent = new Intent(StenoStarter.this, StenoService.class);
		        serviceIntent.putExtra("username", usernameField.getText().toString());
		        startService(serviceIntent);

		        View mostRecentLabel = findViewById(R.id.most_recent_label);

		        loginButton.setVisibility(View.GONE);
		        logoutButton.setVisibility(View.VISIBLE);
		        mostRecentLabel.setVisibility(View.VISIBLE);
		        mostRecentContent.setVisibility(View.VISIBLE);
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

	private BroadcastReceiver newUtteranceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra(NEW_UTTERANCE_KEY)) {
				mostRecentContent.setText(intent.getStringExtra(NEW_UTTERANCE_KEY));
			}

		}

	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_UTTERANCE_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(newUtteranceReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(newUtteranceReceiver);
	}

}
