package com.king.datastructure;


/**
 * The minimum value is stored in the first position
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
	
	public void insert(long newUser, long newScore) {
		if (newScore < score[0]) {
			return;
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
		for (int i = 0; i < score.length; i++) {
			System.out.println(userId[i] + "->" + score[i]);
		}
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
		topK.insert(17, 20);
		topK.insert(16, 100);
		topK.insert(11, 70);
		topK.insert(4, 80);
		topK.insert(16, 20);
		topK.insert(16, 10);
		topK.print();
		
	}
}
