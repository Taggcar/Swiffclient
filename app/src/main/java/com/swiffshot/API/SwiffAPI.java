package com.swiffshot.API;

import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by darien on 7/27/17.
 */

public class SwiffAPI {
    private String CREDENTIALS_USER = "[YOUR_USERNAME]";
    private String CREDENTIALS_PWD = "[YOUR_PASSWORD]";
    public String getUserInfo() throws IOException {
        String URL = "https://www.swiffchat.com/api/user";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();

        client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override public Request authenticate(Route route, Response response) throws IOException {
                        System.out.println("Authenticating for response: " + response);
                        System.out.println("Challenges: " + response.challenges());
                        String credential = Credentials.basic(CREDENTIALS_USER, CREDENTIALS_PWD);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();



        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    public String createRoom() throws IOException {
        String URL = "https://www.swiffchat.com/api/rooms/create";
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody =new FormBody.Builder()
                .add("name", "myroom")
                .add("creator", "0072")
                .add("source", "youtube.com")
                .add("video_url", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")

                .build();
        Request request = new Request.Builder()
                .url(URL)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(formBody)
                .build();

        client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override public Request authenticate(Route route, Response response) throws IOException {
                        System.out.println("Authenticating for response: " + response);
                        System.out.println("Challenges: " + response.challenges());
                        String credential = Credentials.basic(CREDENTIALS_USER, CREDENTIALS_PWD);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();



        Response response = client.newCall(request).execute();

        return response.body().string();
    }

}

