package com.sdiablofix.dt.sdiablofix.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by buxianhui on 17/9/30.
 */

public class LoginResponse extends Response{
    @SerializedName("token")
    private String token;

    LoginResponse(){
        super();
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String mToken) {
        this.token = token;
    }
}
