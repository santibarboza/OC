package Analisis;
import java.io.IOException;

import Tokens.*;
import Excepciones.ErrorLexico;

public class Alex_AnalizadorLexico {
	protected Archivo file;
	protected String linea;
	protected int indexLine;
	protected int nro_linea;
	protected static Reglas reglas;

		
	public Alex_AnalizadorLexico(String filename) throws IOException{
		nro_linea=0;
		file= new Archivo(filename);
		
		recargarLinea();
		reglas=Reglas.crearInstancia();
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
		
		if(esEspacioBlanco())
			return saltarBlancos();
		else if(reglas.esTrivial(caracterActual))
				return analizarCasoTrivial(caracterActual);
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
	private Token analizarCasoTrivial(char caracterActual){
		indexLine++;
		return new Token(reglas.getIDTrivial(caracterActual),caracterActual+"",nro_linea,indexLine+1);
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
	
		if(reglas.esSentencia(lexema)){
			int id= reglas.getIDSentencia(lexema);
			return new Token(reglas.getLexemaSentencia(id),id,nro_linea,numerocolumna);
		}else
			return new Token("Id_Etiq",lexema,nro_linea,numerocolumna);
				
	}
	private Token comentarioSimple() throws ErrorLexico,IOException{
		return recargarLinea();
	}
	private Token comentarioMultilinea() throws ErrorLexico,IOException {
		int linea_inicio=nro_linea;
		int numeroColumna=indexLine+1;
		Token token=null;

		indexLine+=2; 
		
		
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
		private Token findeArchivo(){
		file.Close();
		return new TokenEOF(nro_linea,indexLine);
	}
	
}
