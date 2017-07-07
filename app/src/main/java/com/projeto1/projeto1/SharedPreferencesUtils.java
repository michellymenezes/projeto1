package com.projeto1.projeto1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.projeto1.projeto1.models.User;

/**
 * Created by rafaelle on 06/07/17.
 */

public class SharedPreferencesUtils {

        public static final String TAG = "SHARED_PREFERENCES";

        protected SharedPreferencesUtils() {}

        final static String SEPARATOR = "_";

    public static User getUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("USER", null);
        User obj = gson.fromJson(json, User.class);
        return  obj;
    }

    public static void setUser(Context context, User user){

        SharedPreferences settings = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("USER", json);
        editor.commit();
    }
}