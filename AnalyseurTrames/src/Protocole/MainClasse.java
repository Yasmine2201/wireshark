package Protocole;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.ScrollPane;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainClasse extends Application {

	private String fichier;


	public static void main(String[] args) { 
		launch(args); 
	}

	@Override 
	public void start(Stage primaryStage) throws Exception { 
		//SplitPane root = new SplitPane(); 
		VBox root=new VBox(20);

		HBox choix0=new HBox(70);
		choix0.setAlignment(Pos.CENTER);

		HBox choix=new HBox(70);
		choix.setAlignment(Pos.CENTER);

		Label l1=new Label("Décodeur de Trames");
		l1.setFont(new Font(20));
		Label l2=new Label("Visualiseur de flux");
		l2.setFont(new Font(20));

		l1.setTextFill(Color.RED);
		l2.setTextFill(Color.BLUE);


		Button b1=new Button("Choisissez un fichier");
		Button b2=new Button("Choisissez un fichier");


		choix0.getChildren().addAll(l1,l2);
		choix.getChildren().addAll(b1,b2);


		root.getChildren().addAll(choix0,choix);



		b1.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				FileChooser select=new FileChooser();

				fichier=select.showOpenDialog(primaryStage).getPath();
				decodage(fichier,root);

			}
		});



		b2.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				FileChooser select =new FileChooser();
				fichier=select.showOpenDialog(primaryStage).getPath();
				analyse(fichier,root);
			}

		});




		Scene scene= new Scene(root,500,500);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Analyseur et Decodeur de trames");

		primaryStage.show();


	}

	public void decodage(String fichier,VBox v) {

		Label t=new Label("");
		t.setFont(new Font("Times New Roman",15));
		ScrollPane scrol=new ScrollPane();
		scrol.setContent(t);

		int i=0; //permet de savoir où on est arrivé dans la liste trame1
		int fin;

		ConteneurTrame c=new ConteneurTrame(fichier);
		c.readFile();
		List<String> trame=c.getConteneur().get(0).getTrame();
		fin=trame.size();
		EthernetEntete e= new EthernetEntete(trame);

		if(e.hasPreambule()) {i=22;}
		else { i=14;}

		if(!e.typeToProtocol().equals("IPv4")) {
			t.setText(e.toString()+"\n\n\t\t\t protocole non_recconu");
			v.getChildren().addAll(t,scrol);

		}
		else {
			IPV4 ip=new IPV4(trame.subList(i, fin));

			i+=ip.get_header_length();

			if(ip.get_protocol().equals("TCP")) {
				TCP tcp=new TCP(trame.subList(i, fin),ip.get_total_length()-ip.get_header_length());
				i+=tcp.get_header_length();
				if(tcp.hasHttp() &tcp.getPayload()!=0) {
					HTTP http=new HTTP();

					t.setText(e.toString()+ip.toString()+tcp.toString()+http.toString(trame.subList(i, fin)));
					v.getChildren().addAll(t,scrol);
				}
				else {
					t.setText(e.toString()+ip.toString()+tcp.toString());
					v.getChildren().addAll(t,scrol);}
			}

			else {t.setText(e.toString()+ip.toString());
			
			v.getChildren().addAll(t,scrol);}
			
		}
		try {
		      FileWriter fich = new FileWriter(fichier.substring(0, fichier.length() - 4)+"decode.txt");
		      fich.write(t.getText());
		      fich.close();
		      }
		   catch (IOException e1) {
		      System.out.println("Une erreur est survenue à la creation du fichier.");
		      e1.printStackTrace();
		    }
		
			
			
		}
		
	






	private void analyse(String fichier, VBox v) {
		TextField filterField=new TextField();

		v.getChildren().add(filterField);

		ConteneurTrame c=new ConteneurTrame(fichier);
		c.readFile();
		List<Trame>frames=c.getConteneur();


		TableView<Trame> tableView=new TableView<Trame>();

		TableColumn<Trame,String> numColumn=new TableColumn<>("num"); 
		numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));

		TableColumn<Trame, String> ipSrcColumn=new TableColumn<>("ipSrc"); 
		ipSrcColumn.setCellValueFactory(new PropertyValueFactory<>("ipSrc")); 



		TableColumn<Trame, String> ipDesColumn=new TableColumn<>("ipDes"); 
		ipDesColumn.setCellValueFactory(new PropertyValueFactory<>("ipDes"));

		TableColumn<Trame, String> protocolColumn=new TableColumn<>("protocol"); 
		protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));




		TableColumn<Trame,String> portSrcColumn=new TableColumn<>("portSrc"); 
		portSrcColumn.setCellValueFactory(new PropertyValueFactory<>("portSrc"));

		TableColumn<Trame,String> portDestColumn=new TableColumn<>("portDes"); 
		portDestColumn.setCellValueFactory(new PropertyValueFactory<>("portDes"));

		TableColumn<Trame, String> infoColumn=new TableColumn<>("info"); 
		infoColumn.setCellValueFactory(new PropertyValueFactory<>("info"));

		TableColumn<Trame, String> detailsColumn=new TableColumn<>("details"); 
		detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));

		TableColumn<Trame, String> graphColumn=new TableColumn<>("graph"); 
		graphColumn.setCellValueFactory(new PropertyValueFactory<>("graph"));


		tableView.getColumns().addAll(numColumn,ipSrcColumn,ipDesColumn,protocolColumn,portSrcColumn,portDestColumn,infoColumn,detailsColumn,graphColumn);


		//ObservableList<Trame> dataList=FXCollections.observableArrayList();

		ObservableList<Trame> dataList = 
				FXCollections.observableArrayList();

		for(int i=0;i<frames.size();i++) {
			frames.get(i).remplissage(i);
			tableView.getItems().add(frames.get(i));
			dataList.add(frames.get(i));
		}



		FilteredList<Trame> filteredData=new FilteredList<>(dataList,b->true);

		filterField.textProperty().addListener((observable,oldvalue,newvalue)->{
			filteredData.setPredicate(trame->{
				if(newvalue==null || newvalue.isEmpty()) return true;

				String lowerCaseFilter= newvalue.toLowerCase();
				if(trame.getIpSrc().contains(lowerCaseFilter)) return true;
				if(trame.getIpDes().contains(lowerCaseFilter)) return true;
				if(trame.getProtocol().toLowerCase().contains(lowerCaseFilter)) return true;

				if(trame.getPortDes().toLowerCase().contains(lowerCaseFilter)) return true;
				if(trame.getPortSrc().toLowerCase().contains(lowerCaseFilter))return true;

				return false;

			});

		});

		SortedList<Trame> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);

		v.getChildren().add(tableView);





	}



}

