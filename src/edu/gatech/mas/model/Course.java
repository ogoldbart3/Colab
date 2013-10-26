package edu.gatech.mas.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {

	private int id;

	private List<Student> students;
	
	private List<FriendInfo> friendInfos;

	private String name;
	
	public Course() {
		students = new ArrayList<Student>();
		setFriendInfos(new ArrayList<FriendInfo>());
		id = 0;
		setName(null);
	}
	
	private Course(Parcel in) {
		this();
		id = in.readInt();
		in.readList(students, null);
		in.readList(getFriendInfos(), null);
		setName(in.readString());
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(ArrayList<Student> students) {
		this.students = students;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeList(students);
		dest.writeList(friendInfos);
		dest.writeString(getName());
	}

	public List<FriendInfo> getFriendInfos() {
		return friendInfos;
	}

	public void setFriendInfos(List<FriendInfo> friendInfos) {
		this.friendInfos = friendInfos;
	}

	public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
		public Course createFromParcel(Parcel in) {
			return new Course(in);
		}

		public Course[] newArray(int size) {
			return new Course[size];
		}
	};
}
