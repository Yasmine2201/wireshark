package Protocole;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class Trame {

	 
	private List <String>trame=new ArrayList<>();
	private String num;
	private String ipSrc;
	private String ipDes;
	private String portSrc;
	private String portDes;
	private String protocol;
	private String info;
	private Label graph;
	private Label details;
	
	public Trame() {}
	
	
	public void remplissage(int num) {
		
		this.num=""+num;
		int fin=trame.size();
		
		if(new EthernetEntete(trame).typeToProtocol().equals("IPv4")){ 
			IPV4 ipv4= new IPV4(trame.subList(14, fin));
			ipSrc = ipv4.getIpSrc();
			ipDes =ipv4.getIpDes(); 
			
			protocol= ipv4.get_protocol();
			
			if(protocol.equals("TCP")) {
				TCP tcp=new TCP(trame.subList(14+ ipv4.get_header_length(), fin),ipv4.get_total_length()-ipv4.get_header_length());
				portSrc = ""+tcp.getSource_Port();
				portDes = ""+tcp.getDestination_Port();
				f(tcp,14+ ipv4.get_header_length()+tcp.get_header_length(),fin);
				
			}
			else {
				protocol=ipv4.get_protocol();
				portSrc = ""+0;
				portDes =""+0;
				info="Pas d'informatins sur ce protocole";
				details=new Label("Pas de details sur ce protocole");
				details.setTextFill(Color.BLUEVIOLET);
				graph=new Label("");
			}
		}
		
		else {ipSrc="";
			ipDes="";
			protocol="";
			portSrc=""+0;
			portDes=""+0;
			info="Pas d'informatins sur cette trame";
			details=new Label("Pas de details sur cette trame");
			details.setTextFill(Color.BLUE);
			graph=new Label("");
		}
		
		
		
		
		
	}
	

	public String getNum() {
		return num;
	}
	public String getIpSrc() {
		return ipSrc;
	}


	public String getIpDes() {
		return ipDes;
	}


	public String getPortSrc() {
		return portSrc;
	}


	public String getPortDes() {
		return portDes;
	}


	public String getProtocol() {
		return protocol;
	}


	public String getInfo() {
		return info;
	}
	
	public Label getGraph() {
		return graph;
	}
	public Label getDetails() {
		return details;
	}

	private void f(TCP tcp, int i,int fin) {
		
		
		String s1= tcp.getSource_Port()+"-->"+tcp.getDestination_Port();
		String s2=" Win="+tcp.getWin()+" Len="+tcp.getPayload();
		
		info=s1+s2;
		
		if(tcp.hasHttp() & (tcp.getPayload()!=0)) {
			protocol="HTTP";
			
			HTTP http=new HTTP();
			info="HTTP:"+http.toString1(trame.subList(i, fin));
			details=new Label("traffic web détecté");
			details.setTextFill(Color.RED);
		}
		
		
		else {
			if(tcp.ack()) {
		
				if(tcp.syn_ack()) {
				
				info="TCP"+s1+" [SYN,ACK]"+" Seq="+tcp.getSeqNum()+" Ack="
						+tcp.getAckNum()+s2;
				graph=new Label(ipSrc+"\t\t\t\t\t"+"\t\t"+ipDes+"\n\t\t"+tcp.getSource_Port() +"  -----------[SYN,ACK]----------->"+tcp.getDestination_Port());
				
				graph.setTextFill(Color.BROWN);
				graph.setFont(Font.font("Verdana",FontWeight.BOLD,FontPosture.ITALIC, 12));
				details=new Label("Demande de connexion et acquittement du SYN Client");
				details.setTextFill(Color.BROWN);
				
				}


				else if(tcp.fin_ack()) {
				
				info="TCP:"+ s1+" [FIN,ACK]"+" Seq="+tcp.getSeqNum()+" Ack="
						+tcp.getAckNum()+s2;
				graph=new Label(ipSrc+"\t\t\t\t\t"+"\t\t"+ipDes+"\n\t\t"+tcp.getSource_Port()+" -----------[FIN,ACK]----------->"+tcp.getDestination_Port());
				graph.setTextFill(Color.BURLYWOOD);
				graph.setFont(Font.font("Verdana",FontWeight.BOLD,FontPosture.ITALIC, 12));
				details=new Label("Acquittement et Demande de fermeture de la connexion");
				details.setTextFill(Color.BURLYWOOD);
				}

				else if(tcp.getPayload()==0){
				
				info="TCP:"+ s1+" [ACK]"+" Seq="+tcp.getSeqNum()+" Ack="
						+tcp.getAckNum()+s2;
				graph=new Label(ipSrc+"\t\t\t\t\t"+"\t\t"+ipDes+"\n\t\t"+tcp.getSource_Port()+" -------------[ACK]------------- >"+tcp.getDestination_Port());
				graph.setTextFill(Color.CADETBLUE);
				graph.setFont(Font.font("Verdana",FontWeight.BOLD,FontPosture.ITALIC, 12));
				details=new Label("Acquittement de la trame reçue");
				details.setTextFill(Color.CADETBLUE);
				}
						
				}
			else {
				if(tcp.syn()) { 
				
				info="TCP:"+ s1+" [SYN]"+" Seq="+tcp.getSeqNum()+s2+" MSS=1460";
				graph=new Label(ipSrc+"\t\t\t\t\t"+"\t\t"+ipDes+"\n\t\t"+tcp.getSource_Port()+" -------------[SYN]------------->"+tcp.getDestination_Port());
				graph.setTextFill(Color.CHARTREUSE);
				graph.setFont(Font.font("Verdana",FontWeight.BOLD,FontPosture.ITALIC, 12));
				details=new Label("Demande de connexion");
				details.setTextFill(Color.CHARTREUSE);
				}


				else if(tcp.fin()) {
				
				info="TCP:"+ tcp.getSource_Port()+"-->"+tcp.getDestination_Port()+" [FIN]"+" Seq="+tcp.getSeqNum()+s2;
				graph=new Label(ipSrc+"\t\t\t\t\t"+"\t\t"+ipDes+"\n\t\t"+tcp.getSource_Port()+" -------------[FIN]------------- >"+tcp.getDestination_Port());
				graph.setTextFill(Color.CHOCOLATE);
				graph.setFont(Font.font("Verdana",FontWeight.BOLD,FontPosture.ITALIC, 12));
				details=new Label("Demande de fermeture de la connexion");
				details.setTextFill(Color.CHOCOLATE);
				}
			
		}
		
	}
	}

	public void add(String s) {
		trame.add(s);
	}
	
	public List<String> getTrame() {
		return trame;
	}
	
	
	







}

