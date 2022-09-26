package com.demo.ipc.client;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demo.ipc.Person;
import com.demo.ipc.aidl.IAIDLService;

public class MainActivity extends AppCompatActivity {

    IAIDLService iAIDLService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
        findViewById(R.id.bt_next_person).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPerson();
            }
        });
    }


    private void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.demo.ipc.aidl.ACTION_SERVICE");
        intent.setPackage("com.demo.ipc");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void nextPerson() {

        if (iAIDLService != null) {
            try {
                Person person = iAIDLService.getPerson("");
                Toast.makeText(MainActivity.this, person.getName(), Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iAIDLService = IAIDLService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iAIDLService = null;
        }
    };



}