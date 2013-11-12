package edu.gatech.mas.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {

	private int id;

	private List<Student> students;
	
	private String name;
	
	public Course() {
		students = new ArrayList<Student>();
		setId(0);
		setName(null);
	}
	
	private Course(Parcel in) {
		this();
		setId(in.readInt());
		in.readList(students, null);
		setName(in.readString());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getId());
		dest.writeList(students);
		dest.writeString(getName());
	}

	public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
		public Course createFromParcel(Parcel in) {
			return new Course(in);
		}

		public Course[] newArray(int size) {
			return new Course[size];
		}
	};
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> studentsArray) {
		this.students = studentsArray;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return new String("Course: " + getName() + ", courseId: " + getId());
	}
}
