package VentanasGraficas;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
//import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.TextArea;
import javax.swing.JTextPane;
import javax.swing.JTable;

import Emulacion.OutputTextPane;
import Emulacion.CoordinadorPrincipal;
import Interfaces.OutputManager;

public class VentanaPrincipal {

	private static CoordinadorPrincipal principal;

	private JFrame ventanaPrincipal;
	private JFrame ventanaMemoria;
	private JFrame ventanaHelp;
	
	private String dirInicio;
	private JButton botonVerMemoria;
	private JButton botonCompilar;
	private JButton botonSiguiente;
	private JButton botonEjecutar;
	private JComboBox<String> tipoDeEjecucion;
	private TextArea contenidoArchivoActual;
	private JTextPane panelArchivoCompilado;
	private JTextField textoTutorial,direcccionDeInicioField;
	private JTable registrosTable,memoryTable;
	private JLabel lblArchivoOriginal,lblCompilado,lblDireccionInicio;
	private JLabel nombreArchivoActual,labelPc,labelInstruccion;

	
	private void visibilidadEjecutar(Boolean b){
		botonCompilar.setEnabled(b);
		tipoDeEjecucion.setEnabled(b);
		botonEjecutar.setEnabled(b);
		registrosTable.setEnabled(b);
		botonVerMemoria.setEnabled(b);
	}

