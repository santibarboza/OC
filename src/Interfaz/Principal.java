package Interfaz;

import java.io.IOException;

//import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import Analisis.Alex_AnalizadorLexico;
import Analisis.Ase_AnalizadorSintacticoEjecucion;
import Emulacion.OutpuManager;
import Emulacion.OutputTextPane;
import Excepciones.ErrorEjecucion;
import Excepciones.ErrorLexico;
import Excepciones.ErrorSemantico;
import Excepciones.ErrorSintactico;


public class Principal {

	/**
	 * @param args
	 */
	private boolean elegido_arch;
	private String archivo="";
	private Alex_AnalizadorLexico alex;
	private Ase_AnalizadorSintacticoEjecucion asi;
	private OutpuManager output;
	//private boolean hayPaso;
	
	public Principal(){
		elegido_arch=false;
		archivo="";
//		hayPaso=true;
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
	public boolean compilar(String dirIni,JTextPane textPane,JTable mem){
		boolean exito=true;
		output=new OutputTextPane(textPane,mem);
		try {
			int dirInicio;
			try{
				dirInicio=Integer.parseInt(dirIni, 16);
			}catch(NumberFormatException e){
				throw new ErrorEjecucion("La direccion de ensamblado es invalida");		
			}
			if(dirInicio>255 ||dirInicio<0)
				throw new ErrorEjecucion("La direccion de ensamblado es invalida");
			
			alex = new Alex_AnalizadorLexico(archivo);
			asi= new Ase_AnalizadorSintacticoEjecucion(alex,dirInicio);
			System.out.println("Inicia Analisis...");
			asi.inicial();
			output.mostrarMemoria(asi.getMemoria(),asi.getTablaEtiqueta());
			
			JOptionPane.showMessageDialog(null, "Se compilo correctamente");
		} catch (ErrorLexico |ErrorSintactico|ErrorSemantico | ErrorEjecucion e){ 	
			System.out.println(e.getMessage()); 
			JOptionPane.showMessageDialog(null, e.getMessage());
			exito=false;
		} catch ( IOException e) {
			e.printStackTrace();
			exito=false;
		}
		
		return exito;
	}
	
	/*
	public boolean ejecutar(JTable table,JTable mem,JLabel PC,JLabel Inst){
		boolean exito=true;
		try {
			asi.ejecutar(table,mem,PC,Inst);
			
		} catch (ErrorSintactico | ErrorEjecucion e) {
			System.out.println(e.getMessage()); 
			JOptionPane.showMessageDialog(null, e.getMessage());
			exito=false;
		}
		return exito;
	}
	public boolean ejecutarPAP(JTable table,JTable mem,JLabel PC,JLabel Inst){
		try {
			hayPaso=asi.ejecutarPaP(table,mem,PC,Inst);
		} catch (ErrorSintactico | ErrorEjecucion e) {
			System.out.println(e.getMessage()); 
			JOptionPane.showMessageDialog(null, e.getMessage());
			hayPaso=false;
		}
		return hayPaso;
	}
	public boolean adelantarPaso(JTable table,JTable mem,JLabel PC,JLabel Inst){
		try {
			if(hayPaso)
				hayPaso=asi.pasoAdelante(table,mem,PC,Inst);
			else
				throw new ErrorEjecucion("No se pueden dar mas Pasos (o se produjo un Error o se ejecuto un fin)");
		} catch (ErrorSintactico | ErrorEjecucion e) {
			System.out.println(e.getMessage()); 
			JOptionPane.showMessageDialog(null, e.getMessage());
			hayPaso=false;
		}
		return hayPaso;
	}
	public void resetearRegistros(){
		asi.resetearRegistros();
	}
*/
}
