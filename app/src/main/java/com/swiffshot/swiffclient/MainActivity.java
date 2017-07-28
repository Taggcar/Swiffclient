package com.swiffshot.swiffclient;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import com.swiffshot.API.SwiffAPI;
import java.io.IOException;
import java.net.URISyntaxException;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;




public class MainActivity extends AppCompatActivity {
    Socket mSocket;
    String ROOM_ID;
    final Handler msgHandler  = new Handler(

    ){
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject jobj = new JSONObject(msg.obj.toString());
                String name = jobj.getJSONObject("user").getString("name");
                String smsg  = jobj.getString("msg");
                String incomingMessage = name + "\n" + smsg;
                TextView tv_messages = (TextView) findViewById(R.id.tv_messages);
                tv_messages.setText(tv_messages.getText().toString() + "\n"+incomingMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };
    private void createSocketIO(final String roomId){
        ROOM_ID = roomId;
        try {
            mSocket = IO.socket("https://www.swiffchat.com:3000");
            mSocket.connect();
            JSONObject credentials = new JSONObject();
            try {
                credentials.put("username","[YOUR_USERNAME]");
                credentials.put("password","[YOUR_PASSWORD]");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("authentication",
                    credentials);

            mSocket.on("connect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String _msg = "{\n" +
                            "                        \"msg\":\"It's cool to send a message!\",\n" +
                            "                        \"type\":\"text\",\n" +
                            "                        \"room\":\"" +ROOM_ID+"\","+
                            "                        \"user\":{\n" +
                            "                        \"name\":\"Mr. Pegg\",\n" +
                            "                            \"email\":\"simon@pegg.com\",\n" +
                            "                            \"uid\":\"XXXX11\"\n" +
                            "                        }\n" +
                            "                }";
                    try {
                        JSONObject  jobj = new JSONObject(_msg);
                        mSocket.emit("msg",jobj );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            mSocket.on("msg", new Emitter.Listener() {
                @Override
                public void call(Object... args) {


                    try {

                        String _msg = args[0].toString();
                        JSONObject  jobj = new JSONObject(_msg);

                        Log.d("SWIFF",jobj.getString("type"));
                        if(jobj.getString("type").equals("text")){
                            //update UI


                            Message m = new Message();
                            m.obj = jobj;
                            msgHandler.sendMessage(m);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            String sdata = "{\"room\":\""+roomId+"\",\"user\":{\"name\":\"Swiff client\",\"email\":\"client@swiffchat.com\",\"uid\":\"007\"}}";

            try {
                JSONObject  jobj = new JSONObject(sdata);
                mSocket.emit("room",jobj );

            } catch (JSONException e) {
                e.printStackTrace();
            }



        } catch (URISyntaxException e) {


        }
    }
    public void send_message(View v){
        TextView tv_message = (TextView) findViewById(R.id.tv_message);

        String _msg =
                "{\n" +
                        "                \"msg\":\""+tv_message.getText().toString()+"\",\n" +
                        "                \"type\":\"text\",\n" +
                        "                \"room\":\""+ROOM_ID+"\",\n" +
                        "                \"user\":{\n" +
                        "                    \"name\":\"Mr. Pegg\",\n" +
                        "                        \"email\":\"simon@pegg.com\",\n" +
                        "                        \"uid\":\"XXXX11\"\n" +
                        "                        }\n" +
                        "                }";

        try {
            JSONObject  jobj = new JSONObject(_msg);
            mSocket.emit("msg",jobj);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_message.setText("");



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwiffAPI sAPI = new SwiffAPI();
        //room id handler
        final Handler roomIdHandler  = new Handler(

        ){
            @Override
            public void handleMessage(Message msg) {
                String incomingMessage = msg.obj.toString();
                TextView tv_roomid = (TextView) findViewById(R.id.tv_roomid);
                tv_roomid.setText(incomingMessage);


            }
        };

        //Body of your click handler
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String user = null;
                try {

                    JSONObject room = new JSONObject(sAPI.createRoom());
                    Message msg = new Message();
                    String idRoom =room.getString("id");
                    msg.obj = idRoom; //set id
                    roomIdHandler.sendMessage(msg);
                    createSocketIO(idRoom);
                    //Log.d("SWIFF",user);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        thread.start();





    }
}
