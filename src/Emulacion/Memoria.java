package Emulacion;

import Excepciones.ErrorEjecucion;

public interface Memoria {
	public void escribirMemoria(int m) throws ErrorEjecucion;
	public void escribirMemoria(int dir, int m);
	public int leerMemoria(int dir);
	public void resetearRegistros();
	public void resetearDireccionActual();
	public int getDireccionInicio();
	public int getDireccionActual();
}
