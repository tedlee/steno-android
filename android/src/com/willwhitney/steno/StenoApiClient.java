package com.willwhitney.steno;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class StenoApiClient extends AsyncTask<String, Void, Boolean>{

	public static final String HOST_URL = "http://stenoapp.herokuapp.com/api/users";

	public static void uploadTranscripts(String json) throws IOException {
		Log.i("Steno", "about to upload this JSON: " + json);
		HttpRequest response = HttpRequest.post(HOST_URL).send(json);
		Log.d("Steno", response.toString());
		Log.d("Steno", "code: " + response.code());
		if (response.code() < 200 || response.code() >= 300) {
			Log.e("Steno", "Couldn't upload transcripts");
			throw new IOException();
		}
	}

	@Override
	protected Boolean doInBackground(String... args) {
		if(args.length > 1) {
			Log.e("Steno", "Received invalid arguments for transcript upload.");
			return false;
		} else {
			String json = args[0];
			try {
				uploadTranscripts(json);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
    protected void onPostExecute(Boolean result) {
		if(result) {
			Log.d("Steno", "Successfully uploaded transcript.");
		}
    }
}
