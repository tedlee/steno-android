package com.willwhitney.steno;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TranscriptCache {

	private Gson gson;
	public String user;
	public List<TranscriptBlob> blobs;

	public TranscriptCache(String user) {
		this.user = user;
		blobs = new ArrayList<TranscriptBlob>();
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(TranscriptCache.class, new TranscriptCacheSerializer());
		gson = builder.create();
	}

	public void addBlob(TranscriptBlob blob) {
		blobs.add(blob);
	}

	public String dumpJson() {
		String result = gson.toJson(this);

		try {
			Log.d("Steno", "Attempting to write JSON to memory.");
			File f = new File(Environment.getExternalStorageDirectory(), "StenoLog.txt");
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
			out.println(result);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		blobs.clear();
		return result;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
