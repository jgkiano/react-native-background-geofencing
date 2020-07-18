package me.kiano;

import android.content.Context;
import android.widget.Toast;

public class OkVerify {
    public static void ping(Context context) {
        Toast.makeText(context, "PONG", Toast.LENGTH_LONG).show();
    }
}
