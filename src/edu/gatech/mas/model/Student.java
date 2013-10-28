package edu.gatech.mas.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable{
	
	private int uid; 
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private Status status;
	
	private Location location;
	
	private String ip;
	
	private String port;

	public Student()
	{
		uid = 0;
		username = "";
		location = null;
		firstName = "";
		lastName = "";
		status = Status.OFFLINE;
		ip = null;
		port = null;
	}

	public Student(Parcel in) {
		this();
		uid = in.readInt();
		username = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		ip = in.readString();
		port = in.readString();
		location = in.readParcelable(null);
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
		dest.writeString(ip);
		dest.writeString(port);
		dest.writeParcelable(location, flags);
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
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

	@Override
	public String toString() {
		return ("Student " + username + ", ip: " + ip + ", port: " + port + ", uid: " + uid);
	}

}
