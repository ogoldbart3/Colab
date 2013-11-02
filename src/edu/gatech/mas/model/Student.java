package edu.gatech.mas.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

	private int uid;

	private String username;

	private String firstName;

	private String lastName;

	private Status status;

	private String phone;

	private Location location;

	public Student() {
		uid = 0;
		username = "";
		location = null;
		firstName = "";
		lastName = "";
		phone = "";
		status = Status.OFFLINE;
	}

	public Student(Parcel in) {
		this();
		uid = in.readInt();
		username = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		location = in.readParcelable(null);
		phone = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(uid);
		dest.writeString(username);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeParcelable(location, flags);
		dest.writeString(phone);
	}

	public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
		public Student createFromParcel(Parcel in) {
			return new Student(in);
		}

		public Student[] newArray(int size) {
			return new Student[size];
		}
	};

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return ("StudentId: " + uid + ", username: " + username + ", name: "
				+ firstName + " " + lastName);
	}

}
