package Excepciones;

public class ErrorEjecucion extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorEjecucion(String txt){
		super("Error en Ejecucion = "+txt);
	}


}
