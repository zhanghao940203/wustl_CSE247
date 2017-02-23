package kwaymergesort;

import java.util.ArrayList;
import java.util.Arrays;

import timing.Ticker;

public class KWayMergeSort {
	
	/**
	 * 
	 * @param K some positive power of 2.
	 * @param input an array of unsorted integers.  Its size is either 1, or some other power of 2 that is at least K
	 * @param ticker call .tick() on this to account for the work you do
	 * @return
	 */
	public static Integer[] merge2(Integer[] a1, Integer[] a2, Ticker ticker) {
		int i=0;
		int j=0;
		int c=0;
		Integer[] ans = new Integer[a1.length+a2.length];
		while(i<a1.length||j<a2.length){
			if(i==a1.length){
				ans[c]=a2[j];
				j++;
				c++;
				ticker.tick();
				continue;
			}
			if(j==a2.length){
				ans[c]=a1[i];
				i++;
				c++;
				ticker.tick();
				continue;
			}
			if(a1[i]<a2[j]){
				ans[c]=a1[i];
				i++;
				c++;
				ticker.tick();
			}
			else{
				ans[c]=a2[j];
				c++;
				j++;
				ticker.tick();
			}
			//System.out.println(ans[c]+" ");
		}
		return ans;
	}
	
	public static Integer[][] merge(Integer[][] a, Ticker ticker){
		int l=a.length;
		//System.out.println(l);
		Integer[][] ans=new Integer[l/2][];
		int j=0;
		for(int i=0;i<l;i=i+2){
			ans[j]=merge2(a[i],a[i+1],ticker);
			j++;
			ticker.tick();
		}
		if(ans.length>1){
			ans=merge(ans,ticker);
			ticker.tick();
		}

		return ans;

	}
	
	public static Integer[] kwaymergesort(int K, Integer[] input, Ticker ticker) {
		int n = input.length;
		int m = n/K;
		Integer[] ans = new Integer[n];
		Integer[][] array = new Integer[K][];
		Integer[][] array2 = new Integer[K][];
		if(n==1){
			ans=input;
			ticker.tick();
		}
		if(n==2){
			if(input[0].compareTo(input[1])>0){
				ans[0]=input[1];
				ans[1]=input[0];
				ticker.tick();
			}
			else{
				ans[0]=input[0];
				ans[1]=input[1];
				ticker.tick();
			}
		}
		else{
			int s=0;
			for(int i=0;i<K;i++){
				array[i]=Arrays.copyOfRange(input,s,s+m);
				s=s+m;
				ticker.tick();
//				for(int j=0;j<m;j++){
//					array[i][j]=input[m*i+j];
				//System.out.println(array[i][j]+" ");
				//}
				if(array[i].length>1){
					array2[i]=kwaymergesort(K,array[i],ticker);
					ticker.tick();
				}
				else{
					array2[i]=array[i];
					ticker.tick();
				}
			}
			
			ans=merge(array2,ticker)[0];
			ticker.tick();
		}

		return ans;
	
		//
		// FIXME
		// Following just copies the input as the answer
		//
		// You must replace the loop below with code that performs
		// a K-way merge sort, placing the result in ans
		//
		// The web page for this assignment provides more detail.
		//
		// Use the ticker as you normally would, to account for
		// the operations taken to perform the K-way merge sort.
		//	
	}
}
