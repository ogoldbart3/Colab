package edu.gatech.mas.interfaces;

import java.io.UnsupportedEncodingException;


public interface IAppManager {
	
	public String getUsername();
	public String sendMessage(String username,String tousername, String message) throws UnsupportedEncodingException;
	public String authenticateUser(String usernameText) throws UnsupportedEncodingException; 
	public void messageReceived(String username, String message);
	public boolean isNetworkConnected();
	public boolean isUserAuthenticated();
	public String getLastRawFriendList();
	public void exit();
	public String signUpUser(String usernameText, String passwordText, String email);
	public String addNewFriendRequest(String friendUsername);
	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames);

	
}
