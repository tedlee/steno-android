package com.willwhitney.steno;

import java.io.IOException;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class StenoApiClient {

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



}
