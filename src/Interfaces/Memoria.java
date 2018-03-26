package Interfaces;

import Excepciones.ErrorEjecucion;

public interface Memoria {
	public void escribirMemoria(int m) throws ErrorEjecucion;
	public void escribirMemoria(int dir, int m);
	public int leerMemoria(int dir);
	public void escribirRegistro(int nro, int m);
	public int leerRegistro(int nro);
	public void resetearRegistros();
	public void resetearDireccionActual();
	public int getDireccionInicio();
	public int getDireccionActual();
}
