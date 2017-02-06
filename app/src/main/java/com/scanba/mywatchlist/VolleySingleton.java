package com.scanba.mywatchlist;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private RequestQueue requestQueue;
    private static VolleySingleton volleySingleton = null;

    public VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context) {
        if(volleySingleton == null)
            return new VolleySingleton(context);
        else
            return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
