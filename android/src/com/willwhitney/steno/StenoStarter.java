package com.willwhitney.steno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class StenoStarter extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Steno", "Starting service.");
        Intent serviceIntent = new Intent(this, StenoService.class);
        startService(serviceIntent);
	}

}
