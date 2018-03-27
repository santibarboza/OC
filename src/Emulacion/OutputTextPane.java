package Emulacion;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import Interfaces.Memoria;
import Interfaces.OutputManager;
import Utilitarios.Hexadecimal;

public class OutputTextPane implements OutputManager {
	private JTextPane outputCompiladoPanel;
	private JTable outputMemoryTable;
	private JTable outputRegistrosTable;
	private JLabel PCLabel;
	private JLabel instruccionLabel;
	private Memoria memoria;
	private TablasdeEtiquetas etiquetas;
	
	public OutputTextPane(JTextPane panel,JTable memoria,JTable registros,JLabel PC,JLabel Instruccion){
		outputCompiladoPanel=panel;
		outputMemoryTable=memoria;
		outputRegistrosTable=registros;
		PCLabel=PC;
		instruccionLabel=Instruccion;
	}
	public void setMemoriaTabla(Memoria memoriaCompleta,TablasdeEtiquetas etiquetasCompletas) {
		memoria=memoriaCompleta;
		etiquetas=etiquetasCompletas;
		mostrarMemoria();
		mostrarRegistros();
	}
	public void mostrarMemoria(){
		String[] titulos = {"Dir","Memoria"};
		DefaultTableModel m=new DefaultTableModel(null,titulos);
		
		String [] mem=new String [2];
		for(int i=0;i<256;i++){
			mem[0]=Hexadecimal.hex2Dig(i);
			mem[1]=Hexadecimal.hex2Dig(memoria.leerMemoria(i));
			m.addRow(mem);
		}
		outputMemoryTable.setModel(m);
		mostrarCodificado();
	}
	public void mostrarRegistros(){
			String[] titulos = {"Registro","Contenido"};
			DefaultTableModel m=new DefaultTableModel(null,titulos);
			
			String [][] registros=new String [16][2];
			for(int i=0;i<16;i++){
				registros[i][0]="R"+Hexadecimal.hex(i);
				if(memoria.leerRegistro(i)<16)
					registros[i][1]= "0"+Hexadecimal.hex(memoria.leerRegistro(i));
				else
					registros[i][1]= ""+Hexadecimal.hex(memoria.leerRegistro(i));
				m.addRow(registros[i]);
			}
			outputRegistrosTable.setModel(m);
	}
	private void mostrarCodificado() {
		int i=0;
		int PC,dir=memoria.getDireccionInicio();
		String ret="";
		for(i=dir;i<memoria.getDireccionActual()-1;i=i+2){
			PC=i;
			if(i<16)
				ret+="0";
			ret+=(Hexadecimal.hex(PC)+"h. "+Hexadecimal.hex2Dig(memoria.leerMemoria(i))+Hexadecimal.hex2Dig(memoria.leerMemoria(i+1))+"	");
			ret+="|| "+(mostrarInstruccion(PC));
			ret+="\n";
		}
		ret+=("\n");
		outputCompiladoPanel.setText(ret);
	}
	
	private String mostrarInstruccion(int i) {
		int opcode=memoria.leerMemoria(i)/16;
		int rd=memoria.leerMemoria(i)%16;
		int off,dir=memoria.leerMemoria(i+1),rs1=dir/16,rs2=off=dir%16;
		String etiq="",direccion="",aux;
		
		aux=etiquetas.obtenerEtiqueta(i+1);
		
		String ret="";
		
		if(aux!=null)
			direccion+=aux;
		else
			direccion=Hexadecimal.hex2Dig(dir);
		
		switch(opcode){
			case 0:
				ret+=(etiq+" add R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 1:
				ret+=(etiq+" sub R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 2:
				ret+=(etiq+" and R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 3:
				ret+=(etiq+" xor R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 4:
				ret+=(etiq+" lsh R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 5:
				ret+=(etiq+" rsh R"+Hexadecimal.hex(rd)+", R"+Hexadecimal.hex(rs1)+", R"+Hexadecimal.hex(rs2));
				break;
			case 6:
				ret+=(etiq+" load R"+Hexadecimal.hex(rd)+", "+Hexadecimal.comp(off)+"(R"+Hexadecimal.hex(rs2)+")");
				break;
			case 7:
				ret+=(etiq+" store R"+Hexadecimal.hex(rs1)+", "+Hexadecimal.comp(off)+"(R"+Hexadecimal.hex(rd)+")");
				break;
			case 8:
				ret+=(etiq+" lda R"+Hexadecimal.hex(rd)+", "+direccion);
				break;
			case 9:
				ret+=(etiq+" jz R"+Hexadecimal.hex(rd)+", "+direccion);
				break;
			case 10:
				ret+=(etiq+" jg R"+Hexadecimal.hex(rd)+", "+direccion);
				break;
			case 11:
				ret+=(etiq+" call R"+Hexadecimal.hex(rd)+", "+direccion);
				break;
			case 12:
				ret+=(etiq+" jmp R"+Hexadecimal.hex(rd));
				break;
			case 13:
	
				ret+=(etiq+" inc R"+Hexadecimal.hex(rd));
				break;
			case 14:
				ret+=(etiq+" dec R"+Hexadecimal.hex(rd));
				break;
			case 15:
				ret+=(etiq+" hlt");
				break;
		}
		return ret;
	}
	public void mostrarMensaje(String txt){
		JOptionPane.showMessageDialog(null, txt);
	}
	public String pedirDialogo(String txt){
		return JOptionPane.showInputDialog(txt);
	}
	public void actualizarPCVisual(int pc){
		PCLabel.setText("PC= "+Hexadecimal.hex2Dig(pc));
	}
	public void actualizarVisualIntruccion(int pc){
		instruccionLabel.setText(mostrarInstruccion(pc));
	}
	
}
