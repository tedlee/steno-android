package com.willwhitney.steno;

import java.util.ArrayList;
import java.util.List;

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
		blobs.clear();
		return result;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}

}
