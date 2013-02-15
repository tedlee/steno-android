package com.willwhitney.steno;

//import com.crashlytics.android.Crashlytics;
//import com.crashlytics.android.Crashlytics;
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
	public static final String SERVICE_CREATED_KEY = "SERVICE_CREATED";
	public static final String SERVICE_TERMINATED_KEY = "SERVICE_TERMINATED";

	private View loginButton;
	private View logoutButton;
	private EditText usernameField;
	private TextView mostRecentContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//        Crashlytics.start(this);

		super.onCreate(savedInstanceState);
//        Crashlytics.start(this);

		setContentView(R.layout.login);

        loginButton = findViewById(R.id.login_button);
        logoutButton = findViewById(R.id.logout_button);
        usernameField = (EditText) findViewById(R.id.username);

        mostRecentContent = (TextView) findViewById(R.id.most_recent_content);

        loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();
			}
        });

        logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
        });
        
	}

	private void start() {
		Log.d("Steno", "Starting service.");
        Intent serviceIntent = new Intent(StenoStarter.this, StenoService.class);
        serviceIntent.putExtra("username", usernameField.getText().toString());
        startService(serviceIntent);
	}

	private void stop() {
		Log.d("Steno", "Stopping service.");
        Intent serviceIntent = new Intent(StenoStarter.this, StenoService.class);
        serviceIntent.putExtra("terminate", true);
        startService(serviceIntent);
	}

	private void setIsListening(boolean listening) {
		if (listening) {
			View mostRecentLabel = findViewById(R.id.most_recent_label);

	        loginButton.setVisibility(View.GONE);
	        logoutButton.setVisibility(View.VISIBLE);
	        mostRecentLabel.setVisibility(View.VISIBLE);
	        mostRecentContent.setVisibility(View.VISIBLE);
	        usernameField.setEnabled(false);
		} else {
			logoutButton.setVisibility(View.GONE);
	        loginButton.setVisibility(View.VISIBLE);
	        usernameField.setEnabled(true);
		}
	}

	private BroadcastReceiver newUtteranceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == SERVICE_CREATED_KEY) {
				setIsListening(true);
			} else if (action == SERVICE_TERMINATED_KEY) {
				setIsListening(false);
			} else if (action == NEW_UTTERANCE_KEY) {
				if (intent.hasExtra(NEW_UTTERANCE_KEY)) {
					mostRecentContent.setText(intent.getStringExtra(NEW_UTTERANCE_KEY));
				}
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_UTTERANCE_KEY);
        filter.addAction(SERVICE_CREATED_KEY);
        filter.addAction(SERVICE_TERMINATED_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(newUtteranceReceiver, filter);
        setIsListening(StenoService.listening);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(newUtteranceReceiver);
	}

}
