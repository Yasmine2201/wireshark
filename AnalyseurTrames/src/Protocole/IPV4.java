package Protocole;

import java.util.List;

public class IPV4 {

	private final DemiOctet version;
	private final DemiOctet IHL; // header length exprime en mots de 4 octets 
	private final Octet DSF; //6 bits sont utilisés pour le champ Differentiated Services Code Point (DSCP) et 2 bits pour le champ Explicit Congestion Notification. 
	private final Octet[] Total_Length = new Octet[2];
	private final Octet[] Identification = new Octet[2];
	private final Octet[] Flags = new Octet[2];
	private final Octet TTL;
	private final Octet Protocol;
	private final Octet[] Checksum = new Octet[2];
	private final Octet[] source_protocol_adr = new Octet[4]; //source ip adresse
	private final Octet[] dest_protocol_adr = new Octet[4]; //dest ip adresse
	public Octet[] options =new Octet[0];
	private final Octet[] routeursIP=new Octet[36];//nbmax de routeurs=9(chaque IP étant codé sur 4 octet)

	public IPV4(List<String> list) {
		version = new DemiOctet(list.get(0).charAt(0)); // premier caractere de list
		IHL = new DemiOctet(list.get(0).charAt(1)); // deuxieme caractere de list
		DSF= new Octet((list.get(1)));
		for(int i=0;i<2;i++) {
			Total_Length[i]= new Octet(list.get((i+2)));
		}
		for(int i=0;i<2;i++) {
			Identification[i]= new Octet(list.get((i+4)));
		}
		for(int i=0;i<2;i++) {
			Flags[i]= new Octet(list.get((i+6)));
		}
		TTL=new Octet(list.get(8));
		Protocol=new Octet(list.get(9));
		for(int i=0;i<2;i++) {
			Checksum[i]= new Octet(list.get((i+10)));
		}
		for(int i=0;i<4;i++) {
			source_protocol_adr[i]= new Octet(list.get((i+12)));
		}
		for(int i=0;i<4;i++) {
			dest_protocol_adr[i]= new Octet(list.get((i+16)));
		}
		
		if(hasOption()) {
			options=new Octet[get_header_length()-20];
			for(int i=0;i<get_header_length()-20;i++) {
			options[i]=new Octet(list.get(i+20));}
		}

	}
	public int get_header_length() {
		return IHL.getValue() * 4;
	}
	public int get_total_length() {
		return Total_Length[0].getValue() * 16 * 16 + Total_Length[1].getValue();
	}
	public boolean hasOption() {
		return get_header_length()>20;
	}
	
	public String get_protocol() {
		int i = Protocol.getValue();
		switch (i) {
		case 1: return "ICMP";
		case 2: return "IGMP";
		case 6: return "TCP";
		case 8: return "EGP";
		case 17: return "UDP";
		case 36: return "XTP";
		default: return "aia e";
		}
	}
	public String is_set(int i) {
		if (i == 1) {
			return "Set";
		} else {
			return "Not set";
		}
	}

	public String decoupageRouteurs(int ind) {
		//appelée si l'option est 7(Recorded Route) et on lui passe en parametre l'indice du debut de l'option
		
		int pointeur=options[ind+2].getValue();
		String s="";
		int nbIP=options[ind+1].getValue()-3;//nombre d'@IP
		//la 1ere @IP est à l@ ind+3
		for(int j=0;j<nbIP;j++) {
				routeursIP[j]=options[ind+3+j];
				
			}
			s+="\t\t\t  >Pointeur: "+pointeur+"\n\t\t\t * Recorded Route: ";
			for(int i=0;i<nbIP;i++) {

				if(i%4==3 &i!=nbIP-1) {s+=routeursIP[i].getValue()+"\n\t\t\t * Recorded Route: ";}	
				if(i%4==3 &i==nbIP-1) s+=routeursIP[i].getValue()+"\n";
				else s+=routeursIP[i].getValue()+".";
				
			}
			return s;
	}
		

	
	public String option(int type) { //associer à chaque type d'option le nom de cette option
		switch (type) {
		case 0: return "IP Option - End of Options List(EOOL)";
		case 1: return "IP Option - No Operation(NOP)";
		case 7: return "IP Option - Recorded Route (RR)";
		case 68: return "IP Option - Time Stamp (TS)";
		case 131: return "IP Option - Loose Source Route (LSR)";
		case 137: return "IP Option - Strict Source Route(SSR)";
		default: return "unrecognized type";
		}



	}
	public String getIpSrc() {
		return source_protocol_adr[0].getValue() + "." + source_protocol_adr[1].getValue() + "."
				+ source_protocol_adr[2].getValue() + "." + source_protocol_adr[3].getValue();
	}

