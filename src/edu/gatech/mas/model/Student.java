package edu.gatech.mas.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model object for a student. Not that it is assumed that an user of the app is
 * also Student.
 * 
 * @author Pawel
 * 
 */
public class Student implements Parcelable {

	/** Georgia Tech id of the student */
	private int uid;

	/** Georgia Tech username of the student */
	private String username;

	/** First name of the student */
	private String firstName;

	/** Last name of the student */
	private String lastName;

	/** Current {@link Status} of the student */
	private Status status;

	/** Phone number of the student */
	private String phone;

	/** Location of the student	 */
	private Location location;

	/**
	 * Short information about the user (e.g. which classes he/she has taken).
	 */
	private String about;

	public Student() {
		uid = 0;
		username = "";
		location = null;
		firstName = "";
		lastName = "";
		phone = "";
		status = Status.Offline;
		about = "";
	}

	public Student(Parcel in) {
		this();
		uid = in.readInt();
		username = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		status = (Status) in.readParcelable(Status.class.getClassLoader());

		// TODO Pawel: after integrating with Amy's code, exchange these lines
		location = in.readParcelable(null);
		// location = Location.CREATOR.createFromParcel(in);
		phone = in.readString();
		about = in.readString();
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
		dest.writeParcelable(status, flags);
		dest.writeParcelable(location, flags);
		dest.writeString(phone);
		dest.writeString(about);
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

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	@Override
	public String toString() {
		return ("StudentId: " + uid + ", username: " + username + ", name: "
				+ firstName + " " + lastName);
	}

}