	public static void main(String[] args) {
		principal= new CoordinadorPrincipal();
		if(args.length>0){
			String fileI=args[0];
			if(fileI.equals("-h")||fileI.equals("--help"))
				VentanaHelp.mostrarAyuda();
		}else
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						VentanaPrincipal window = new VentanaPrincipal();
						window.ventanaPrincipal.setVisible(true);
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			});
	}
	public VentanaPrincipal() {
		initialize();
	}
	    class OyenteAbrirArchivo implements ActionListener {
    	  public void actionPerformed(ActionEvent evt) {
    		  JFileChooser fc = new JFileChooser(); 
    		  fc.setFileFilter(new FileNameExtensionFilter("Archivos de OCVM","ocuns"));
    		  int opcion = fc.showDialog(null, "Abrir");
    		 
    		  if (opcion == JFileChooser.APPROVE_OPTION) {
    			File file = fc.getSelectedFile(); 
    			activarElementos(file);
    			String[]titulos={"Registros","Contenido"};
  				String []contenido=new String[2];
    			DefaultTableModel m=new DefaultTableModel(null,titulos);
    			for(int i=0;i<16;i++){
    				contenido[0]=("R"+Integer.toHexString(i)).toUpperCase();
    				contenido[1]="";
    				m.addRow(contenido);
    			}
    			registrosTable.setModel(m);
    			labelPc.setText("PC= ");
    			labelInstruccion.setText("Intruccion:");
    		  }  
    	  }
    	  private void activarElementos(File file){
    		  String arch=file.getAbsolutePath();
    		  nombreArchivoActual.setText(arch);
			  principal.setArchivoNuevo(arch);
			  textoTutorial.setText("Archivo Cargado con Exito! Puede Compilarlo o Abrir uno nuevo");
			  contenidoArchivoActual.setText(Cat(file));
			  contenidoArchivoActual.setEnabled(true);
			  visibilidadEjecutar(false);  
			  botonCompilar.setEnabled(true);
			  direcccionDeInicioField.setEnabled(true);
			  lblDireccionInicio.setEnabled(true);
			  botonSiguiente.setEnabled(false);
			  panelArchivoCompilado.setText("");
    	  }
    	  private String Cat(File f){
    		String contenido="";
    		try {
    		  String linea;
    		  FileReader fr=new FileReader (f);
    		  BufferedReader br = new BufferedReader(fr);
			
    		  linea=br.readLine();
    		  while(linea!=null){
    			  contenido+=linea+"\n";
    			  linea=br.readLine();
    		  }
    		  br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
    		return contenido;
    	  }
  	}
    class OyenteCompilar implements ActionListener {
  	  public void actionPerformed(ActionEvent evt) {
  		  dirInicio=direcccionDeInicioField.getText();
  		  OutputManager output=new OutputTextPane(panelArchivoCompilado,memoryTable,registrosTable,labelPc,labelInstruccion);
  		  if(principal.compilar(dirInicio,output)){
  			  visibilidadEjecutar(true);
  			  textoTutorial.setText("Elegi el tipo de Ejecucion y presiona Ejecutar");
  		  }
  	  }
  	}
    class OyenteEjecutar implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
		principal.resetearRegistros();
    		botonSiguiente.setEnabled(tipoDeEjecucion.getSelectedIndex()==1);
    		if(tipoDeEjecucion.getSelectedIndex()==0)
    			principal.ejecutar();
    		else
    			principal.ejecutarPAP();
    	}
    }
    class OyenteSiguiente implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		if(!principal.adelantarPaso())
    			botonSiguiente.setEnabled(false);
    	}
    }class OyenteMemoria implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		ventanaMemoria.setVisible(true);
    		
    	}
    }
    class OyenteHelp implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		ventanaHelp.setVisible(true);
    		
    	}
    }
    private void initialize() {
		ventanaPrincipal = new JFrame();
		ventanaPrincipal.setTitle("OCUNS - VirtualMachine");
		ventanaPrincipal.setBounds(50, 50, 800, 520);
		ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventanaPrincipal.getContentPane().setLayout(null);
		
		JButton botonAbrirArchivo = new JButton("Abrir Nuevo Archivo");
		botonAbrirArchivo.setBounds(12, 15, 199, 25);
		OyenteAbrirArchivo oArch = new OyenteAbrirArchivo();
		botonAbrirArchivo.addActionListener(oArch);
		ventanaPrincipal.getContentPane().add(botonAbrirArchivo);
		
		textoTutorial = new JTextField();
		textoTutorial.setEditable(false);
		textoTutorial.setBackground(null);
		textoTutorial.setBorder(null);
		textoTutorial.setText("Para iniciar la Aplicación Elegí el Archivo a Compilar");
		textoTutorial.setBounds(12, 460, 770, 19);
				
		ventanaPrincipal.getContentPane().add(textoTutorial);
		textoTutorial.setColumns(10);
		
		nombreArchivoActual = new JLabel("");
		nombreArchivoActual.setBackground(null);
		nombreArchivoActual.setAutoscrolls(true);
		nombreArchivoActual.setBounds(236, 15, 534, 25);
		ventanaPrincipal.getContentPane().add(nombreArchivoActual);
		
		botonCompilar = new JButton("Compilar");
		botonCompilar.setEnabled(false);
		botonCompilar.setBounds(12, 83, 117, 25);
		ventanaPrincipal.getContentPane().add(botonCompilar);
		OyenteCompilar oComp = new OyenteCompilar();
		botonCompilar.addActionListener(oComp);
		
		botonEjecutar = new JButton("Ejecutar");
		botonEjecutar.setEnabled(false);
		botonEjecutar.setBounds(653, 64, 117, 25);
		ventanaPrincipal.getContentPane().add(botonEjecutar);
		OyenteEjecutar oEjec = new OyenteEjecutar();
		botonEjecutar.addActionListener(oEjec);
		
		tipoDeEjecucion = new JComboBox<String>();
		tipoDeEjecucion.addItem("Ejecutar todo el Codigo");
		tipoDeEjecucion.addItem("Ejecutar de a una Instruccion");
		tipoDeEjecucion.setEnabled(false);
		tipoDeEjecucion.setBounds(328, 64, 271, 25);
		ventanaPrincipal.getContentPane().add(tipoDeEjecucion);
		
		contenidoArchivoActual = new TextArea();
		contenidoArchivoActual.setEnabled(false);
		contenidoArchivoActual.setEditable(false);
		contenidoArchivoActual.setBounds(30, 150, 200, 300);
		ventanaPrincipal.getContentPane().add(contenidoArchivoActual);
		
		lblArchivoOriginal = new JLabel("Archivo Original");
		lblArchivoOriginal.setEnabled(false);
		lblArchivoOriginal.setBounds(58, 125, 111, 15);
		ventanaPrincipal.getContentPane().add(lblArchivoOriginal);
		
		panelArchivoCompilado = new JTextPane();
		JScrollPane jsp = new JScrollPane(panelArchivoCompilado);
		jsp.setBounds(250, 150, 230, 300);
		ventanaPrincipal.getContentPane().add(jsp);
		
		direcccionDeInicioField = new JTextField();
		direcccionDeInicioField.setEnabled(false);
		direcccionDeInicioField.setText("00");
		direcccionDeInicioField.setBounds(100, 55, 30, 19);
		ventanaPrincipal.getContentPane().add(direcccionDeInicioField);
		direcccionDeInicioField.setColumns(10);
		
		lblDireccionInicio = new JLabel("Dir Inicio:");
		lblDireccionInicio.setEnabled(false);
		lblDireccionInicio.setBounds(20, 55, 70, 15);
		ventanaPrincipal.getContentPane().add(lblDireccionInicio);
		
		lblCompilado = new JLabel("Compilado");
		lblCompilado.setEnabled(false);
		lblCompilado.setBounds(236, 125, 74, 15);
		ventanaPrincipal.getContentPane().add(lblCompilado);
		
		String [][]a=new String[16][2];
		for(int i=0;i<16;i++){
			a[i][0]=("R"+Integer.toHexString(i)).toUpperCase();
			a[i][1]="";
		}
		String[]columnNames={"Registros","Contenido"};
		registrosTable = new JTable(a, columnNames);
		registrosTable.setEnabled(false);
		JScrollPane jsp2 = new JScrollPane(registrosTable);
		jsp2.setBounds(496, 170, 150, 280);
		ventanaPrincipal.getContentPane().add(jsp2);
		
		JLabel labelBancoDeRegistros = new JLabel("Banco de Registros");
		labelBancoDeRegistros.setEnabled(false);
		labelBancoDeRegistros.setBounds(503, 150, 137, 15);
		ventanaPrincipal.getContentPane().add(labelBancoDeRegistros);
		
		botonVerMemoria = new JButton("Ver Memoria");
		botonVerMemoria.setEnabled(false);
		botonVerMemoria.setBounds(659, 276, 123, 25);
		ventanaPrincipal.getContentPane().add(botonVerMemoria);
		OyenteMemoria oMem = new OyenteMemoria();
		botonVerMemoria.addActionListener(oMem);
		
		botonSiguiente = new JButton("Siguiente");
		botonSiguiente.setEnabled(false);
		botonSiguiente.setBounds(659, 239, 123, 25);
		ventanaPrincipal.getContentPane().add(botonSiguiente);
		OyenteSiguiente oSig = new OyenteSiguiente();
		botonSiguiente.addActionListener(oSig);
		
		labelPc = new JLabel("PC=");
		labelPc.setEnabled(false);
		labelPc.setBounds(659, 174, 113, 19);
		ventanaPrincipal.getContentPane().add(labelPc);
		
		labelInstruccion = new JLabel("Intruccion:");
		labelInstruccion.setEnabled(false);
		labelInstruccion.setBounds(659, 197, 123, 30);
		ventanaPrincipal.getContentPane().add(labelInstruccion);
		
		JButton botonAyuda = new JButton("Ayuda");
		botonAyuda.setBounds(659, 386, 123, 25);
		ventanaPrincipal.getContentPane().add(botonAyuda);

		OyenteHelp oHelp = new OyenteHelp();
		botonAyuda.addActionListener(oHelp);
		
		JButton botonSobreMi = new JButton("Sobre Mi");
		botonSobreMi.setBounds(659, 423, 123, 25);
		ventanaPrincipal.getContentPane().add(botonSobreMi);
		
		ventanaMemoria=new JFrame();
		ventanaMemoria.setTitle("Memoria de OCUNS");
		ventanaMemoria.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		ventanaMemoria.setBounds(50, 50, 170, 520);
		ventanaMemoria.getContentPane().setLayout(null);
		ventanaMemoria.setVisible(false);
		
		String[]cNames={"Dir","Memoria"};
		String [][]b=new String[256][2];
		for(int i=0;i<16;i++){
			b[i][0]=("0"+Integer.toHexString(i)).toUpperCase();
			b[i][1]="";
		}for(int i=16;i<256;i++){
			b[i][0]=(""+Integer.toHexString(i)).toUpperCase();
			b[i][1]="";
		}
		
		memoryTable = new JTable(b, cNames);
		memoryTable.setEnabled(false);
		JScrollPane jsp3 = new JScrollPane(memoryTable);
		jsp3.setBounds(0, 0, 150, 490);
		ventanaMemoria.getContentPane().add(jsp3);
		
		ventanaHelp = new VentanaHelp();
		ventanaHelp.setVisible(false);
		ventanaHelp.setTitle("Ayuda para OCUNS Virtual Machine");
		ventanaHelp.setSize(600,500);
	}

}
