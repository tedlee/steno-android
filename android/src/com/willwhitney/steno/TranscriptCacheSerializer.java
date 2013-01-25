package com.willwhitney.steno;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TranscriptCacheSerializer implements JsonSerializer<TranscriptCache> {

	@Override
	public JsonElement serialize(TranscriptCache src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("user", src.user);
		JsonArray jsonBlobs = new JsonArray();
		for (TranscriptBlob blob : src.blobs) {
			JsonObject jsonBlob = new JsonObject();
			jsonBlob.add("blob", context.serialize(blob));
			jsonBlobs.add(jsonBlob);
		}
		result.add("steno_blobs", jsonBlobs);
		return result;
	}

}