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
	protected AnalizadorLexico alex;
	protected Token tokenActual;
	protected String idTok;
	
	protected Hashtable<String,String> simbolos;
	protected TablasdeEtiquetas etiquetas;
	protected Memoria memoria;
	
	
	public AnalizadorSintactico(AnalizadorLexico lex) throws ErrorLexico, IOException, ErrorEjecucion{
		this(lex,0);
	}
	
	public AnalizadorSintactico(AnalizadorLexico lex,int dirInicio) throws ErrorLexico, IOException, ErrorEjecucion{
		alex=lex;
		memoria= new MemoriaImp();
		etiquetas= new TablasdeEtiquetas();
		simbolos=new Hashtable<String,String> ();
		tokenActual=lex.getToken();
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
		
		System.out.println("Programa Correcto Sintacticamente");
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
		int opcode = 0,rs1,rs2,off,rd;
		if(!esIgual("T_Salto"))
			opcode=tokenActual.get_Lexema();

		if(esIgual("T_SentenciaOperacion")){
			match("T_SentenciaOperacion");
				rd=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				rs1=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				rs2=tokenActual.get_Lexema();
			match("Id_Reg");
				memoria.escribirMemoria(opcode*16+rd);
				memoria.escribirMemoria(rs1*16+rs2);
		}else if(esIgual("T_SentenciaMemoria")){
			boolean load=tokenActual.get_Lexema()==6;
			match("T_SentenciaMemoria"); 
			if(load){
					rd=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					off=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					rs1=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");

			}else{
					rs1=tokenActual.get_Lexema();
//				memoria.escribirMemoria(convertirHexa(tokenActual.get_Lexema()));
				match("Id_Reg");
				match("T_Coma");
					off=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					rd=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");;
			}
			memoria.escribirMemoria(opcode*16+rd);
			memoria.escribirMemoria(rs1*16+off);
		}else if(esIgual("T_SentenciaAddress")){
			match("T_SentenciaAddress");
			rd=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				memoria.escribirMemoria(opcode*16+rd);
			dirOEtiq();
		}else if(esIgual("T_SentenciaT3")){
			match("T_SentenciaT3");
				rd=tokenActual.get_Lexema();
			match("Id_Reg");
			memoria.escribirMemoria(opcode*16+rd);
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
		System.out.println(idTok);
		if(esIgual(ID_T)){
			tokenActual=alex.getToken();
			idTok=tokenActual.get_IDTOKEN();
		}else
			throw new ErrorSintactico(Error(posibles));
	}
	private String Error(String esperado){
		return"Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= Se esperaba un "+esperado+" y se encontro un "+simbolos.get(idTok)+" "+tokenActual.get_Lexema();
	}
	private boolean esIgual(String txt){
		return idTok.equals(txt);
	}

	/*
	 * Cargo explicaciones de los Errores
	 */
	private void cargarSimbolos() {
		simbolos= new Hashtable<String,String>();
		try{
			simbolos.put("T_ParenIni","(");
			simbolos.put("T_ParenFin",")");
			simbolos.put("T_Puntos",": para terminar una etiqueta");
			simbolos.put("T_Coma",",");
			simbolos.put("T_Salto","\\n");
			simbolos.put("Lit_Desp","un Desplazamiento");
			simbolos.put("Lit_Dir","una Direccion");
			simbolos.put("Id_Reg","un Registro");
			simbolos.put("Id_Etiq","Etiqueta");
			simbolos.put("EOF","fin de Archivo");
			
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










