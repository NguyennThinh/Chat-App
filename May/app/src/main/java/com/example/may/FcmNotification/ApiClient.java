package com.example.may.FcmNotification;


import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.may.Model.Group;

import com.example.may.Model.GroupChat;
import com.example.may.Model.User;
import com.example.may.Model.UserChat;
import com.example.may.Model.Work;
import com.example.may.Utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAA1WmhCXQ:APA91bHhIJ-3ZteI7ergMAAwVkdJqy68RlYza4QPbsHVcE4RoXHriLpRr9vKNzfFcXpGiGdJdw5JKDHCM6c0UXzXZzgugrtbkqbe9DGZeAayr4G8KurYKLMpyY2pYkziY2qC0Jy26qr9";

    public static void  pushNotificationUserChat(Context context,String token, User e, UserChat msg){
        StrictMode.ThreadPolicy
                policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("to", token);
            JSONObject body = new JSONObject();
            body.put(Constants.KEY_NAME, e.getFullName());
            body.put(Constants.KEY_ID, e.getId());
            body.put(Constants.KEY_MESSAGE, msg.getMessage());
            body.put(Constants.KEY_AVATAR, e.getAvatar());
            body.put(Constants.REMOTE_MSG_STATUS, msg.isSeen());
            body.put(Constants.KEY_ITEM, "Single");

            jsonObject.put("data", body);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("API", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(request);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    public static void  pushNotificationGroupMSG(Context context, List<String> Token, Group g, GroupChat msg){
        StrictMode.ThreadPolicy
                policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {

            JSONArray  tokens = new JSONArray();
            for (int i =0 ; i < Token.size(); i++){
                tokens.put(Token.get(i));
            }

            JSONObject jsonObject = new JSONObject();


            JSONObject body = new JSONObject();
            body.put(Constants.KEY_NAME, g.getGroupName());
            body.put(Constants.KEY_ID, g.getGroupID());
            body.put(Constants.KEY_MESSAGE, msg.getMessage());
            body.put(Constants.KEY_AVATAR, g.getGroupImage());
            body.put(Constants.REMOTE_MSG_STATUS, msg.isSeen());
            body.put(Constants.KEY_ITEM, "Group");

            jsonObject.put("data", body);
            jsonObject.put("registration_ids", tokens);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("APII", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(request);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }


    public static void  pushNotificationCreateWork(Context context, List<String> Token, User sender, Work work){
        StrictMode.ThreadPolicy
                policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {

            JSONArray  tokens = new JSONArray();
            for (int i =0 ; i < Token.size(); i++){
                tokens.put(Token.get(i));
            }

            JSONObject jsonObject = new JSONObject();


            JSONObject body = new JSONObject();
            body.put(Constants.KEY_NAME, sender.getFullName());
            body.put(Constants.KEY_ID, sender.getId());
            body.put(Constants.KEY_MESSAGE, work.getWorkName());
            body.put(Constants.KEY_AVATAR, sender.getAvatar());
            body.put(Constants.KEY_WORK_ID, work.getId());
            body.put(Constants.REMOTE_MSG_STATUS, "create_work");
            body.put(Constants.KEY_ITEM, "work");

            jsonObject.put("data", body);
            jsonObject.put("registration_ids", tokens);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("APII", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(request);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }
    public static void  pushNotificationWorkRemind(Context context, List<String> Token, User sender, Work work){
        StrictMode.ThreadPolicy
                policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {

            JSONArray  tokens = new JSONArray();
            for (int i =0 ; i < Token.size(); i++){
                tokens.put(Token.get(i));
            }

            JSONObject jsonObject = new JSONObject();


            JSONObject body = new JSONObject();
            body.put(Constants.KEY_NAME, sender.getFullName());
            body.put(Constants.KEY_ID, sender.getId());
            body.put(Constants.KEY_MESSAGE, work.getWorkName());
            body.put(Constants.KEY_AVATAR, sender.getAvatar());
            body.put(Constants.KEY_WORK_ID, work.getId());
            body.put(Constants.REMOTE_MSG_STATUS, "remind_work");
            body.put(Constants.KEY_ITEM, "work");

            jsonObject.put("data", body);
            jsonObject.put("registration_ids", tokens);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("APII", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(request);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }
}
