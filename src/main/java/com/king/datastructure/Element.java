package com.king.datastructure;

public class Element {
	private long uid;
	private long score;
		
	public Element(long uid, long score) {
		this.uid = uid;
		this.score = score;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	
	
}
