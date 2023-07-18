
package Protocole;

public class Octet {

	private int value; // la valeur décimale de l'octet lu
	
	public Octet(String c) {
		value=Integer.decode("0x"+c);
	}
	
	public String getHexValue() { //renvoie un entier hexadecimal representant le caractere lu codé sur deux sympboles hexadecimaux
		
		if (value >= 16)
			return Integer.toHexString(value).toUpperCase();
		
		else return "0" + Integer.toHexString(value).toUpperCase(); 
	}
	
	
	
	public int getValue() {
		return value;
	}
	
	
}

