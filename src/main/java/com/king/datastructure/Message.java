package com.king.datastructure;

/**
 * message definition, used by producer and consumer
 * @author liujingjing
 *
 */
public class Message {
	private String name;
	private long userId;
	private long score;
	private long level;
	
	
	public Message(String name, long userId, long score, long level) {
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
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public long getLevel() {
		return level;
	}
	public void setLevel(long level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Message [name=" + name + ", userId=" + userId + ", score="
				+ score + ", level=" + level + "]";
	}
	
	
}
