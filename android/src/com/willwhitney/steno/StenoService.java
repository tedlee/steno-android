package com.willwhitney.steno;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class StenoService extends Service {

    private Handler mHandler;
	private NotificationManager mNM;

	private PowerManager powerManager;
	private WakeLock wakeLock;

	private SpeechRecognizer recognizer;
	private LocationManager locationManager;
	private TranscriptCache transcriptCache = new TranscriptCache("will");

	public static StenoService instance;


	//	private TextToSpeech tts;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 19935;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d("Steno", "Intent from onStartCommand: " + intent);
    	if (intent != null) {
    		if (intent.hasExtra("start_listening")) {
        		listen();
        		return START_STICKY;
        	}
    	}

    	instance = this;
        Log.i("Steno", "Received start id " + startId + ": " + intent);
        Log.d("Steno", "Service received start command.");

//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//
//			@Override
//				public void onInit(int status) {
//			}
//
//        });
//        tts.setOnUtteranceCompletedListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StenoLock");
        wakeLock.acquire(10 * 60 * 1000);

        mHandler = new Handler();
        TranscriptUploader transcriptUploader = new TranscriptUploader(true);
        mHandler.postDelayed(transcriptUploader, 60 * 60 * 1000);

        listen();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification while Steno is awake.
        showNotification();
    }

//    public void speak(String words) {
//    	HashMap<String, String> speechParams = new HashMap<String, String>();
//    	speechParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "WORDS");
//    	tts.speak(words, TextToSpeech.QUEUE_FLUSH, speechParams);
//    }

    public void handleSpeech(List<String> matches) {
    	Log.d("Steno", "Best match: " + matches.get(0));
    	transcriptCache.addBlob(buildTranscriptBlob(matches));

    	Log.d("Steno", transcriptCache.toString());
    }

    public TranscriptBlob buildTranscriptBlob(List<String> interpretations) {
    	Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	long unixTime = System.currentTimeMillis() / 1000L;
    	return new TranscriptBlob(interpretations, unixTime, lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

//    @Override
//	public void onUtteranceCompleted(String utteranceId) {
//    	Log.d("Steno", "Some utterance was completed with id " + utteranceId);
//    	Log.d("Steno", "I am in state " + state);
//    	Log.d("Steno", "Running startService...");
//
//    	Intent utteranceCompletedIntent = new Intent(this, StenoService.class);
//    	utteranceCompletedIntent.putExtra("utterance_completed", true);
//        startService(utteranceCompletedIntent);
//	}
//
//    public void utteranceCompletedThreadsafe() {
//    	Log.d("Steno", "Received startService in utteranceCompletedThreadsafe");
//    	switch (state) {
//			case NONE:
//				break;
//			case AWAITING_NOTE:
//				listen();
//				break;
//    	}
//    }

    public void listen() {

    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    	intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.willwhitney.Steno");

    	recognizer = SpeechRecognizer.createSpeechRecognizer(this.getApplicationContext());
    	RecognitionListener listener = new RecognitionListener() {

    		@Override
    	    public void onResults(Bundle results) {
    	        ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    	        if (voiceResults == null) {
    	            Log.e("Steno", "No voice results");
    	        } else {
//    	            Log.d("Steno", "Printing matches: ");
//    	            for (String match : voiceResults) {
//    	                Log.d("Steno", match);
//    	            }
    	        }
    	        StenoService.this.handleSpeech(voiceResults);
    	        StenoService.this.listen();
    	    }

    	    @Override
    	    public void onReadyForSpeech(Bundle params) {
    	        Log.d("Steno", "Ready for speech");
    	    }

    	    @Override
    	    public void onError(int error) {
    	        Log.e("Steno", "Error listening for speech: " + error);
    	        mHandler.removeCallbacks(recognitionStopper);
    	        StenoService.this.listen();
    	    }

    	    @Override
    	    public void onBeginningOfSpeech() {
    	        Log.d("Steno", "Speech starting");
    	        mHandler.removeCallbacks(recognitionStopper);
    	    }

			@Override
			public void onBufferReceived(byte[] buffer) {
				Log.d("Steno", "Speech buffer received: " + buffer);

			}

			@Override
			public void onEndOfSpeech() {
				Log.d("Steno", "Speech ended.");
			}

			@Override
			public void onEvent(int eventType, Bundle params) {
				Log.d("Steno", "Speech event received.");

			}

			@Override
			public void onPartialResults(Bundle partialResults) {
				Log.d("Steno", "Speech partial results received.");

			}

			@Override
			public void onRmsChanged(float rmsdB) {
				// TODO Auto-generated method stub

			}
    	};

    	try {
	    	mHandler.postDelayed(recognitionStopper, 3000);
	    	recognizer.setRecognitionListener(listener);
	    	recognizer.startListening(intent);
    	} catch (Exception e) {
    		Log.e("Steno", e.getStackTrace().toString());
    		recognizer.stopListening();
    		mHandler.removeCallbacks(recognitionStopper);
    	}
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, "Steno service stopped.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this)
        		.setSmallIcon(R.drawable.ic_launcher)
        		.setContentTitle("Steno")
        		.setContentText("Running...")
        		.setOngoing(true)
        		.build();


        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    Runnable recognitionStopper = new Runnable() {
    	@Override
    	public void run() {
    		Log.d("Steno", "KILL ALL THE LISTENING");
    		recognizer.stopListening();
    	}
    };

    private class TranscriptUploader implements Runnable {
    	public static final int MAX_ATTEMPTS = 5;

    	public boolean repeats;
    	private int attempts = 0;

    	public TranscriptUploader() {
    		this.repeats = false;
    	}

    	public TranscriptUploader(boolean repeats) {
    		this.repeats = repeats;
    	}

		@Override
		public void run() {
			String json = transcriptCache.dumpJson();
			attemptUpload(json);
			if (repeats) {
				mHandler.postDelayed(this, 60 * 60 * 1000);
			}
		}

		private void attemptUpload(String json) {
			Log.d("Steno", "Uploading transcripts.");
			try {
				attempts++;
				StenoApiClient.uploadTranscripts(json);
			} catch (IOException e) {
				if (attempts < MAX_ATTEMPTS) {
					try {
						Thread.sleep(1 * 1000);
						attemptUpload(json);
					} catch (InterruptedException e1) {
						Log.e("Steno", "TranscriptUploader interrupted during retry. Uploading abandoned.");
					}
				}
			}
		}

    }


}
