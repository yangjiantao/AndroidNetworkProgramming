package io.jiantao.androidsocketchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.jiantao.socketclient.ImManager;
import io.jiantao.socketserver.ServerSocketHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocketHelper.start();
            }
        }).start();


        ImManager.init(this.getApplicationContext(), 0);
    }
}
