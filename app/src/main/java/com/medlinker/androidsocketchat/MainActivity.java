package com.medlinker.androidsocketchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jiantao.socketlib.client.SocketHelper;
import com.jiantao.socketlib.server.ServerSocketHelper;
import com.medlinker.socketclient.ImManager;

public class MainActivity extends AppCompatActivity {

  TextView textView;
  EditText hostName, port;

  SocketHelper socketHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = (TextView) findViewById(R.id.textview);
    hostName = (EditText) findViewById(R.id.host_name);
    port = (EditText) findViewById(R.id.port);

    findViewById(R.id.client_btn)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        String host = hostName.getText().toString();
                        int ePort =
                            Integer.parseInt(
                                port.getText().toString());
                        socketHelper = new SocketHelper();
                        socketHelper.connectServer(host, ePort);
                      }
                    })
                    .start();
              }
            });

    findViewById(R.id.server_btn)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        ServerSocketHelper.startServive(
                            hostName.getText().toString());
                      }
                    })
                    .start();
              }
            });
  }

  @Override
  protected void onDestroy() {
    if (socketHelper != null) {
      socketHelper.close();
    }

    ServerSocketHelper.closeLastHandler();
    super.onDestroy();
  }
}
