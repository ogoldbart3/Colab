package edu.gatech.mas.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

	public static final String MESSAGE_TAG = "message";

	private int userId;
	private int sentTo;
	private String messageText;
	private int isRead;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSentTo() {
		return sentTo;
	}

	public void setSentTo(int sentTo) {
		this.sentTo = sentTo;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public int isRead() {
		return isRead;
	}

	public void setRead(int isRead) {
		this.isRead = isRead;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(userId);
		dest.writeInt(sentTo);
		dest.writeString(messageText);
		dest.writeInt(isRead);
	}

	private Message(Parcel in) {
		this();
		this.userId = in.readInt();
		this.sentTo = in.readInt();
		this.messageText = in.readString();
		this.isRead = in.readInt();
	}

	public Message() {
		this.userId = 0;
		this.sentTo = 0;
		this.messageText = "";
		this.isRead = 0;
	}

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
		public Message createFromParcel(Parcel in) {
			return new Message(in);
		}

		public Message[] newArray(int size) {
			return new Message[size];
		}
	};

	public String toString() {
		return new String("Message: " + messageText + ", from: " + userId
				+ ", to: " + sentTo);
	};

}
