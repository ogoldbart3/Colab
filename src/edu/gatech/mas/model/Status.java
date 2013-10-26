package edu.gatech.mas.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum Status implements Parcelable{
	OFFLINE, ONLINE, AWAY;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ordinal());
	}

	public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
		public Status createFromParcel(Parcel in) {
			return Status.values()[in.readInt()];
		}

		public Status[] newArray(int size) {
			return new Status[size];
		}
	};
}
