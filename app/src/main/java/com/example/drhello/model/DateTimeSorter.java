package com.example.drhello.model;

public class DateTimeSorter {

    public int mIndex;

    public String mDateTime;

    public DateTimeSorter(int index, String DateTime){

        mIndex = index;

        mDateTime = DateTime;

    }

    public int getIndex() {

        return mIndex;

    }

    public String getDateTime() {

        return mDateTime;

    }

}
