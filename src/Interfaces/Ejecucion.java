package Interfaces;

import Excepciones.ErrorEjecucion;

public abstract class Ejecucion {
	
	protected Memoria memoria;
	protected OutputManager output;
	public Ejecucion(Memoria memory, OutputManager out){
		memoria= memory;
		output=out;
	}
	public abstract void Ejecutar() throws ErrorEjecucion;
	public abstract boolean ejecutarPaP() throws ErrorEjecucion ;
	public abstract boolean pasoAdelante() throws ErrorEjecucion ;

}