	public String getIpDes() {
		return dest_protocol_adr[0].getValue() + "." + dest_protocol_adr[1].getValue() + "."
				+ dest_protocol_adr[2].getValue() + "." + dest_protocol_adr[3].getValue();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tInternet Protocol Version: ").append(version);
		sb.append("\n\t\t"+version.toString()+ " = Version:" +" "+version.getValue());
		
		sb.append("\n\t\tHeader Length: " + IHL.getValue() * 4 + " Octets (" + IHL.getValue() + ")");
		sb.append("\n\t\tDifferentiated Services Field: 0x" + DSF.getHexValue());
		sb.append("\n\t\t\t\tDifferentiated Services Codepoint: " + DSF.getValue() / 4); //6 premiers bits(shift 2bits à droite)
		sb.append("\n\t\t\t\tExplicit Congestion Notification: " + DSF.getValue() % 4); //2derniers bits
		sb.append("\n\t\tTotal Length: " + get_total_length());
		int x = Identification[0].getValue() * 16 * 16+Identification[1].getValue();
		sb.append("\n\t\tIdentification: 0x" + Identification[0].getHexValue() + Identification[1].getHexValue() + " (" + x + ")");

		sb.append("\n\t\tFlags: 0x" + Flags[0].getHexValue() );
		sb.append("\n\t\t\t\tReserved bit: " +  is_set(Flags[0].getValue() / 128));
		sb.append("\n\t\t\t\tDon't Fragment: " +  is_set(Flags[0].getValue() / 64));
		sb.append("\n\t\t\t\tMore Fragments: " +  is_set(Flags[0].getValue() / 32));
		sb.append("\n\t\t\t\tFragment Offset: " +  ((Flags[0].getValue() % 2) * 128 + Flags[1].getValue()));
		sb.append("\n\t\tTime to live: " + TTL.getValue());
		sb.append("\n\t\tProtocol: " + get_protocol() + " (" + Protocol.getValue() + ")");
		sb.append("\n\t\tHeader checksum: 0x" + Checksum[0].getHexValue() + Checksum[1].getHexValue());
		sb.append("\n\t\tSource: " + getIpSrc());
		sb.append("\n\t\tDestination: " + getIpDes() + "\n");

		// Les options sont codées sur le principe TLV (Type, Longueur, Valeur)
		if(hasOption()) {
			sb.append("\t\tOptions:\n");
			int i=0; int type;
			while(i<options.length) {
				type=options[i].getValue();
				if(type!=0 & type!=1) {
					sb.append("\t\t\t"+option(type)+"\n");
					sb.append("\t\t\t  >Type: "+type+"\n"); //type
					sb.append("\t\t\t  >Length: "+options[i+1].getValue()+"\n"); //longueur

					if(type==7) {sb.append(decoupageRouteurs(i));
						
							i=options[i+1].getValue(); //on saute à la prochaine option
							}
					else {sb.append("\t\t\t"+option(type)+"\n");
						i=options.length; //cest une option qu'on ne sait pas décoder donc on sort de la boucle
					}
				}
				if(type==1) {
					sb.append("\t\t\t"+option(type)+"\n");
					i++;  //on passe à l'iteration suivante
				}

				else { 
					sb.append("\t\t\t"+option(type)+"\n");
					i=options.length;//quand type==0
				}
				//l'option n'est pas de la forme(type,taille,valeur) mais uniquement type comme l'option (EOOL) "padding"		



			}

		}

		return sb.toString();

	
}

}



