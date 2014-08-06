package com.as.schat;

import android.app.Application;

import com.parse.Parse;

public class SChatApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "hbOwq6ws1EpPTiPeAjQgP0R51eFU4lwnRdGoH0On", "sc5Lkx76LGQpNleOfv9iQDGppy7VoIsm8YmlAikE");
	}

}
