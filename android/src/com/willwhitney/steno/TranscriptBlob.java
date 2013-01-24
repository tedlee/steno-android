package com.willwhitney.steno;

import java.util.List;

public class TranscriptBlob {

	public List<String> transcription;
	public long timestamp;
	public double lat;
	public double lon;

	public TranscriptBlob(List<String> interpretations, long timestamp, double lat, double lon) {
		this.transcription = interpretations;
		this.timestamp = timestamp;
		this.lat = lat;
		this.lon = lon;
	}

}
