package com.relaxisapp.relaxis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BreathingScore {
		
	public BreathingScore() {
		
	}
	
	public BreathingScore(int userId, double score, String timestamp) {
		this.userId = userId;
		this.score = score;
		this.timestamp = timestamp;
	}
	
	private int userId;
	private double score;
	private String timestamp;


    @JsonProperty(value="UserId")
    public int getUserId() {
        return this.userId;
    }
    
    @JsonProperty(value="Score")
    public double getScore() {
        return this.score;
    }
    
    @JsonProperty(value="Timestamp")
    public String getTimestamp() {
        return this.timestamp;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
