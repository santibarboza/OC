package Analisis;
import java.io.IOException;
import java.util.Hashtable;

import Tokens.Token;
import Tokens.TokenEOF;
import Tokens.TokenSalto;

import Excepciones.ErrorLexico;

public class Alex_AnalizadorLexico {
	protected Archivo file;
	protected String linea;
	protected int indexLine;
	protected int nro_linea;
	protected static String[] sentencias=
		{"T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaMemoria",
		 "T_SentenciaMemoria",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaT3",
		 "T_SentenciaT3",
		 "T_SentenciaT3",
		 "T_Halt"};
	protected static Hashtable<String,Integer>opcodes;
	protected Hashtable<Character,String> casosTriviales;
	
	public Alex_AnalizadorLexico(String filename) throws IOException{
		nro_linea=0;
		file= new Archivo(filename);
		
		recargarLinea();
		CargarSentencias();
		cargarTriviales();
	}
	
	
	private Token recargarLinea() throws IOException{
		indexLine=0;
		do{
			nro_linea++;
			linea=file.readLine();
		}while(lineaVacia(linea));
	
		return new TokenSalto(nro_linea,indexLine);
	}
	private boolean lineaVacia(String linea){
		return linea!=null && linea.length()==0;
	}
	
	/**
	 * Retorna un Token por Demanda
	 */
	public Token getToken()throws ErrorLexico,IOException{
		if(terminoLinea())
			return recargarLinea();
		if(terminoArchivo())
			return findeArchivo();

		char caracterActual=linea.charAt(indexLine);
		String T_ID=casosTriviales.get(caracterActual);
		
		if(esEspacioBlanco())
			return saltarBlancos();
		else if(esTrivial(T_ID))
				return analizarCasoTrivial(T_ID,caracterActual);
			else if(caracterActual=='/')
					return analizarComentarios();
				else if(esOffsetNegativo())
						analizarDesplazamientoNegativo();
					else if(esDigito())
							return analizarDigito();
						else if(esRegistro())
								return analizarRegistro();
							else if(esDireccion())
									return analizarDireccion();
								else if(esLetra())
									   return analizarIdentificador();
		
		throw new ErrorLexico("ErrorLexico en "+nro_linea+":"+indexLine+"= El caracter "+caracterActual+" no esta valido en el lenguaje");
	}

	

