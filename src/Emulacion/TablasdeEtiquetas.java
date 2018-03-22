package Emulacion;

import java.util.Hashtable;

import Excepciones.ErrorSintactico;

public class TablasdeEtiquetas {

	protected Hashtable<String,Integer> etiquetas;
	protected Hashtable<Integer,String> pendiente;
	
	public TablasdeEtiquetas(){
		etiquetas=new Hashtable<String,Integer> ();
		pendiente= new Hashtable<Integer,String>();
	}
	
	public void cargarEtiquetaPendiente(int direccion,String etiqueta){
		pendiente.put(direccion, etiqueta);
	}
	public void cargarDirecciondeEtiqueta(String etiqueta,int direccion){
		etiquetas.put(etiqueta,direccion);
	}
	public void remplazarEtiquetas(Memoria memoria) throws ErrorSintactico {
		int pcActual,des;
		for(Integer i: pendiente.keySet()){
			int opcode= memoria.leerMemoria(i-1)/16;
			Integer direccionTarget=etiquetas.get(pendiente.get(i));
			
			if(direccionTarget==null)
				throw new ErrorSintactico("ErrorSintactico = Etiqueta "+pendiente.get(i)+" no definida");
			
			if(opcode==8 || opcode==11){
				memoria.escribirMemoria(i, direccionTarget);
			}else{
				pcActual=i+1;
				des=direccionTarget-pcActual;
				if(des<0){
					des=256+des;
				}
				memoria.escribirMemoria(i, des);
			}
		}
	}
	public String obtenerEtiqueta(int i){
		return pendiente.get(i);
	}
}

