package rabinkarp;

public class RK {
	char[] chars;
	int count=0;
	int buk=0;
	//
	// Be sure to look at the write up for this assignment
	//  so that you get full credit by satisfying all
	//  of its requirements
	//
	

	/**
	 * Rabin-Karp string matching for a window of the specified size
	 * @param m size of the window
	 */
	public RK(int m) {
		chars = new char[m];
	}
	

	/**
	 * Compute the rolling hash for the previous m-1 characters with d appended.
	 * @param d the next character in the target string
	 * @return
	 */
	public int nextCh(char d) {
		   //System.out.println(d);
		   int m = chars.length;
		   int ans=0;
		   int val=d;
		   int c=count%m;
		   int mpower = powermod(31,m);
		   //System.out.println(c);
		   //buk=((((buk%511)*(31%511)))-((((mpower)%511)*(chars[c]%511))%511)+(val%511))%511;
		   buk = mod((buk*31-mpower*chars[c]+val),511);
		   chars[c]=d;
		   count++;
		   ans=buk;
		   //System.out.println(ans);
		   return ans;
	}
	
	public int mod(int a, int b){
		int ans =(a%b+b)%b;
		return ans;
	}
	
	public int powermod(int a, int p){
		int ans=1;
		for(int i=0;i<p;i++){
			ans = mod(ans*a,511);
		}
		return ans;
	}

}
