package Interfaz;

import java.io.IOException;

import Analisis.*;
import Emulacion.*;
import Excepciones.*;


public class Principal {
	
	private boolean elegido_arch;
	private String archivo="";
	private AnalizadorLexico alex;
	private AnalizadorSintactico asi;
	private OutputManager output;
	private Ejecucion ejecucion;
	private boolean hayPaso;
	
	public Principal(){
		elegido_arch=false;
		archivo="";
		hayPaso=true;
	}
	public void setArchivoNuevo(String arch){
		archivo =arch;
		elegido_arch=true;
	}
	public String getArchivo(){
		return archivo;
	}
	public boolean isSelected(){
		return elegido_arch;
	}
	
	public boolean compilar(String dirIni,OutputManager out){
		boolean exito=true;
		output=out;
		try {
			int dirInicio=obtenerDireccion(dirIni);
			alex = new AnalizadorLexico(archivo);
			asi= new AnalizadorSintactico(alex,dirInicio);
			asi.inicial();
			output.setMemoriaTabla(asi.getMemoria(),asi.getTablaEtiqueta());
			output.mostrarMemoria();
			output.mostrarRegistros();
			output.mostrarMensaje("Se compilo correctamente");
			ejecucion=new EjecucionImpl(asi.getMemoria(),output);	
		} catch (ErrorLexico |ErrorSintactico|ErrorSemantico | ErrorEjecucion|IOException e){ 	
			output.mostrarMensaje(e.getMessage());
			exito=false;
		}		
		return exito;
	}
	private int obtenerDireccion(String dirIni) throws ErrorEjecucion {
		int dirInicio;
		try{
			dirInicio=Integer.parseInt(dirIni, 16);
		}catch(NumberFormatException e){
			throw new ErrorEjecucion("La direccion de ensamblado es invalida");		
		}
		if(dirInicio>255 ||dirInicio<0)
			throw new ErrorEjecucion("La direccion de ensamblado es invalida");
		return dirInicio;
	}
	public boolean ejecutar(){
		boolean exito=true;
		try {		
			ejecucion.Ejecutar();
		} catch (ErrorEjecucion e) {
			output.mostrarMensaje(e.getMessage());
			exito=false;
		}
		return exito;
	}
	public void resetearRegistros() {
		asi.getMemoria().resetearRegistros();
	}
	
	
	public boolean ejecutarPAP(){
		try {
			hayPaso=ejecucion.ejecutarPaP();
		} catch (ErrorEjecucion e) {
			output.mostrarMensaje(e.getMessage());
			hayPaso=false;
		}
		return hayPaso;
	}
	public boolean adelantarPaso(){
		try {
			if(hayPaso)
				hayPaso=ejecucion.pasoAdelante();
			else
				throw new ErrorEjecucion("No se pueden dar mas Pasos (o se produjo un Error o se ejecuto un fin)");
		} catch (ErrorEjecucion e) {
			output.mostrarMensaje(e.getMessage());
			hayPaso=false;
		}
		return hayPaso;
	}
	

}
