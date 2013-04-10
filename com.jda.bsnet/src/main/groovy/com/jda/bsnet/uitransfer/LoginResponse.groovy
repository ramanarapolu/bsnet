package com.jda.bsnet.uitransfer

class LoginResponse {
	List<MenuUrlPair> menuList
	boolean loginSuccess
	String errorString

	boolean isLoginSuccess() {
		return loginSuccess
	}

}
