package com.nick.nusbuddy;

public interface LoginAsyncTaskListener {

	public void onLoginTaskComplete(String responseContent, String userId, String password);
}
