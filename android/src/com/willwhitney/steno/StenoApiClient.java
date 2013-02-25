package com.willwhitney.steno;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class StenoApiClient {

	public static final String HOST_URL = "http://stenoapp.herokuapp.com/api/users";

	public void uploadTranscripts(String json) {
		new TranscriptUploaderTask().execute(HOST_URL, json);
	}
	
	private class TranscriptUploaderTask extends AsyncTask<String, Void, Boolean> {
		
		public final int MAX_ATTEMPTS = 5;
		public final int RETRY_DELAY_SECONDS = 5;
		
		private int attempts = 0;
		
		public void uploadTranscripts(String url, String json) throws IOException {
			Log.i("Steno", "about to upload this JSON: " + json);
			HttpRequest response = HttpRequest.post(url).send(json);
			Log.d("Steno", "code: " + response.code());
			if (response.code() < 200 || response.code() >= 300) {
				Log.e("Steno", "Couldn't upload transcripts.");
				throw new IOException();
			}
		}
		
		private boolean attemptUpload(String url, String json) {
			Log.d("Steno", "Uploading transcripts.");
			try {
				attempts++;
				uploadTranscripts(url, json);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				if (attempts < MAX_ATTEMPTS) {
					try {
						Thread.sleep(RETRY_DELAY_SECONDS * 1000);
						attemptUpload(url, json);
					} catch (InterruptedException e1) {
						Log.e("Steno", "TranscriptUploader interrupted during retry. Uploading abandoned.");
						return false;
					}
				} else {
					Log.e("Steno", "TranscriptUploader could not upload transcript after " + MAX_ATTEMPTS + " attempts. Uploading abandoned.");
					return false;
				}
			}
			return false;
		}

		@Override
		protected Boolean doInBackground(String... args) {
			if(args.length != 2) {
				Log.e("Steno", "Received invalid arguments for transcript upload: " + args);
				return false;
			} else {
				String url = args[0];
				String json = args[1];
				return attemptUpload(url, json);
			}
		}

		@Override
	    protected void onPostExecute(Boolean result) {
			if(result) {
				Log.d("Steno", "Successfully uploaded transcript.");
			} 
	    }
	}
}
