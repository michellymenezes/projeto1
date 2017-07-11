package com.projeto1.projeto1.endpoints;

import android.os.AsyncTask;
import android.util.Log;

import com.projeto1.projeto1.listeners.GetUserListener;
import com.projeto1.projeto1.models.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by rafaelle on 07/07/17.
 */

public class HerokuGetUserTask extends AsyncTask {

    private static final String TAG = "HEROKU_GET_USER_TASK";

    private ArrayList<User> users;
    private User userToFind;
    private boolean findUser;

    private String responseMessage = "";
    private String endpoint;
    private GetUserListener mListener;


    public HerokuGetUserTask(String url, GetUserListener listener) {
        endpoint = url;
        users = new ArrayList<User>();
        mListener = listener;
    }

    public HerokuGetUserTask(String url, GetUserListener listener, User user) {
        endpoint = url;
        userToFind = user;
        mListener = listener;
        users = new ArrayList<User>();
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        if (userToFind == null){
            return  getAllUsers();
        } else {
            return FindUser();
        }
    }

    private boolean getAllUsers(){
        URL url;
        try {
            url = new URL(endpoint);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.connect();
            int responseCode = conn.getResponseCode();

            Log.d(TAG, "Response Code: " + String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseMessage += line;
                }
                JSONArray usersJSON = new JSONArray(responseMessage);
                Log.d(TAG, String.valueOf(usersJSON));


                for (int i = 0; i < usersJSON.length(); i++) {
                    if ( usersJSON.getJSONObject(i).has("facebookId")){
                        //Models
                        //String name, String id, String email, String image, Long createdAt, String birthday, String gender, Double reputation, ArrayList<String> preferences

                        /* BD
                        user {
                            _id: String,
                                    fullname: String,
                                    email: String,
                                    created_at: String,
                                    image: String,
                                    reputation: Double,
                                    preferences: [String],
                        }*/
                        String id = usersJSON.getJSONObject(i).getString("_id");
                        String faceoockId = usersJSON.getJSONObject(i).getString("facebookId");
                        String name = usersJSON.getJSONObject(i).getString("fullname");
                        String email = usersJSON.getJSONObject(i).getString("email");
                        String createdAt = usersJSON.getJSONObject(i).getString("created_at");
                        String image = usersJSON.getJSONObject(i).getString("image");
                        Double reputation = usersJSON.getJSONObject(i).getDouble("reputation");
                        //TODO ajustar preferences
                        String preferences = usersJSON.getJSONObject(i).getString("preferences");

                        users.add(new User(name, id,faceoockId, email, image, createdAt, reputation, new ArrayList<String>()));

                    }

                }

                return true;

            } else {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line = "", error = "";
                while ((line = br1.readLine()) != null) {
                    error += line;
                }
                Log.d(TAG, error);
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean FindUser(){
        getAllUsers();
        for (User user: users){
            if (user.getFacebookId().equals(userToFind.getFacebookId())){
                findUser = true;
                return true;
            }
        }
        return false;


    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (userToFind != null) {
            mListener.OnGetUserFinished(findUser, this.userToFind);
            Log.d(TAG, String.valueOf(findUser));
        } else {
            if (users != null){
                mListener.OnGetAllUsersFinished(true, this.users);

            } else {
                mListener.OnGetAllUsersFinished(false, this.users);
            }

        }
    }

}
