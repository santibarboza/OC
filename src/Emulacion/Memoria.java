package Emulacion;

import Excepciones.ErrorEjecucion;

public class Memoria {
	protected int memoria[];
	protected int registro[];
	protected int direccionInicio;
	protected int direccionActual;
	
	public Memoria(){
		direccionInicio=0;
		memoria= new int[256];
		registro= new int[16];
	}
	public Memoria(int dir){
		direccionInicio=dir;
		memoria= new int[256];
		registro= new int[16];
	}
	
	
	
	public void escribirMemoria(int m) throws ErrorEjecucion{
		if(direccionActual>255)
			throw new ErrorEjecucion("Codigo alocado fuera de la memoria");
		direccionActual=direccionActual & 255;
		memoria[direccionActual]=m;
		direccionActual++;
	}
	public void escribirMemoria(int dir, int m){
		memoria[dir]=m;
	}
	public int leerMemoria(int dir){
		return memoria[dir];
	}
	public void resetearRegistros(){
		registro= new int[16];
	}
	public void resetearDireccionActual(){
		direccionActual=direccionInicio;
	}
	public int getDireccionActual(){
		return direccionActual;
	}
}
