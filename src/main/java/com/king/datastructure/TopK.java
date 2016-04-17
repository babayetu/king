package com.king.datastructure;

import java.util.Arrays;


/**
 * Data structure to store each level score
 * The minimum value is stored in the first position
 * The maxmium value is stored in the last position
 * @author liujingjing
 *
 */
public class TopK {
	private static final int TOPN = 15;
	private long[] userId = null;
	private long[] score = null;
	
	public TopK() {
		userId = new long[TOPN];
		score = new long[TOPN];
		for (int i = 0; i < TOPN; i++) {
			userId[i] = Integer.MIN_VALUE;
			score[i] = Integer.MIN_VALUE;
		}
	}
	
	// same function as reentrantreadwritelock
	public long[] getUserId() {
		return userId;
	}

	public long[] getScore() {
		return score;
	}

	private void moveInsert(int index, long newUser, long newScore) {
		for (int i = 0; i < index; i++) {
			userId[i] = userId[i+1];
			score[i] = score[i+1];
		}
		
		userId[index] = newUser;
		score[index] = newScore;
	}
	
	/**
	 * insert new user and score
	 * 1. if use id exists in array, judge score, if new score is bigger, remove old entry and insert new
	 * 2. if new score is smaller than the first array element, just skip, we only counts biggest 15
	 * 3. when searching for proper insertion position, use binary search
	 * 4. after insert position is found, move element to get sparse space
	 * @param newUser
	 * @param newScore
	 */
	public synchronized void insert(long newUser, long newScore) {
		//filter low score
		if (newScore < score[0]) {
			return;
		}
		
		//find same user record
		for (int i = 0; i < userId.length; i++) {
			if (newUser == userId[i]) {
				if (newScore <= score[i]) {
					return;
				} else {
					//user in the list has a higher new score
					userId[i] = Integer.MIN_VALUE;
					score[i] = Integer.MIN_VALUE;
					for (int j = i - 1; j >= 0; j--) {
						userId[j+1] = userId[j];
						score[j+1] = score[j];
					}
					userId[0] = Integer.MIN_VALUE;
					score[0] = Integer.MIN_VALUE;
				}
			}
		}
		
        int mid = (TOPN - 1) / 2;   
        if (newScore == score[mid]) {   
        	moveInsert(mid,newUser,newScore);
        	return;
        }   
  
        int start = 0;   
        int end = (TOPN - 1);   
        while (start <= end) {   
            mid = (end - start) / 2 + start;   
  
            if (newScore < score[mid]) {   
               end = mid - 1;   
            } else if (newScore > score[mid]) {   
                start = mid + 1;   
            } else {   
            	moveInsert(mid,newUser,newScore);
            	return;   
            }   
        }   
        
//        System.out.println("start:"+start);
//        System.out.println("end:"+end);
        
        moveInsert(end,newUser,newScore);
	}

	public void print() {
		System.out.println("------------------");
		for (int i = 0; i < score.length; i++) {
			System.out.println(userId[i] + "->" + score[i]);
		}
	}

	
	@Override
	public String toString() {
		return "TopK [userId=" + Arrays.toString(userId) + ", score="
				+ Arrays.toString(score) + "]";
	}

	public static void main(String[] args) {
		TopK topK = new TopK();
		topK.insert(1, 100);
		topK.insert(2, 70);
		topK.insert(5, 80);
		topK.insert(3, 20);
		topK.insert(6, 100);
		topK.insert(7, 70);
		topK.insert(8, 80);
		topK.insert(9, 20);
		topK.insert(10, 100);
		topK.insert(12, 70);
		topK.insert(15, 80);
		topK.insert(14, 20);
		topK.insert(13, 100);
		topK.insert(11, 70);
		topK.insert(4, 80);
		topK.insert(16, 20);
		topK.print();
		topK.insert(16, 70);
		topK.print();
		
	}
}
