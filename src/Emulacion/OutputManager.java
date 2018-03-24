package Emulacion;


public interface OutputManager {
	public void mostrarMemoria (Memoria memoria,TablasdeEtiquetas etiquetas);
	public void mostrarMensaje(String txt);
	public String pedirDialogo(String txt);
	public void actualizarPCVisual(int pc);
	public void actualizarVisualIntruccion(int pc);
}
