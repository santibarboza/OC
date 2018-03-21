package Analisis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Archivo {

	protected FileReader fr;
	protected BufferedReader br;
	
	public Archivo(String file) throws FileNotFoundException{
		File archivo = new File (file);
		fr= new FileReader (archivo);
		br = new BufferedReader(fr);
	}
	public String readLine() throws IOException{
		return br.readLine();
	}
	public void Close(){
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
