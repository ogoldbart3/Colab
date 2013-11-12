package edu.gatech.mas.api;

/**
 * Interface responsible for passing a result of AsyncTask to calling class.
 * 
 * @author Pawel
 */
public interface IChatCallback {

	/**
	 * Display chat message to the user.
	 * 
	 * @param message
	 *            content of the message
	 */
	void displayChatMessage(String message);
}
