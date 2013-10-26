package edu.gatech.mas.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable{
	
	private String username;
	
	private Status status;
	
	private Location location;
	
	private String ip;
	
	private String port;

	public Student()
	{
		username = null;
		location = null;
		status = Status.OFFLINE;
		ip = null;
		port = null;
	}

	public Student(Parcel in) {
		this();
		username = in.readString();
		ip = in.readString();
		port = in.readString();
		location = in.readParcelable(null);
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(username);
		dest.writeString(ip);
		dest.writeString(port);
		dest.writeParcelable(location, flags);
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
	
	@Override
	public String toString() {
		return ("Student " + username + ", ip: " + ip + ", port: " + port);
	}

	public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
		public Student createFromParcel(Parcel in) {
			return new Student(in);
		}

		public Student[] newArray(int size) {
			return new Student[size];
		}
	};
	
}
