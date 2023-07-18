package Protocole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EthernetEntete {

	//l'entete Ethernet contient trois champs qu'on va stocker dans trois tableaux
	
	private final Octet[] preambule=new Octet[8] ;
	private final Octet[] adr_source=new Octet[6] ;
	private final Octet[] adr_dest=new Octet[6];
	private final Octet[] type =new Octet[2];
	private List<String> liste;
	
	public EthernetEntete(List<String> liste) {
		/*les String de la liste transmise contiennent deux caractères hexadécimaux representant un octet*/
		
		this.liste=liste;
		if(hasPreambule()){
			int i, j = 0;
			
			for (i = 0; i < 8; i++)
				preambule[j++] = new Octet(liste.get(i));		//preambule
			
			j=0;
			for (i = 8; i < 14; i++)
				adr_dest[j++] = new Octet(liste.get(i));		//destination adresse

			j = 0;
			for (i = 14; i < 20; i++)
				adr_source[j++] = new Octet(liste.get(i));	//source adresse

			j = 0;
			for (i = 20; i < 22; i++)
				type[j++] = new Octet(liste.get(i));			//protocol type

		
		
		}
		else {
		
			{
				int i, j = 0;

				for (i = 0; i < 6; i++)
					adr_dest[j++] = new Octet(liste.get(i));		//destination adresse

				j = 0;
				for (i = 6; i < 12; i++)
					adr_source[j++] = new Octet(liste.get(i));	//source adresse

				j = 0;
				for (i = 12; i < 14; i++)
					type[j++] = new Octet(liste.get(i));			//protocol type

			}
		}
	}
	public boolean hasPreambule() {

		List<String> preambuleStr=liste.subList(0, 8);
		List<String>preambuleStr2=new ArrayList<>();
		
		
		String [] tab=  {"AA","AA","AA","AA","AA","AA","AA","AB"};
		preambuleStr2.addAll(Arrays.asList(tab));
		
		
		int i=0;
		for(String s:preambuleStr) {
			if(!(s.equals(preambuleStr2.get(i)) )) return false;
			i+=1;
		}
		return true;
		}
	
	public boolean isBroadcast()
	{
		for (Octet b : adr_dest)
			if (b.getValue() != 255)
				return false;
		return true;
	}
	
	
	public String typeToProtocol()
	{
		String str = type[0].getHexValue() + type[1].getHexValue();
		switch(str)
		{
			case "0800": return "IPv4";
			case "0806": return "ARP";
			case "0842": return "Wake-on-LAN";
			case "22F0": return "AVTP";
			case "22F3": return "IETF TRILL Protocol";
		}
		return "protocole non_recoonu";	
	}

	public String toString() {
		if(!hasPreambule()) {
			StringBuilder sb = new StringBuilder();
			int i;
			
			sb.append("\tEthernet II:\n\t\t");
			sb.append("Adresse MAC Destination: ");
			
			if (isBroadcast())
			{
				sb.append("Broadcast (");
				for (i = 0; i < 6; i++)
				{	
					sb.append(adr_dest[i].getHexValue());
					if (i != 5)
						sb.append(":");
				}
				sb.append(")");
			}
			else
				for (i = 0; i < 6; i++)
				{	
					sb.append(adr_dest[i].getHexValue());
					if (i != 5)
						sb.append(":");
				}
			
			sb.append("\n\t\t").append("Adresse MAC Source: ");
			for (i = 0; i < 6; i++)
			{	
				sb.append(adr_source[i].getHexValue());
				if (i != 5 )
					sb.append(":");
			}
				
		
			
			sb.append("\n\t\t").append("Type: 0x");
			sb.append(type[0].getHexValue()).append(type[1].getHexValue()).append(" (").append(typeToProtocol()).append(")\n");
			
			return sb.toString();
		}
		else {
			StringBuilder sb = new StringBuilder();
				int i;
				
				sb.append("\t Ethernet II: (cette trame contient un preambule!)\n\t\t");
				sb.append("Preambule: ");
				
				for (i = 0; i < 8; i++)
				{	
					sb.append(preambule[i].getHexValue()).append(" ");
					
				}
				
				sb.append("\n\t\t");
				sb.append("Adresse MAC Destination: ");
				
				if (isBroadcast())
				{
					sb.append("Broadcast (");
					for (i = 0; i < 6; i++)
					{	
						sb.append(adr_dest[i].getHexValue());
						if (i != 5)
							sb.append(":");
					}
					sb.append(")");
				}
				else
					for (i = 0; i < 6; i++)
					{	
						sb.append(adr_dest[i].getHexValue());
						if (i != 5)
							sb.append(":");
					}
				
				sb.append("\n\t\t").append("Adresse MAC Source: ");
				for (i = 0; i < 6; i++)
				{	
					sb.append(adr_source[i].getHexValue());
					if (i != 5 )
						sb.append(":");
				}
					
			
				
				sb.append("\n\t\t").append("Type: 0x");
				sb.append(type[0].getHexValue()).append(type[1].getHexValue()).append(" (").append(typeToProtocol()).append(")\n");
				
				return sb.toString();
			 
		}
		
	}
		
		
		
}
	
	
