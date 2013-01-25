package com.willwhitney.steno;

import java.io.IOException;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class StenoApiClient {

	public static final String HOST_URL = "complete me";

	public static void uploadTranscripts(String json) throws IOException {
		int response = HttpRequest.post(HOST_URL).send(json).code();
		if (response < 200 || response >= 300) {
			Log.e("Steno", "Couldn't upload transcripts");
			throw new IOException();
		}
	}



}
