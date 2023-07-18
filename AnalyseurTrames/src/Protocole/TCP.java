package Protocole;

import java.util.List;



public class TCP {

	private final Octet[] Source_Port = new Octet[2];
    private final Octet[] Destination_Port = new Octet[2];
    private final Octet[] seqNum = new Octet[4];
    private final Octet[] ackNum = new Octet[4];
    private final DemiOctet THL;
    private final DemiOctet[] flags = new DemiOctet[6];
    private final Octet[] window = new Octet[2];
    private final Octet[] Checksum = new Octet[2];
    private final Octet[] urgentPointer = new Octet[2];
    private Octet[] options=new Octet[0];;
    private int payload;
    
    public TCP(List<String> list, int ipLen) {
        //ipLen est la taille des donnes ip
    	Source_Port[0] = new Octet(list.get(0));
        Source_Port[1] = new Octet(list.get(1));
        Destination_Port[0] = new Octet(list.get(2));
        Destination_Port[1] = new Octet(list.get(3));
        int j = 0;
        for (int i = 4; i < 8; i++)
            seqNum[j++] = new Octet(list.get(i));
        j = 0;
        for (int i = 8; i < 12; i++)
            ackNum[j++] = new Octet(list.get(i));
        
        THL = new DemiOctet(list.get(12).charAt(0));
        
        flags[0] = new DemiOctet(list.get(13).charAt(0));  //0b 0-0-URG-ACK
        flags[1] = new DemiOctet(list.get(13).charAt(1)); //Ob PSH-RST-SYN-FIN
        
        window[0] = new Octet(list.get(14));
        window[1] = new Octet(list.get(15));
        
        Checksum[0] = new Octet(list.get(16));
        Checksum[1] = new Octet(list.get(17));
        urgentPointer[0] = new Octet(list.get(18));
        urgentPointer[1] = new Octet(list.get(19));
        if(hasOption()) {
        	options=new Octet[get_header_length()-20];
        	for(int i=0;i<get_header_length()-20;i++) {
    			options[i]=new Octet(list.get(i+20));
    			}
        }
        
        payload=ipLen-get_header_length();
        }
    
    
    public int get_header_length() {
		return THL.getValue() * 4;
	}
	
	public boolean hasOption() {
		return get_header_length()>20;
	}
	
	public String is_set(char i) {
		if (i == '1') {
			return "Set";
		} else {
			return "Not set";
		}
	}
	
	public boolean hasHttp() {
		if( (Destination_Port[0].getValue() * 16 * 16 + Destination_Port[1].getValue())==80 || 
				Source_Port[0].getValue() * 16 * 16 + Source_Port[1].getValue()==80) return true;
		else return false;
	}
	
	 
	
	public String decoupageOptions() {

		StringBuilder sb = new StringBuilder();
		sb.append("\t\tOptions: (" + (get_header_length() - 20) + " bytes)");

		int i=0;
		while(i < options.length) {
			
			switch (options[i].getValue()) {
			case(0): {
				
				sb.append("\n\t\t\t TCP Option - ");
                sb.append("End of options list(EOOL)");
                i=options.length;
			}
			case(1):{
				 sb.append("\n\t\t\tTCP Option - ");
                 sb.append("No Operation (NOP)");
                 i++;
                 continue;
				
			}
			
			case(2): {
                sb.append("\n\t\t\tTCP Option - ");
                sb.append("Maximum Segment Size(MSS)");
                sb.append("\n\t\t\t  >kind: 2");
                sb.append("\n\t\t\t  >length: 4 bytes");
                sb.append("\n\t\t\t  >value: "+options[i+2].getValue()*16*16+options[i+3].getValue());
                i+=4;
                continue;
				
				
			}
			case(3):{
				sb.append("\n\t\t\tTCP Option - ");
                sb.append("Windows Scale WSopt(WSOPT)");
                sb.append("\n\t\t\t  >kind: 3");
                sb.append("\n\t\t\t  >length: 3 bytes");
                sb.append("\n\t\t\t  >value: "+options[i+2].getValue());
                i+=3;
                continue;}
			case(4): {
                sb.append("\n\t\t\tTCP Option - ");
                sb.append("SACK permitted");
                sb.append("\n\t\t\t  >Kind: 4");
                sb.append("\n\t\t\t  >length: 2 bytes");
                i += 2;
               continue;}
			case(8):{
				sb.append("\n\t\t\tTCP Option - ");
                sb.append("TSOPT - Time Stamp Option");
                sb.append("\n\t\t\t  >Kind: 8");
                sb.append("\n\t\t\t  >length: 10 bytes");
				int TSV=0;
				i+=2; int  j=i+4;
				while(i<j){
					TSV+=options[i].getValue(); 
					i++;
				}
				sb.append("\n\t\t\t  >STV:"+ TSV);
				
				int STER=0;
				j=i+4;
				while(i<j) {
					STER+=options[i].getValue(); i++;
			}
				sb.append("\n\t\t\t  >STER:"+ STER);
				
				continue; 
				}
			
			default: break;
			


	}
		
		
	}	
		
		return sb.toString();}

