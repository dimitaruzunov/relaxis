package com.relaxisapp.relaxis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
	
	public User() {
		
	}
	
	public User(String fbUserId) {
		this.fbUserId = fbUserId;
	}
	
	private int userId;
    private String fbUserId;

    @JsonProperty(value="UserId")
    public int getUserId() {
        return this.userId;
    }

    @JsonProperty(value="FbUserId")
    public String getFbUserId() {
        return this.fbUserId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }
}
