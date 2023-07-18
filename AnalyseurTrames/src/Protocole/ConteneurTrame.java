package Protocole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConteneurTrame {

	private List<Trame> conteneur=new ArrayList<>();

	private String fichier;

	public ConteneurTrame(String f) {

		fichier=f;}

	public static boolean isHex(String str)
	{
		if (str.length()!= 2)	  return false;


		for (int i = 0; i < str.length(); i++)
		{
			if (!((Character.toUpperCase(str.charAt(i)) >= 'A' && Character.toUpperCase(str.charAt(i)) <= 'F') || Character.isDigit(str.charAt(i)) ) )
				return false;	//return false si on a un caractere qui n'est pas un symbol valide en hexa (A, B, C, D, E, F) ou si on n'a pas un caractere qui est un chiffre
		}
		return true;
	}

	
	public void readFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fichier));

			String line; 
			int i=-1;
			while ((line = br.readLine())!=null) {

				for (String word : line.split(" ")) {
					if(word.equals("0000")){
						conteneur.add(new Trame());
						i++;}


					if(isHex(word)) {
						conteneur.get(i).add(word);
					}


				}	
			}
			br.close();
		}


		catch (IOException e1) {
			System.out.println("Il y a un problème avec le fichier que vous essayez de lire");
			e1.printStackTrace();
		}


	}
	

	public List<Trame> getConteneur() {
		return conteneur;
	}




}
