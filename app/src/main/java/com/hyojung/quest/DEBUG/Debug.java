package com.hyojung.quest.DEBUG;

import android.content.Context;
import android.widget.Toast;

public class Debug {
    private static final boolean DEBUG = true;
    public static void makeToast(Context context, String message){
        if(DEBUG)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
