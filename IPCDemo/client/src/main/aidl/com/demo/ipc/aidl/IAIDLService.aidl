// IAIDLService.aidl
package com.demo.ipc.aidl;

// Declare any non-default types here with import statements

import com.demo.ipc.Person;

interface IAIDLService {

    Person getPerson(String id);
}