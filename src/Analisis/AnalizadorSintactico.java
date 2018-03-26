package Analisis;

import java.io.IOException;
import java.util.Hashtable;


import Tokens.Token;

import Emulacion.*;
import Excepciones.*;

public class AnalizadorSintactico {

	/**
	 * @param args
	 */
	protected AnalizadorLexico analizadorLexico;
	protected Token tokenActual;
	protected String idTok;
	
	protected Hashtable<String,String> simbolos;
	protected TablasdeEtiquetas etiquetas;
	protected Memoria memoria;
	
		
	public AnalizadorSintactico(AnalizadorLexico alex,Memoria memory) throws ErrorLexico, IOException, ErrorEjecucion{
		analizadorLexico=alex;
		memoria= memory;
		etiquetas= new TablasdeEtiquetas();
		simbolos=new Hashtable<String,String> ();
		tokenActual=alex.getToken();
		idTok=tokenActual.get_IDTOKEN();
		cargarSimbolos();
	}
	
	//..................................................Analisis Sintactico.................................

	/**
	 * <Inicial> → <Sentencias>  EOF
 	 */
	public void inicial()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico, ErrorEjecucion{
		try{
			Sentencias();
		}catch(ArrayIndexOutOfBoundsException e){
			throw new ErrorEjecucion("La direccion de ensamblado es muy grande,no se puede cargar el programa a partir de esa direccion");
		}
		match("EOF");
		
		etiquetas.remplazarEtiquetas(memoria);
	}
	
	/**
	*   <Sentencias> → λ | <EtiquetaOLam><Sentencia> <Sentencias>
	 * @throws ErrorEjecucion 
	*/
	public void Sentencias()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico, ErrorEjecucion{
		if(esIgual("Id_Etiq") || esSentencia()){
			EtiquetaOLam();
			Sentencia();
			Sentencias();
		}else if(!esIgual("EOF"))
			throw new ErrorSintactico(Error("Fin de Archivo o inicio de Sentencia"));
	}
	private boolean esSentencia() {
		return esIgual("T_SentenciaOperacion") ||esIgual("T_SentenciaMemoria")||esIgual("T_SentenciaAddress")|esIgual("T_SentenciaT3") ||esIgual("T_Halt")||esIgual("T_Salto");
	}

	/**
	 * <Sentencia>   → <SentenciaOperacion>  idReg, idReg, idReg
	 * <Sentencia>   → <SentenciaMemoria>  idReg, literalDesplazamiento (idReg) 
	 * <Sentencia>   → <SentenciaAddress>  idReg, <DirOEtiq>
	 * <Sentencia>   → <SentenciaT3>  idReg B | hlt | \n
	 * @throws ErrorEjecucion 
	 */
	private void Sentencia() throws ErrorSintactico, ErrorLexico, IOException, ErrorEjecucion {
		int opcode = 0,registroS,registroT,offset,registroD;

		if(!esIgual("T_Salto"))
			opcode=tokenActual.get_Lexema();

		if(esIgual("T_SentenciaOperacion")){
			match("T_SentenciaOperacion");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				registroS=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				registroT=tokenActual.get_Lexema();
			match("Id_Reg");
				memoria.escribirMemoria(opcode*16+registroD);
				memoria.escribirMemoria(registroS*16+registroT);
		}else if(esIgual("T_SentenciaMemoria")){
			boolean esLoad=tokenActual.get_Lexema()==6;
			match("T_SentenciaMemoria"); 
			if(esLoad){
					registroD=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					offset=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					registroS=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");
			}else{
					registroS=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					offset=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					registroD=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");;
			}
			memoria.escribirMemoria(opcode*16+registroD);
			memoria.escribirMemoria(registroS*16+offset);
		}else if(esIgual("T_SentenciaAddress")){
			match("T_SentenciaAddress");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				memoria.escribirMemoria(opcode*16+registroD);
			dirOEtiq();
		}else if(esIgual("T_SentenciaT3")){
			match("T_SentenciaT3");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			memoria.escribirMemoria(opcode*16+registroD);
			memoria.escribirMemoria(0);		
		}else if(esIgual("T_Halt")){
				match("T_Halt");
					memoria.escribirMemoria(opcode*16);
					memoria.escribirMemoria(0);
		}else if(esIgual("T_Salto"))
				match("T_Salto");
		else
			throw new ErrorSintactico(Error("Inicio de Sentencia"));
	}
	
	private void dirOEtiq() throws ErrorSintactico, ErrorLexico, IOException, ErrorEjecucion {
		if(esIgual("Lit_Dir")){
			memoria.escribirMemoria(tokenActual.get_Lexema());
			match("Lit_Dir");
		}
		else if(esIgual("Id_Etiq")){
			etiquetas.cargarEtiquetaPendiente(memoria.getDireccionActual(), tokenActual.get_Etiqueta());
			match("Id_Etiq");
			memoria.escribirMemoria(0);
		}
		else
			throw new ErrorSintactico(Error("Direccion o Etiqueta"));
		
	}
	/**
	 *	λ | etiqueta :  
	 */
	private void EtiquetaOLam() throws ErrorSintactico, ErrorLexico, IOException {
		if(esIgual("Id_Etiq")){
			etiquetas.cargarDirecciondeEtiqueta(tokenActual.get_Etiqueta(), memoria.getDireccionActual());
			match("Id_Etiq");
			match("T_Puntos");
		}else if(!esSentencia())
			throw new ErrorSintactico(Error("inicio de Sentencia"));
		
	}
	private void match(String ID_T)throws ErrorSintactico, ErrorLexico, IOException{
		String posibles=simbolos.get(ID_T);
		if(esIgual(ID_T)){
			tokenActual=analizadorLexico.getToken();
			idTok=tokenActual.get_IDTOKEN();
		}else
			throw new ErrorSintactico(Error(posibles));
	}
	private String Error(String esperado){
		return"Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= Se esperaba "+esperado+" y se encontro un "+simbolos.get(idTok)+" "+tokenActual.get_Lexema();
	}
	private boolean esIgual(String txt){
		return idTok.equals(txt);
	}
	private void cargarSimbolos() {
		simbolos= new Hashtable<String,String>();
		try{
			simbolos.put("T_ParenIni","un (");
			simbolos.put("T_ParenFin","un )");
			simbolos.put("T_Puntos","un : para terminar una etiqueta");
			simbolos.put("T_Coma","una ,");
			simbolos.put("T_Salto","un \\n");
			simbolos.put("Lit_Desp","un Desplazamiento");
			simbolos.put("Lit_Dir","una Direccion");
			simbolos.put("Id_Reg","un Registro");
			simbolos.put("Id_Etiq","una Etiqueta");
			simbolos.put("EOF","el fin de Archivo");
		}catch(NullPointerException e){
			}
	}
	public Memoria getMemoria(){
		return memoria;
	}
	public TablasdeEtiquetas getTablaEtiqueta(){
		return etiquetas;
	}
}










