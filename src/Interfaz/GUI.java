package Interfaz;

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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.TextArea;
import javax.swing.JTextPane;
import javax.swing.JTable;

public class GUI {

	private static Principal ppal;
	private JFrame frmOcunsVirtualmachine;
	private JTextField txtParaIniciarLa;
	private JLabel Name_archivito;
	
	private JButton btnVerMemoria;
	private JButton btnCompilar;
	private JCheckBox chckbxDeteccionDeOv,chckbxRealizarTraza;
	private JComboBox<String> comboBox;
	private JButton btnEjecutar;
	private TextArea textArea;
	private JLabel lblArchivoOriginal;
	private JTextPane textPane;
	private JTextField textField;
	private JLabel lblDirInicio;
	private String dirInicio;
	private JLabel lblCompilado;
	private JTable table;
	private JTable table1;
	private JButton btnSiguiente;
	private JLabel lblPc;
	private JLabel lblIntruccion;

	private JFrame ventanaMem;
	private JFrame ventanaHelp;
	
	private void visibilidadEjecutar(Boolean b){
		btnCompilar.setEnabled(b);
	//	chckbxDeteccionDeOv.setEnabled(b);
		chckbxRealizarTraza.setEnabled(b);
		comboBox.setEnabled(b);
		btnEjecutar.setEnabled(b);
		table.setEnabled(b);
		btnVerMemoria.setEnabled(b);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ppal= new Principal();
		if(args.length>0){
			String fileI=args[0];
			if(fileI.equals("-h")||fileI.equals("--help"))
				mostrarAyuda();
		}else
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						GUI window = new GUI();
						window.frmOcunsVirtualmachine.setVisible(true);
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			});
		
	}
	private static void mostrarAyuda() {

		System.out.println("\n\n		 ____ ____ ____ ____ ____ ____ ____ ____ ____ ____ 	");
		System.out.println("		||I |||n |||t |||e |||r |||p |||r |||e |||t |||e ||	");
		System.out.println("		||__|||__|||__|||__|||__|||__|||__|||__|||__|||__||	");
		System.out.println("		|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|	");
		System.out.println("			      ____ ____ ____ ____ ____ 			");
		System.out.println("			     ||O |||C |||U |||N |||S ||			");
		System.out.println("			     ||__|||__|||__|||__|||__||			");
		System.out.println("			     |/__\\|/__\\|/__\\|/__\\|/__\\|			");
		System.out.println();
		System.out.println("				Manual Interprete OCUNS\n");

		System.out.println("Modo de Empleo:\n	java -jar ./OCUNS.jar \n");

		System.out.println("		El intérprete OCUNS es un programa desarrollado en java que permite ensamblar codigo de ");
		System.out.println("	la arquitectura OCUNS. Además como funcionalidades adicionales el interprete también permite ejecutar el ");
		System.out.println("	codigo OCUNS completo o de a pasos y Visualizar el estado de la memoria y el banco de registro. \n");
		System.out.println("\n		Para mas informacion sobre el set de instrucciones,oprimir el boton ayuda o consultar el manualde usuario");

		System.out.println("		Este programa fue desarollado en el marco del proyecto \"Herramientas para estudiar Computacion\"");
		System.out.println(" 	del Centro de Estudiantes de Computacion, para ayudar a los alumnos de Organizacion de Computadoras");
		System.out.println("								");
		System.out.println("Agedecimiento:							");
		//System.out.println("								");
		System.out.println("	ooooooooooooooooooooooooooooooooooooooooooooooooooooo	");
		System.out.println("	       a888b.  88888b  a888b.                           ");
		System.out.println("	      d8' `88 a88aa   88  `88 .d888b. 88d8b.d8b.       	");
		System.out.println("	      88       88     88      88' `88 88'`88'`88	");
		System.out.println("	       Y888P'  88888P  Y888P' `8888P' dP  dP  dP   	");
		System.out.println("	ooooooooooooooooooooooooooooooooooooooooooooooooooooo  	");
		System.out.println("								");
		System.out.println("Desarollador:							");
		System.out.println("		Santiago Rubén Barboza 				");
		System.exit(0);
		
		
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOcunsVirtualmachine = new JFrame();
		frmOcunsVirtualmachine.setTitle("OCUNS - VirtualMachine");
		frmOcunsVirtualmachine.setBounds(50, 50, 800, 520);
		frmOcunsVirtualmachine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOcunsVirtualmachine.getContentPane().setLayout(null);
		
		JButton btnAbrirNuevoArchivo = new JButton("Abrir Nuevo Archivo");
		btnAbrirNuevoArchivo.setBounds(12, 15, 199, 25);

		OyenteArch oArch = new OyenteArch();
		btnAbrirNuevoArchivo.addActionListener(oArch);
		
		frmOcunsVirtualmachine.getContentPane().add(btnAbrirNuevoArchivo);
		
		txtParaIniciarLa = new JTextField();
		txtParaIniciarLa.setEditable(false);
		txtParaIniciarLa.setBackground(null);
		txtParaIniciarLa.setBorder(null);
		txtParaIniciarLa.setText("Para iniciar la Aplicación Elegí el Archivo a Compilar");
		txtParaIniciarLa.setBounds(12, 460, 770, 19);
				
		frmOcunsVirtualmachine.getContentPane().add(txtParaIniciarLa);
		txtParaIniciarLa.setColumns(10);
		
		Name_archivito = new JLabel("");
		Name_archivito.setBackground(null);
		Name_archivito.setAutoscrolls(true);
		Name_archivito.setBounds(236, 15, 534, 25);
		frmOcunsVirtualmachine.getContentPane().add(Name_archivito);
		
		btnCompilar = new JButton("Compilar");
		btnCompilar.setEnabled(false);
		btnCompilar.setBounds(12, 83, 117, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnCompilar);

		OyenteComp oComp = new OyenteComp();
		btnCompilar.addActionListener(oComp);
		
		btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.setEnabled(false);
		btnEjecutar.setBounds(653, 64, 117, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnEjecutar);
		
		OyenteEjec oEjec = new OyenteEjec();
		btnEjecutar.addActionListener(oEjec);
		
		chckbxDeteccionDeOv = new JCheckBox("Deteccion de Ov");
		chckbxDeteccionDeOv.setEnabled(false);
		chckbxDeteccionDeOv.setSelected(true);
		chckbxDeteccionDeOv.setBounds(152, 65, 139, 23);
		frmOcunsVirtualmachine.getContentPane().add(chckbxDeteccionDeOv);
		
		chckbxRealizarTraza = new JCheckBox("Realizar Traza");
		chckbxRealizarTraza.setEnabled(false);
		chckbxRealizarTraza.setBounds(152, 84, 129, 23);
		chckbxRealizarTraza.setVisible(false);
		frmOcunsVirtualmachine.getContentPane().add(chckbxRealizarTraza);
		
		comboBox = new JComboBox<String>();
		comboBox.addItem("Ejecutar todo el Codigo");
		comboBox.addItem("Ejecutar de a una Instruccion");
		comboBox.setEnabled(false);
		comboBox.setBounds(328, 64, 271, 25);
		frmOcunsVirtualmachine.getContentPane().add(comboBox);
		
		textArea = new TextArea();
		textArea.setEnabled(false);
		textArea.setEditable(false);
		textArea.setBounds(30, 150, 200, 300);
		frmOcunsVirtualmachine.getContentPane().add(textArea);
		
		lblArchivoOriginal = new JLabel("Archivo Original");
		lblArchivoOriginal.setEnabled(false);
		lblArchivoOriginal.setBounds(58, 125, 111, 15);
		frmOcunsVirtualmachine.getContentPane().add(lblArchivoOriginal);
		
		textPane = new JTextPane();
		JScrollPane jsp = new JScrollPane(textPane);
		jsp.setBounds(250, 150, 230, 300);
		frmOcunsVirtualmachine.getContentPane().add(jsp);
		
		textField = new JTextField();
		textField.setEnabled(false);
		textField.setText("00");
		textField.setBounds(100, 55, 30, 19);
		frmOcunsVirtualmachine.getContentPane().add(textField);
		textField.setColumns(10);
		
		lblDirInicio = new JLabel("Dir Inicio:");
		lblDirInicio.setEnabled(false);
		lblDirInicio.setBounds(20, 55, 70, 15);
		frmOcunsVirtualmachine.getContentPane().add(lblDirInicio);
		
		lblCompilado = new JLabel("Compilado");
		lblCompilado.setEnabled(false);
		lblCompilado.setBounds(236, 125, 74, 15);
		frmOcunsVirtualmachine.getContentPane().add(lblCompilado);
		
		String [][]a=new String[16][2];
		for(int i=0;i<16;i++){
			a[i][0]=("R"+Integer.toHexString(i)).toUpperCase();
			a[i][1]="";
		}
		String[]columnNames={"Registros","Contenido"};
		table = new JTable(a, columnNames);
		table.setEnabled(false);
		JScrollPane jsp2 = new JScrollPane(table);
		jsp2.setBounds(496, 170, 150, 280);
		frmOcunsVirtualmachine.getContentPane().add(jsp2);
		
		JLabel lblBancoDeRegistros = new JLabel("Banco de Registros");
		lblBancoDeRegistros.setEnabled(false);
		lblBancoDeRegistros.setBounds(503, 150, 137, 15);
		frmOcunsVirtualmachine.getContentPane().add(lblBancoDeRegistros);
		
		btnVerMemoria = new JButton("Ver Memoria");
		btnVerMemoria.setEnabled(false);
		btnVerMemoria.setBounds(659, 276, 123, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnVerMemoria);

		OyenteMem oMem = new OyenteMem();
		btnVerMemoria.addActionListener(oMem);
		
		btnSiguiente = new JButton("Siguiente");
		btnSiguiente.setEnabled(false);
		btnSiguiente.setBounds(659, 239, 123, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnSiguiente);
		
		lblPc = new JLabel("PC=");
		lblPc.setEnabled(false);
		lblPc.setBounds(659, 174, 113, 19);
		frmOcunsVirtualmachine.getContentPane().add(lblPc);
		
		lblIntruccion = new JLabel("Intruccion:");
		lblIntruccion.setEnabled(false);
		lblIntruccion.setBounds(659, 197, 123, 30);
		frmOcunsVirtualmachine.getContentPane().add(lblIntruccion);
		
		JButton btnAyuda = new JButton("Ayuda");
		btnAyuda.setBounds(659, 386, 123, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnAyuda);

		OyenteHelp oHelp = new OyenteHelp();
		btnAyuda.addActionListener(oHelp);
		
		JButton btnSobreMi = new JButton("Sobre Mi");
		btnSobreMi.setBounds(659, 423, 123, 25);
		frmOcunsVirtualmachine.getContentPane().add(btnSobreMi);

		OyenteSig oSig = new OyenteSig();
		btnSiguiente.addActionListener(oSig);
		
		ventanaMem=new JFrame();
		ventanaMem.setTitle("Memoria de OCUNS");
		ventanaMem.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		ventanaMem.setBounds(50, 50, 170, 520);
		ventanaMem.getContentPane().setLayout(null);
		ventanaMem.setVisible(false);
		
		String[]cNames={"Dir","Memoria"};
		String [][]b=new String[256][2];
		for(int i=0;i<16;i++){
			b[i][0]=("0"+Integer.toHexString(i)).toUpperCase();
			b[i][1]="";
		}for(int i=16;i<256;i++){
			b[i][0]=(""+Integer.toHexString(i)).toUpperCase();
			b[i][1]="";
		}
		table1 = new JTable(b, cNames);
		table1.setEnabled(false);
		JScrollPane jsp3 = new JScrollPane(table1);
		jsp3.setBounds(0, 0, 150, 490);
		ventanaMem.getContentPane().add(jsp3);
		
		
		ventanaHelp = new Help();
		ventanaHelp.setVisible(false);
		ventanaHelp.setTitle("Ayuda para OCUNS Virtual Machine");
		ventanaHelp.setSize(600,500);
	//	ventanaHelp.setResizable(false);
	}
    class OyenteArch implements ActionListener {
    	  public void actionPerformed(ActionEvent evt) {
    		  JFileChooser fc = new JFileChooser(); 
    		  fc.setFileFilter(new FileNameExtensionFilter("Archivos de OCVM","ocuns"));
    		  int opcion = fc.showDialog(null, "Abrir");
    		  if (opcion == JFileChooser.APPROVE_OPTION) {
    			  File file = fc.getSelectedFile(); 
    			  System.out.println("Archivo Abierto: "+file.getAbsolutePath());
    			  String arch=file.getAbsolutePath();
    			  //activartodo
    			  Name_archivito.setText(arch);
    			  ppal.setArchivoNuevo(arch);
    			  txtParaIniciarLa.setText("Archivo Cargado con Exito! Puede Compilarlo o Abrir uno nuevo");
    			  textArea.setText(Cat(file));
    			  textArea.setEnabled(true);
    			  visibilidadEjecutar(false);  
    			  btnCompilar.setEnabled(true);
    			  textField.setEnabled(true);
    			  lblDirInicio.setEnabled(true);
    			  
    			  btnSiguiente.setEnabled(false);
    			  textPane.setText("");
    			 

  				String[]titulos={"Registros","Contenido"};
  				String []a=new String[2];
    			DefaultTableModel m=new DefaultTableModel(null,titulos);
    			for(int i=0;i<16;i++){
    				a[0]=("R"+Integer.toHexString(i)).toUpperCase();
    				a[1]="";
    				m.addRow(a);
    			}
    			table.setModel(m);
    			lblPc.setText("PC= ");
    			lblIntruccion.setText("Intruccion:");
    		  }
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
    class OyenteComp implements ActionListener {
  	  public void actionPerformed(ActionEvent evt) {
  		  dirInicio=textField.getText();
  		  if(ppal.compilar(dirInicio,textPane,table1)){
  			  visibilidadEjecutar(true);
  			  txtParaIniciarLa.setText("Elegi el tipo de Ejecucion y presiona Ejecutar");
  		  }
  	  }
  	}
    class OyenteEjec implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
		ppal.resetearRegistros();
    		btnSiguiente.setEnabled(comboBox.getSelectedIndex()==1);
    		if(comboBox.getSelectedIndex()==0)
    			ppal.ejecutar(table,table1,lblPc,lblIntruccion);
    		else
    			ppal.ejecutarPAP(table,table1,lblPc,lblIntruccion);
    	}
    }
    class OyenteSig implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		if(!ppal.adelantarPaso(table,table1,lblPc,lblIntruccion))
    			btnSiguiente.setEnabled(false);
    	}
    }class OyenteMem implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		ventanaMem.setVisible(true);
    		
    	}
    }
    class OyenteHelp implements ActionListener {
    	public void actionPerformed(ActionEvent evt) {
    		ventanaHelp.setVisible(true);
    		
    	}
    }
}
