package edu.gatech.mas.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model object for a chat message. Contains message text, sender id, receiver
 * id, etc.
 * 
 * @author Pawel
 * 
 */
public class Message implements Parcelable {

	public static final String MESSAGE_TAG = "message";

	/** Unique id of the message */
	private int messageId;

	/** User id of the sender */
	private int senderId;
	
	/** User id of the receiver */
	private int receiverId;
	
	/** Content of chat message */
	private String messageText;
	
	/** Flag indicating if the message has been already read */
	private int isRead;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(messageId);
		dest.writeInt(senderId);
		dest.writeInt(receiverId);
		dest.writeString(messageText);
		dest.writeInt(isRead);
	}

	private Message(Parcel in) {
		this();
		this.messageId = in.readInt();
		this.senderId = in.readInt();
		this.receiverId = in.readInt();
		this.messageText = in.readString();
		this.isRead = in.readInt();
	}

	public Message() {
		this.messageId = 0;
		this.senderId = 0;
		this.receiverId = 0;
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
		return new String("Message: " + messageText + ", from: " + senderId
				+ ", to: " + receiverId);
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	};

	public int getUserId() {
		return senderId;
	}

	public void setUserId(int userId) {
		this.senderId = userId;
	}

	public int getSentTo() {
		return receiverId;
	}

	public void setSentTo(int sentTo) {
		this.receiverId = sentTo;
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

}
