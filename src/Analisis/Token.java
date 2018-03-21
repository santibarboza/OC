package Analisis;

public class Token {
	protected String ID_TOKEN;
	protected int Lexema;
	protected String Etiqueta;
	protected int Nro_Linea;
	protected int Nro_Col;
	
	public Token(String id,int lex, int nro,int col){
		ID_TOKEN=id;
		Lexema=lex;
		Nro_Linea=nro;
		Nro_Col=col;
		Etiqueta="";
	}

	public Token(String id,String etiq, int nro,int col){
		ID_TOKEN=id;
		Lexema=300;
		Etiqueta=etiq;
		Nro_Linea=nro;
		Nro_Col=col;
	}
	public String get_IDTOKEN(){
		return ID_TOKEN;
	}
	public String get_Etiqueta(){
		return Etiqueta;
	}
	public int get_Lexema(){
		return Lexema;
	}
	public int get_NroLinea(){
		return Nro_Linea;
	}
	public int get_NroCol(){
		return Nro_Col;
	}

}
