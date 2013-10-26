package edu.gatech.mas.interfaces;

import edu.gatech.mas.model.FriendInfo;
import edu.gatech.mas.model.MessageInfo;


public interface IUpdateData {
	 public void updateData(MessageInfo[] messages, FriendInfo[] friends, String userKey);
}
