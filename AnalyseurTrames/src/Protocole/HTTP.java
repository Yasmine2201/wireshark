package Protocole;

import java.util.List;

public class HTTP {

	public String toString(List<String> list) {

		StringBuilder sb=new StringBuilder();
		for(String s:list) {

			sb.append(ascii(s)+" ");


		}
		return sb.toString();


	}

	public String toString1(List<String> list) {
		StringBuilder sb=new StringBuilder();
		
		for(String s:list) {
			if(!(s.equals("0d"))) sb.append(ascii(s));
			else break;
		}
		return sb.toString();
		
		
	}
	public String ascii (String s) {
		Octet o=new Octet(s);
		return ""+(char) o.getValue();
	}

}

