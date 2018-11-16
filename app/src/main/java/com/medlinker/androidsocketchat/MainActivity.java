package com.medlinker.androidsocketchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.medlinker.socketclient.ImManager;
import com.medlinker.socketserver.ServerSocketHelper;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText hostName, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textview);
        hostName = (EditText) findViewById(R.id.host_name);
        port = (EditText) findViewById(R.id.port);

        findViewById(R.id.client_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = hostName.getText().toString();
                int ePort = Integer.parseInt(port.getText().toString());
                ImManager.init(getApplicationContext(), 0, host, ePort);
            }
        });

        findViewById(R.id.server_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerSocketHelper.startServive(hostName.getText().toString());
                    }
                }).start();
            }
        });
    }
}
