package Protocole;


public class DemiOctet {

	private Integer[]bits={0,0,0,0};
	
	private int value;
	public DemiOctet(Character c) {
		
		value=Integer.decode("0x"+c);
		
		int quotient=value;
		int i=3;
		while( quotient !=0) {
			bits[i]=quotient%2;
			quotient=quotient/2;
			i-=1;
			
			}
		
		}
		public int getValue() {
			return value;
		}
		public String toString() {
			String s="";
			for(Integer i: bits) {
				s+=i;
			}
			return s;
		}
		
		
	}
	
	

