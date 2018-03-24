package Emulacion;

import Excepciones.ErrorEjecucion;

public abstract class Ejecucion {
	
	protected Memoria memoria;
	protected OutputManager output;
	public Ejecucion(Memoria memory, OutputManager out){
		memoria= memory;
		output=out;
	}
	public abstract void Ejecutar() throws ErrorEjecucion;

}
