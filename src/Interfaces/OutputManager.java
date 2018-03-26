package Interfaces;

import Emulacion.TablasdeEtiquetas;


public interface OutputManager {
	public void mostrarMemoria();
	public void mostrarRegistros();
	public void mostrarMensaje(String txt);
	public String pedirDialogo(String txt);
	public void actualizarPCVisual(int pc);
	public void actualizarVisualIntruccion(int pc);
	public void setMemoriaTabla(Memoria memoria, TablasdeEtiquetas tablaEtiqueta);
}
