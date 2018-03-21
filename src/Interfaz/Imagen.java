package Interfaz;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Imagen extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean mitad;
	private String ruta;
	private int margen;
	public Imagen(String rut,int x,int marg,boolean mit) {
			ruta=rut;
			this.setSize(x, x); //se selecciona el tamaño del panel
			mitad=mit;
			margen=marg;
	}

	//Se crea un método cuyo parámetro debe ser un objeto Graphics

	public void paint(Graphics grafico) {
			Dimension height = getSize();

			//Se selecciona la imagen que tenemos en el paquete de la //ruta del programa

			ImageIcon Img = new ImageIcon(getClass().getResource(ruta)); 

			//se dibuja la imagen que tenemos en el paquete Images //dentro de un panel
			if(mitad)
				grafico.drawImage(Img.getImage(), margen,margen*10, height.width-margen, height.height/2+margen*5, null);
			else
				grafico.drawImage(Img.getImage(), margen, margen, height.width-margen, height.height-margen, null);
			
			setOpaque(false);
			super.paintComponent(grafico);
	}
}