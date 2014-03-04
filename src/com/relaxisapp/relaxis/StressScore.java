package com.relaxisapp.relaxis;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StressScore {
		
	private int userId;
	private double score;
	private Date timestamp;
	

    @JsonProperty(value="UserId")
    public int getUserId() {
        return this.userId;
    }
    
    @JsonProperty(value="Score")
    public double getScore() {
        return this.score;
    }
    
    @JsonProperty(value="Timestamp")
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