	 public int getSource_Port() {
		return (Source_Port[0].getValue() * 16 * 16 + Source_Port[1].getValue());
	}

	public int getWin() {
		return window[0].getValue() * 256 + window[1].getValue();
	}
	public int getDestination_Port() {
		return (Destination_Port[0].getValue() * 16 * 16 + Destination_Port[1].getValue());
	}


	public  long getSeqNum() {
		return (long)((seqNum[0].getValue() * 16777216 + seqNum[1].getValue() * 65536 +
                seqNum[2].getValue() * 256 + seqNum[3].getValue()) & 0xffffffffL);
	}


	public long getAckNum() {
		return (long)( (ackNum[0].getValue() * 16777216 + ackNum[1].getValue() * 65536 +
                ackNum[2].getValue() * 256 + ackNum[3].getValue()));
	}

	public int getPayload() {
		return payload;
	}
	
	public boolean ack() {
		return flags[0].toString().charAt(3)=='1';
		
	
	}
	public boolean syn() {
		return flags[1].toString().charAt(2)=='1';
		
	}
	
	public boolean fin() {
		return flags[1].toString().charAt(3)=='1';
		 
	}
	public boolean fin_ack() {
		return fin()&ack();
		
	}
	public boolean syn_ack(){
		return syn()&ack();
		
	}
	
	


	
	
	public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("\tTransmission Control Protocol:");
	        sb.append("\n\t\tSource Port: " + getSource_Port());
	        sb.append("\n\t\tDestination Port: " + getDestination_Port());
	        sb.append("\n\t\tSequence number: " + getSeqNum()) ;
	        
	        sb.append("\n\t\tAcknowledgement number: " +getAckNum());
	        
	        sb.append("\n\t\t"+ THL.toString()+"="+ "Header Length: " + get_header_length() + " bytes (" + THL.getValue() + ")");
	        
	        
	        sb.append("\n\t\tFlags: 0x0" +flags[0].getValue()+flags[1].getValue());
	        
	        sb.append("\n\t\t\t\t000 = Reserved " + "Not set");
	        sb.append("\n\t\t\t\t...0 = Nonce: " + "Not set");
	        sb.append("\n\t\t\t\t....0 = Congestion Window Reduced (CWR): " +"Not set");
	        sb.append("\n\t\t\t\t.....0 = ECN-Echo: " + "Not set");
	        
	        sb.append("\n\t\t\t\t......"+flags[0].toString().charAt(2)+" = Urgent: " + is_set(flags[0].toString().charAt(2)));
	        sb.append("\n\t\t\t\t........"+flags[0].toString().charAt(3)+" = Acknowledgement: " + is_set(flags[0].toString().charAt(3)));
	        
	        sb.append("\n\t\t\t\t.........."+flags[1].toString().charAt(0)+" = Push: " + is_set( (flags[1].toString().charAt(0)) ));
	        sb.append("\n\t\t\t\t..........."+flags[1].toString().charAt(1)+" = Reset: " + is_set(flags[1].toString().charAt(1)));
	        sb.append("\n\t\t\t\t............"+flags[1].toString().charAt(2)+" = Syn: " + is_set(flags[1].toString().charAt(2)));;
	        sb.append("\n\t\t\t\t............."+flags[1].toString().charAt(3)+" = Fin: " + is_set(flags[1].toString().charAt(3)));
	        
	        sb.append("\n\t\t Window");
	        sb.append("\n\t\t[Calculated Window size value: " + (window[0].getValue() * 256 + window[1].getValue())+"]");
	        
	        sb.append("\n\t\tChecksum: 0x" + Checksum[0].getHexValue() + Checksum[1].getHexValue());
	        sb.append("\n\t\tUrgent pointer: " + (urgentPointer[0].getValue() * 256 + urgentPointer[1].getValue()));
	        if(hasOption()) {
	        	sb.append(decoupageOptions());
	        }
	        sb.append("\n\t\tpayload ("+payload+" bytes)");
	        return sb.toString();
	    }

	
}
