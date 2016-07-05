package com.app.madym.tracker;

import android.os.Parcel;
import android.os.Parcelable;

public class PointsEntry implements Parcelable {

    public String text; // thing you're counting
    public int count; // how much of it

    public PointsEntry() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    public PointsEntry(int c, String t) {
        count = c;
        text = t;
    }

    // FUCK YOU FIREBASE WHAT DO YOU WANT FROM ME
    public PointsEntry(int c) {
        count = c;
    }
    public PointsEntry(String t) {
        text = t;
    }
    public PointsEntry(String t, long c) {
        count = (int) c;
        text = t;
    }

    public String getText() {
        return text;
    }

    public int getCount() {
        return count;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected PointsEntry(Parcel in) {
        count = in.readInt();
        text = in.readString();
    }

    public static final Creator<PointsEntry> CREATOR = new Creator<PointsEntry>() {
        @Override
        public PointsEntry createFromParcel(Parcel in) {
            return new PointsEntry(in);
        }

        @Override
        public PointsEntry[] newArray(int size) {
            return new PointsEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(count);
        parcel.writeString(text);
    }

    @Override
    public String toString() {
        return "PointsEntry: text: " + text + " count: " + count;
    }
}
