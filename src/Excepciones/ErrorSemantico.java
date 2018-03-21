package Excepciones;

import Analisis.Token;

public class ErrorSemantico extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorSemantico(String txt){
		super(txt);
	}
	public ErrorSemantico(String txt,Token tok){
		super("Error Semantico("+tok.get_NroLinea()+":"+tok.get_NroCol()+")="+txt);
	}
}
