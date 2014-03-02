package com.relaxisapp.relaxis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
	
	public User() {
		
	}
	
	public User(String fbUserId, String fbUserName) {
		this.fbUserId = fbUserId;
		this.fbUserName = fbUserName;
	}
	
	private int userId;
    private String fbUserId;
    private String fbUserName;

    @JsonProperty(value="UserId")
    public int getUserId() {
        return this.userId;
    }

    @JsonProperty(value="FbUserId")
    public String getFbUserId() {
        return this.fbUserId;
    }

    @JsonProperty(value="FbUserName")
    public String getFbUserName() {
        return this.fbUserName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public void setFbUserName(String fbUserName) {
        this.fbUserName = fbUserName;
    }
}
