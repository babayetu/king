package com.king.datastructure;

public class Message {
	private String name;
	private int userId;
	private long score;
	private int level;
	
	
	public Message(String name, int userId, long score, int level) {
		this.name = name;
		this.userId = userId;
		this.score = score;
		this.level = level;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Message [name=" + name + ", userId=" + userId + ", score="
				+ score + ", level=" + level + "]";
	}
	
	
}
