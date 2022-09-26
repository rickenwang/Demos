package com.demo.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jeremywang on 2022/9/11.
 */
public class Person implements Parcelable {

    private String id;

    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Person(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}
