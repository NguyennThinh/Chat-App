package com.example.may.Api;

import com.example.may.Model.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH-mm").create();

    ApiClient client = new Retrofit.Builder()
          //  .baseUrl("https://may-chat.herokuapp.com/")
            .baseUrl("http://192.168.3.87:3000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiClient.class);


    @POST("user/{id}/{email}")
    Call<User> addUser(@Path("id") String id,@Path("email") String email);


    @DELETE("user/{id}")
    Call<Void> deleteUser(@Path("id") String id);
}
