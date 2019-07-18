package com.ahmet.barberbooking.Model;

import com.ahmet.barberbooking.Common.Common;

public class Token {

    private String token, phoneNunber;
    private Common.TOKEN_TYPE tokenType;

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return phoneNunber;
    }

    public void setUser(String phoneNunber) {
        this.phoneNunber = phoneNunber;
    }

    public Common.TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(Common.TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }
}
