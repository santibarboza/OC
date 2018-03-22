package Emulacion;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

public class OutputTextPane implements OutpuManager {
	private JTextPane outputPanel;
	private JTable outputTable;
	private Memoria memoria;
	private TablasdeEtiquetas etiquetas;
	
	public OutputTextPane(JTextPane panel,JTable table){
		outputPanel=panel;
		outputTable=table;
	}
	public void mostrarMemoria(Memoria memoriaCompleta,TablasdeEtiquetas etiquetasCompletas) {
		memoria=memoriaCompleta;
		etiquetas=etiquetasCompletas;
		String[] titulos = {"Dir","Memoria"};
		DefaultTableModel m=new DefaultTableModel(null,titulos);
		
		String [] mem=new String [2];
		for(int i=0;i<256;i++){
			mem[0]=hex2Dig(i);
			mem[1]=hex2Dig(memoria.leerMemoria(i));
			m.addRow(mem);
		}
		outputTable.setModel(m);
		mostrarCodificado();
	}
	private void mostrarCodificado() {
		int i=0;
		int PC,dir=memoria.getDireccionInicio();
		String ret="";
		for(i=dir;i+1<memoria.getDireccionActual();i=i+2){
			PC=i;
			if(i<16)
				ret+="0";
			ret+=(hex(PC)+"h. "+hex2Dig(memoria.leerMemoria(i))+hex2Dig(memoria.leerMemoria(i+1))+"	");
			ret+="|| "+(mostrarInstruccion(PC));
			ret+="\n";
		}
		ret+=("\n");
		outputPanel.setText(ret);
	}
	private String hex2Dig(int i) {
		String a="";
		if (i<16)
			a+="0";
		return a+hex(i);
	}
	private String hex(int a){
		return Integer.toHexString(a).toUpperCase();
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
			direccion=hex2Dig(dir);
		
		switch(opcode){
			case 0:
				ret+=(etiq+" add R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 1:
				ret+=(etiq+" sub R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 2:
				ret+=(etiq+" and R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 3:
				ret+=(etiq+" xor R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 4:
				ret+=(etiq+" lsh R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 5:
				ret+=(etiq+" rsh R"+hex(rd)+", R"+hex(rs1)+", R"+hex(rs2));
				break;
			case 6:
				ret+=(etiq+" load R"+hex(rd)+", "+comp(off)+"(R"+hex(rs2)+")");
				break;
			case 7:
				ret+=(etiq+" store R"+hex(rs1)+", "+comp(off)+"(R"+hex(rd)+")");
				break;
			case 8:
				ret+=(etiq+" lda R"+hex(rd)+", "+direccion);
				break;
			case 9:
				ret+=(etiq+" jz R"+hex(rd)+", "+direccion);
				break;
			case 10:
				ret+=(etiq+" jg R"+hex(rd)+", "+direccion);
				break;
			case 11:
				ret+=(etiq+" call R"+hex(rd)+", "+direccion);
				break;
			case 12:
				ret+=(etiq+" jmp R"+hex(rd));
				break;
			case 13:
	
				ret+=(etiq+" inc R"+hex(rd));
				break;
			case 14:
				ret+=(etiq+" dec R"+hex(rd));
				break;
			case 15:
				ret+=(etiq+" hlt");
				break;
		}
		return ret;
	}
	private int comp(int i) {
		int j=i;
		if(i>127)
			j=-(256-i);
		if(i<0)
			j=256+i;
		return j;
	}

}
