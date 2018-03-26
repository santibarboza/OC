package Interfaz;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class Help extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Help frame = new Help();
					frame.setVisible(true);
					frame.setTitle("Ayuda para OCUNS Virtual Machine");
					frame.setSize(600,500);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Help() {
		setSize(600,500);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
        JTabbedPane pestañas=new JTabbedPane();
        
        JPanel panel3=new JPanel();
        pestañas.addTab("Modo de uso", panel3);
        panel3.setLayout(null);
        
        JButton btnAbrirNuevoArchivo = new JButton("Abrir Nuevo Archivo");
        btnAbrirNuevoArchivo.setBounds(12, 65, 199, 25);
        panel3.add(btnAbrirNuevoArchivo);
        
        
        JLabel label = new JLabel("Abre un archivo \".ocuns\" para compilarlo.");
        label.setBounds(229, 70, 400, 17);
        panel3.add(label);
        
        JButton btnCompilar = new JButton("Compilar");
		btnCompilar.setBounds(12, 110, 117, 25);
		panel3.add(btnCompilar);
		
		JLabel lblCompilaElArchivo = new JLabel("Compila el Archivo Abierto desde la direccion especificada en");
		lblCompilaElArchivo.setBounds(147, 110, 400, 17);
		panel3.add(lblCompilaElArchivo);
		
		JLabel lblDireccionEspecificadaEn = new JLabel("el campo de texto.");
		lblDireccionEspecificadaEn.setBounds(147, 127, 234, 17);
		panel3.add(lblDireccionEspecificadaEn);
		

		JButton btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.setBounds(12, 155, 117, 25);
		panel3.add(btnEjecutar);
		
		JLabel lblEjecutaTodoEl = new JLabel("Ejecuta todo el codigo compilado o activa el modo de");
		lblEjecutaTodoEl.setBounds(147, 155, 400, 17);
		panel3.add(lblEjecutaTodoEl);
		
		JLabel lblLaEjecucionPaso = new JLabel("ejecucion paso a paso");
		lblLaEjecucionPaso.setBounds(147, 172, 152, 17);
		panel3.add(lblLaEjecucionPaso);
	
		JButton btnSiguiente = new JButton("Siguiente");
		btnSiguiente.setBounds(12, 200, 123, 25);
		panel3.add(btnSiguiente);
		
		JLabel lbl1 = new JLabel("Ejecuta una Intruccion actualizando la memoria y los registros");
		lbl1.setBounds(147, 205, 400, 17);
		panel3.add(lbl1);
		
		JButton btnVerMemoria = new JButton("Ver Memoria");
		btnVerMemoria.setBounds(12, 245, 123, 25);
		panel3.add(btnVerMemoria);
		
		JLabel lbl2 = new JLabel("Permite visualizar el estado de la Memoria Principal");
		lbl2.setBounds(147, 250, 400, 17);
		panel3.add(lbl2);
		
		JLabel lblLaInterfazGrfica = new JLabel("La Interfaz Gráfica posee los Siguientes Botones:");
		lblLaInterfazGrfica.setFont(new Font("Dialog", Font.BOLD, 16));
		lblLaInterfazGrfica.setBounds(12, 12, 427, 22);
		panel3.add(lblLaInterfazGrfica);
		
		JLabel lbl3 = new JLabel("Para Obtener mas Información, Consulta el Manual de Usuario");
		lbl3.setBounds(12, 365, 560, 17);
		panel3.add(lbl3);

		URLabel lbl4 = new URLabel();
		lbl4.setURL("http://www.google.com.ar");
		lbl4.setText("Aqui");
		lbl4.setBounds(405, 365, 560, 17);
		panel3.add(lbl4);
//

		Imagen img1= new Imagen("/Imagenes/img1.png",30,5,false);
        pestañas.addTab("Set de Intrucciones", img1);

        JPanel panel2=new JPanel();
        Imagen img2= new Imagen("/Imagenes/img2.png",300,5,true);
        panel2.setLayout(new BorderLayout(0, 0));
        panel2.add(img2, BorderLayout.CENTER);
        pestañas.addTab("Formato de Instruccion", panel2);
//  */      

        contentPane.add(pestañas);
		
		contentPane.repaint();
	}
	public static void mostrarAyuda() {

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
}
