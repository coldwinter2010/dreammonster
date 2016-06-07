package com.cold.dreammonster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingUtils {
	public static boolean getFirstUsing(Context context) {
		SharedPreferences info = context.getSharedPreferences("current", 0);
		boolean res = info.getBoolean("FIRSTUSING", true); 
		return res;
	}


	public static boolean setFirstUsing(Context context, boolean value) {
		SharedPreferences info = context.getSharedPreferences("current", 0);
		Editor editor = info.edit();
		editor.putBoolean("FIRSTUSING", value);
		return editor.commit();
	}
}
