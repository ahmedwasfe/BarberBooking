package com.ahmet.barberbooking.Common;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class LoginButtonFacebook {

    private LoginManager loginManager;

    void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    LoginManager getLoginManager() {
        if (loginManager == null) {
            loginManager = LoginManager.getInstance();
        }
        return loginManager;
    }


    /**
     * Registers a login callback to the given callback manager.
     *
     * @param callbackManager The callback manager that will encapsulate the callback.
     * @param callback        The login callback that will be called on login completion.
     */
    public void registerCallback(
            final CallbackManager callbackManager,
            final FacebookCallback<LoginResult> callback) {
        getLoginManager().registerCallback(callbackManager, callback);
    }

    /**
     * Unregisters a login callback to the given callback manager.
     *
     * @param callbackManager The callback manager that will encapsulate the callback.
     */
    public void unregisterCallback(final CallbackManager callbackManager) {
        getLoginManager().unregisterCallback(callbackManager);
    }

}