	private boolean terminoLinea(){
		return linea!=null && indexLine== linea.length();
	}
	private boolean terminoArchivo(){
		return linea==null;
	}
	private boolean esEspacioBlanco(){
		char caracterActual=linea.charAt(indexLine);
		return caracterActual==' ' || caracterActual==(char)9;
	}
	private boolean esTrivial(String T_Id){
		return T_Id!=null;
	}
	private boolean esSiguienteCaracter(char nextChar){
		return indexLine<(linea.length()-1) && linea.charAt(indexLine+1)==nextChar;
	}
	private boolean esOffsetNegativo(){
		return linea.charAt(indexLine)=='-' && digitoDe0a8(indexLine+1);
	}
	private boolean esDigito(){
		char caracterActual= linea.charAt(indexLine);
		return (caracterActual>'0' && caracterActual<'9'); 
	}
	private boolean esRegistro(){
		return linea.charAt(indexLine)=='R' &&digitoHexa(indexLine+1)&& !esCaracterIdentificador(indexLine+2);
	}
	private boolean esDireccion(){
		return digitoHexa(indexLine)&& digitoHexa(indexLine+1);
	}
	private boolean esLetra(){
		char caracterActual= linea.charAt(indexLine);
		return (caracterActual>='A' && caracterActual<='Z')||(caracterActual>='z' && caracterActual<='z');
	}
	private boolean digitoHexa(int indexLine){
		if(indexLine>=linea.length())
			return false;
		char caracter=linea.charAt(indexLine);
		return ((caracter>='0' && caracter<='9')||(caracter>='A' && caracter<='F'));
	}
	private boolean digitoDe0a8(int indexLine){
		if(indexLine>=linea.length())
			return false;
		char caracter=linea.charAt(indexLine);
		return (caracter>='0' && caracter<='8');
	}
	private Token analizarCasoTrivial(String T_ID,char caracterActual){
		indexLine++;
		return new Token(T_ID,caracterActual+"",nro_linea,indexLine+1);
	}
	private Token analizarComentarios() throws ErrorLexico, IOException{
		if(esSiguienteCaracter('/'))			
			return comentarioSimple();
		else if(esSiguienteCaracter('*'))
			return comentarioMultilinea();
		else
			throw new ErrorLexico("Error Lexico en "+nro_linea+":"+indexLine+" = El caracter siguiente a / no es valido en el lenguaje");
	}
	private Token analizarDesplazamientoNegativo(){
		int i=(linea.charAt(indexLine-1)-48); 
		int complemento=16-i;
		indexLine+=2;
		return new Token("Lit_Desp",complemento,nro_linea,indexLine);
	}
	private Token analizarDigito() throws ErrorLexico{
		char caracterActual=linea.charAt(indexLine);
		indexLine++;
		if(digitoHexa(indexLine)){
			indexLine++;
			int i=Integer.parseInt(""+caracterActual+linea.charAt(indexLine-1),16);
			return new Token("Lit_Dir",i,nro_linea,indexLine-1);
		}else if(caracterActual<='7')
			return new Token("Lit_Desp",(caracterActual-48),nro_linea,indexLine);
		else
			throw new ErrorLexico("ErrorLexico en "+nro_linea+":"+indexLine+"= Desplazamiento fuera de rango");
	}
	private Token analizarRegistro(){
		indexLine=indexLine+2;
		int numero=linea.charAt(indexLine-1);
		if(numero>57)
			numero-=7;
		return new Token("Id_Reg",numero -48,nro_linea,indexLine-1);
	}
	private Token analizarDireccion(){
		int i= Integer.parseInt(""+linea.charAt(indexLine)+linea.charAt(indexLine-1),16);
		indexLine=indexLine+2;
		return new Token("Lit_Dir",i,nro_linea,indexLine-1);
	}
	private Token analizarIdentificador() {
		int numerocolumna=indexLine+1;
		String lexema =nombreIdentificador();
		Integer id=opcodes.get(lexema);
	
		if(id==null)
			return new Token("Id_Etiq",lexema,nro_linea,numerocolumna);
		else
			return new Token(sentencias[id.intValue()],id.intValue(),nro_linea,numerocolumna);	
	}
	private Token comentarioSimple() throws ErrorLexico,IOException{
		return recargarLinea();
	}
	private Token comentarioMultilinea() throws ErrorLexico,IOException {
		int linea_inicio=nro_linea;
		int numeroColumna=indexLine+1;
		Token token=null;

		indexLine+=2; //salteo /*
		
		
		while(noTerminaComentarioMultilinea())
			token= recargarLinea();

		if(linea==null)
			throw new ErrorLexico("ErrorLexico en "+linea_inicio+":"+numeroColumna+"= comentario multilinea empieza pero nunca termina");

		indexLine=linea.indexOf("*/",indexLine)+2; 
		return token;
	}
	private boolean  noTerminaComentarioMultilinea(){
		return linea!=null && linea.indexOf("*/",indexLine)==-1;
	}
	private Token saltarBlancos() throws ErrorLexico,IOException {
		while(indexLine<linea.length() && esEspacioBlanco())
			indexLine++;
		return getToken();
	}
	private String nombreIdentificador() {
		String lexema="";
		while(esCaracterIdentificador(indexLine)){
			lexema+=linea.charAt(indexLine);
			indexLine++;
		}
		return lexema;
	}
	private boolean esCaracterIdentificador(int indexLine){
		return indexLine<linea.length() && (esLetra()||esDigito()|| esGuionBajo()); 
	}
	private boolean esGuionBajo() {
		return linea.lastIndexOf(indexLine)=='_';
	}
			
	/**
	 * Carga las Sentencias
	 */
	private void CargarSentencias(){

		opcodes= new Hashtable<String,Integer>();
		try{
		opcodes.put("add",0);
		opcodes.put("sub",1);
		opcodes.put("and",2);
		opcodes.put("xor",3);
		opcodes.put("lsh",4);
		opcodes.put("rsh",5);
		opcodes.put("load",6);
		opcodes.put("store",7);
		opcodes.put("lda",8);
		opcodes.put("jz",9);
		opcodes.put("jg",10);
		opcodes.put("call",11);
		opcodes.put("jmp",12);
		opcodes.put("inc",13);
		opcodes.put("dec",14);
		opcodes.put("hlt",15);
		}catch(NullPointerException e){
		}
	}
	private void cargarTriviales(){
		casosTriviales= new Hashtable<Character,String>();
		try{
			casosTriviales.put('(',"T_ParenIni");
			casosTriviales.put(')',"T_ParenFin");
			casosTriviales.put(':',"T_Puntos");
			casosTriviales.put(',',"T_Coma");
		}catch(NullPointerException e){
		}
	}
	private Token findeArchivo(){
		file.Close();
		return new TokenEOF(nro_linea,indexLine);
	}
	
}
