package edu.gatech.mas.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describes possible status of the student in the colab app. Each student can
 * be online (currently using Colab), away (has been using Colab recently),
 * offline (is not using Colab currently).
 * 
 * @author Pawel
 */
public enum Status implements Parcelable {
	Offline, Online, Away;

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
