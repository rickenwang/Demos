package com.demo.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.demo.ipc.Person;

import java.util.Random;

/**
 * Created by jeremywang on 2022/9/11.
 */
public class AIDLRemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return service;
    }

    private final IAIDLService.Stub service = new IAIDLService.Stub() {

        private String[] candidateNames = new String[] {
                "Andy",
                "Jeremy",
                "Bolt",
                "Ken",
                "Jordan",
                "Jenny",
                "Jone",
                "Fred"
        };

        @Override
        public Person getPerson(String id) throws RemoteException {
            int index = new Random(System.currentTimeMillis()).nextInt(candidateNames.length);
            return new Person(id, candidateNames[index]);
        }
    };
}
