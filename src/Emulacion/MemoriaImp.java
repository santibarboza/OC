package Emulacion;

import Excepciones.ErrorEjecucion;

public class MemoriaImp implements Memoria{
	protected int memoria[];
	protected int registro[];
	protected int direccionInicio;
	protected int direccionActual;
	
	
	public MemoriaImp(int dir){
		direccionInicio=dir;
		direccionActual=direccionInicio;
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
	public void resetearRegistros(){
		registro= new int[16];
		for(int i=0;i<16;i++)
			registro[i]=0;
	}
	public void resetearDireccionActual(){
		direccionActual=direccionInicio;
	}
	public int getDireccionActual(){
		return direccionActual;
	}
	public int getDireccionInicio(){
		return direccionInicio;
	}
	public int leerRegistro(int nro) {
		return registro[nro];
	}
	public int leerMemoria(int dir){
		return memoria[dir];
	}
	public void escribirMemoria(int dir, int m){
		memoria[dir]=m;
	}
	public void escribirRegistro(int nro, int m) {
		registro[nro]=m;
	}
}
