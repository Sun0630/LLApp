package com.cdbwsoft.library;

import android.content.Context;

public interface RequestPermissionListener {

	void requestPermission(String[] permission, String reason, Runnable runnable);
	Context getContext();
}
