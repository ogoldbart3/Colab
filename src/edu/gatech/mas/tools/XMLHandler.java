package edu.gatech.mas.tools;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import edu.gatech.mas.interfaces.IUpdateData;
import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.MessageInfo;
import edu.gatech.mas.model.Status;

/*
 * Parses the xml data to FriendInfo array
 * XML Structure 
 * <?xml version="1.0" encoding="UTF-8"?>
 * 
 * <friends>
 * 		<user key="..." />
 * 		<friend username="..." status="..." IP="..." port="..." key="..." expire="..." />
 * 		<friend username="..." status="..." IP="..." port="..." key="..." expire="..." />
 * </friends>
 *
 *
 *status == online || status == unApproved
 * */

public class XMLHandler extends DefaultHandler
{
		private String userKey = new String();
		private IUpdateData updater;
		
		public XMLHandler(IUpdateData updater) {
			super();
			this.updater = updater;
		}

		private Vector<FriendInfo> mFriends = new Vector<FriendInfo>();
		private Vector<FriendInfo> mOnlineFriends = new Vector<FriendInfo>();
		private Vector<FriendInfo> mUnapprovedFriends = new Vector<FriendInfo>();
		
		private Vector<MessageInfo> mUnreadMessages = new Vector<MessageInfo>();

		
		public void endDocument() throws SAXException 
		{
			FriendInfo[] friends = new FriendInfo[mFriends.size() + mOnlineFriends.size()];
			MessageInfo[] messages = new MessageInfo[mUnreadMessages.size()];
			
			int offlineFriendCount = mFriends.size();			
			for (int i = 0; i < offlineFriendCount; i++) 
			{
				friends[i] = mFriends.get(i);
			}
			
			int unreadMessagecount = mUnreadMessages.size();
			//Log.i("MessageLOG", "mUnreadMessages="+unreadMessagecount );
			for (int i = 0; i < unreadMessagecount; i++) 
			{
				messages[i] = mUnreadMessages.get(i);
				Log.i("MessageLOG", "i="+i );
			}
			
			this.updater.updateData(messages, friends, userKey);
			super.endDocument();
		}		
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException 
		{				
			if (localName == "friend")
			{
				FriendInfo friend = new FriendInfo();
				friend.userName = attributes.getValue(FriendInfo.USERNAME);
				String status = attributes.getValue(FriendInfo.STATUS);
				friend.ip = attributes.getValue(FriendInfo.IP);
				friend.port = attributes.getValue(FriendInfo.PORT);
				friend.userKey = attributes.getValue(FriendInfo.USER_KEY);
				//friend.expire = attributes.getValue("expire");
				
				if (status != null && status.equals("online"))
				{					
					friend.status = Status.ONLINE;
				}
				else
				{
					friend.status = Status.OFFLINE;
				}

				mFriends.add(friend);
			}
			else if (localName == "user") {
				this.userKey = attributes.getValue(FriendInfo.USER_KEY);
			}
			else if (localName == "message") {
				MessageInfo message = new MessageInfo();
				message.userid = attributes.getValue(MessageInfo.USERID);
				message.sendt = attributes.getValue(MessageInfo.SENDT);
				message.messagetext = attributes.getValue(MessageInfo.MESSAGETEXT);
				Log.i("MessageLOG", message.userid + message.sendt + message.messagetext);
				mUnreadMessages.add(message);
			}
			super.startElement(uri, localName, name, attributes);
		}

		@Override
		public void startDocument() throws SAXException {			
			this.mFriends.clear();
			this.mOnlineFriends.clear();
			this.mUnreadMessages.clear();
			super.startDocument();
		}

		public Vector<FriendInfo> getFriends() {
			return mFriends;
		}
}

