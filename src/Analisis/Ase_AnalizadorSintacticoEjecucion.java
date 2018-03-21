package Analisis;

import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Excepciones.ErrorEjecucion;
import Excepciones.ErrorLexico;
import Excepciones.ErrorSemantico;
import Excepciones.ErrorSintactico;

public class Ase_AnalizadorSintacticoEjecucion {

	/**
	 * @param args
	 */
	protected Alex_AnalizadorLexico alex;
	protected Token tokenActual;
	protected String idTok;
	protected Hashtable<String,String> simbolos;
	protected Hashtable<String,Integer> etiquetas;
	protected Hashtable<Integer,String> pendiente;

	protected int memoria[];
	protected int registro[];
	protected int direccionInicio;
	protected int direccionActual;
	
	/*
	 * Creo un Analizador Sintactico
	 */
	public Ase_AnalizadorSintacticoEjecucion(Alex_AnalizadorLexico lex) throws ErrorLexico, IOException{
		alex=lex;
		direccionInicio=0;
		memoria= new int[256];
		registro= new int[16];
		simbolos=new Hashtable<String,String> ();
		etiquetas=new Hashtable<String,Integer> ();
		pendiente= new Hashtable<Integer,String>();
		tokenActual=lex.getToken();
		idTok=tokenActual.get_IDTOKEN();
		cargarSimbolos();
	}
	
	/*
	 * Creo un Analizador Sintactico con direccion de inicio
	 */
	public Ase_AnalizadorSintacticoEjecucion(Alex_AnalizadorLexico lex,int dirInicio) throws ErrorLexico, IOException, ErrorEjecucion{
		alex=lex;
		direccionInicio=dirInicio;
		memoria= new int[256];
		registro= new int[16];
		simbolos=new Hashtable<String,String> ();
		etiquetas=new Hashtable<String,Integer> ();
		pendiente= new Hashtable<Integer,String>();
		tokenActual=lex.getToken();
		idTok=tokenActual.get_IDTOKEN();
		cargarSimbolos();
	}
	
	//..................................................Analisis Sintactico.................................

	/**
	 * <Inicial> → <Sentencias>  EOF
 	 */
	//public void inicial(boolean ejecutar,int traza,int inidir,int findir)throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico, ErrorEjecucion{
	public String inicial(JTable mem)throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico, ErrorEjecucion{
		direccionActual=direccionInicio;
		try{
			Sentencias();
		}catch(ArrayIndexOutOfBoundsException e){
			throw new ErrorEjecucion("La direccion de ensamblado es muy grande,no se puede cargar el programa a partir de esa direccion");
		}
		match("EOF");
		
		System.out.println("Programa Correcto Sintacticamente");
		remplazarEtiquetas();
		String ret=mostrarCodificado();
		mostrarMemoria(mem);
		
		return ret;
	}
	
	/**
	*   <Sentencias> → λ | <EtiquetaOLam><Sentencia> <Sentencias>
	 * @throws ErrorEjecucion 
	*/
	public void Sentencias()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico, ErrorEjecucion{
		if(esIgual("Id_Etiq") || esSentencia()){
			EtiquetaOLam();
			Sentencia();
			Sentencias();
		}else if(!esIgual("EOF"))
			throw new ErrorSintactico(Error("Fin de Archivo o inicio de Sentencia"));
	}
	private boolean esSentencia() {
		return esIgual("T_SentenciaOperacion") ||esIgual("T_SentenciaMemoria")||esIgual("T_SentenciaAddress")|esIgual("T_SentenciaT3") ||esIgual("T_Halt")||esIgual("T_Salto");
	}

	/**
	 * <Sentencia>   → <SentenciaOperacion>  idReg, idReg, idReg
	 * <Sentencia>   → <SentenciaMemoria>  idReg, literalDesplazamiento (idReg) 
	 * <Sentencia>   → <SentenciaAddress>  idReg, <DirOEtiq>
	 * <Sentencia>   → <SentenciaT3>  idReg B | hlt | \n
	 * @throws ErrorEjecucion 
	 */
	private void Sentencia() throws ErrorSintactico, ErrorLexico, IOException, ErrorEjecucion {
		int opcode = 0,rs1,rs2,off,rd;
		if(!esIgual("T_Salto"))
			opcode=tokenActual.get_Lexema();

		if(esIgual("T_SentenciaOperacion")){
			match("T_SentenciaOperacion");
				rd=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				rs1=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				rs2=tokenActual.get_Lexema();
			match("Id_Reg");
				escribir(opcode*16+rd);
				escribir(rs1*16+rs2);
		}else if(esIgual("T_SentenciaMemoria")){
			boolean load=tokenActual.get_Lexema()==6;
			match("T_SentenciaMemoria"); 
			if(load){
					rd=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					off=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					rs1=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");

			}else{
					rs1=tokenActual.get_Lexema();
				//escribir(convertirHexa(tokenActual.get_Lexema()));
				match("Id_Reg");
				match("T_Coma");
					off=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					rd=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");;
			}
			escribir(opcode*16+rd);
			escribir(rs1*16+off);
		}else if(esIgual("T_SentenciaAddress")){
			match("T_SentenciaAddress");
			rd=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				escribir(opcode*16+rd);
			dirOEtiq();
		}else if(esIgual("T_SentenciaT3")){
			match("T_SentenciaT3");
				rd=tokenActual.get_Lexema();
			match("Id_Reg");
			escribir(opcode*16+rd);
			escribir(0);		
		}else if(esIgual("T_Halt")){
				match("T_Halt");
				escribir(opcode*16);
				escribir(0);
		}else if(esIgual("T_Salto"))
				match("T_Salto");
		else
			throw new ErrorSintactico(Error("Inicio de Sentencia"));
	}
	
	private void dirOEtiq() throws ErrorSintactico, ErrorLexico, IOException, ErrorEjecucion {
		if(esIgual("Lit_Dir")){
			escribir(tokenActual.get_Lexema());
			match("Lit_Dir");
		}
		else if(esIgual("Id_Etiq")){
			pendiente.put(direccionActual, tokenActual.get_Etiqueta());
			match("Id_Etiq");
			escribir(0);
		}
		else
			throw new ErrorSintactico(Error("Direccion o Etiqueta"));
		
	}
	/**
	 *	λ | etiqueta :  
	 */
	private void EtiquetaOLam() throws ErrorSintactico, ErrorLexico, IOException {
		if(esIgual("Id_Etiq")){
			etiquetas.put(tokenActual.get_Etiqueta(), direccionActual);
			match("Id_Etiq");
			match("T_Puntos");
		}else if(!esSentencia())
			throw new ErrorSintactico(Error("inicio de Sentencia"));
		
	}

	/**
	 * 
	 * @param ID_T
	 * @throws ErrorSintactico
	 * @throws IOException 
	 * @throws ErrorLexico 
	 */
	private void match(String ID_T)throws ErrorSintactico, ErrorLexico, IOException{
		String posibles=simbolos.get(ID_T);
		if(esIgual(ID_T)){
			tokenActual=alex.getToken();
			idTok=tokenActual.get_IDTOKEN();
		}else
			throw new ErrorSintactico(Error(posibles));
	}
	
	/*
	 * Formateo un error Sintactico
	 */

	private String Error(String esperado){
		return"Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= Se esperaba un "+esperado+" y se encontro un "+simbolos.get(idTok)+" "+tokenActual.get_Lexema();
	}
	

	private boolean esIgual(String txt){
		return idTok.equals(txt);
	}
	
	/*
	 * Escribe en memoria
	 */
	private void escribir(int m) throws ErrorEjecucion{
		if(direccionActual>255)
			throw new ErrorEjecucion("Codigo alocado fuera de la memoria");
		direccionActual=direccionActual & 255;
		memoria[direccionActual]=m;
		direccionActual++;
	}

	/*
	 * Cargo explicaciones de los Errores
	 */
	private void cargarSimbolos() {
		simbolos= new Hashtable<String,String>();
		try{
			simbolos.put("T_ParenIni","(");
			simbolos.put("T_ParenFin",")");
			simbolos.put("T_Puntos",": para terminar una etiqueta");
			simbolos.put("T_Coma",",");
			simbolos.put("T_Salto","\\n");
			simbolos.put("Lit_Desp","un Desplazamiento");
			simbolos.put("Lit_Dir","una Direccion");
			simbolos.put("Id_Reg","un Registro");
			simbolos.put("Id_Etiq","Etiqueta");
			simbolos.put("EOF","fin de Archivo");
			
		}catch(NullPointerException e){
			}
		
	}
	/*
	 * REmplazo el vlaor de las etiquetas
	 */
	private void remplazarEtiquetas() throws ErrorSintactico {
		int pcActual,des;
		for(Integer i: pendiente.keySet()){
			int opcode= memoria[i-1]/16;
			Integer direccionTarget=etiquetas.get(pendiente.get(i));
			
			if(direccionTarget==null)
				throw new ErrorSintactico("ErrorSintactico = Etiqueta "+pendiente.get(i)+" no definida");
			
			if(opcode==8 || opcode==11){
				memoria[i]=direccionTarget;
			}else{
				pcActual=i+1;
				des=direccionTarget-pcActual;
				if(des<0){
					des=256+des;
				}
				memoria[i]=des;
			}
		}
	}
	/*
	 * Muestreo la memoria
	 */
	private String mostrarCodificado() {
		int i=0;
		int PC,dir=direccionInicio;
		String ret="";
		for(i=dir;i+1<direccionActual;i=i+2){
			PC=i;
			if(i<16)
				ret+="0";
			ret+=(hex(PC)+"h. "+hex2Dig(memoria[i])+hex2Dig(memoria[i+1])+"	");
			ret+="|| "+(mostrarInstruccion(PC));
			ret+="\n";
		}
		ret+=("\n");
		return ret;
	}

	
	private String mostrarInstruccion(int i) {
		int opcode=memoria[i]/16;
		int rd=memoria[i]%16;
		int off,dir=memoria[i+1],rs1=dir/16,rs2=off=dir%16;
		String etiq="",direccion="",aux;
		aux=pendiente.get(i+1);
		
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

	public void ejecutar(JTable table,JTable mem,JLabel PC,JLabel Inst) throws ErrorSintactico, ErrorEjecucion {
		int instruccion=0, opcode, rd, rs, rt, addr, offset,desp;
		int pc= direccionInicio;
		System.out.println("Ejecucion del Programa");
		while(true) { // lazo eterno de fetch, decode y execute
			// Etapa fetch
			instruccion =(memoria[pc] << 8)+ memoria[pc+1];
			Inst.setText(mostrarInstruccion(pc));
			pc = (pc + 2) & 255;
			PC.setText("PC= "+hex2Dig(pc));
			
			//Etapa decode
			opcode=(instruccion>>12)&15; 
			rd=(instruccion >> 8) & 15;
			rs=(instruccion >> 4) & 15;
			rt=(instruccion >> 0) & 15;
			addr=(instruccion >> 0) & 255;
			offset=rt;
			if(rt>=8)
				offset=rt-16;
			
			if(rd==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
				throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+pc);
			
			
			//Etapa execute
			if(opcode == 0xF)
				break;
			switch(opcode){
				case 0x0:
					registro[rd]=comp(registro[rs])+comp(registro[rt]);
					if(registro[rd]>127 || registro[rd]< -128)
						throw new ErrorEjecucion("Overflow cuando PC="+hex2Dig(pc));
					registro[rd]=comp(registro[rd]);
					break;		
				case 0x1:
					registro[rd]=comp(registro[rs])-comp(registro[rt]);
					if(registro[rd]>127 || registro[rd]< -128)
						throw new ErrorEjecucion("Overflow cuando PC="+hex2Dig(pc));
					registro[rd]=comp(registro[rd]);
					break;		
				case 0x2:
					registro[rd]=(comp(registro[rs]) & comp(registro[rt]));
					break;		
				case 0x3:
					registro[rd]=(comp(registro[rs]) ^ comp(registro[rt]));
					break;		
				case 0x4:
					registro[rd]=(registro[rd]<<registro[rt]) & 256;
					break;		
				case 0x5:
					registro[rd]=(registro[rd]>>1) & 256;
					break;		
				case 0x6:
					desp=(registro[rs]+comp(offset));
					if(desp==255){
						try{
							String ax = JOptionPane.showInputDialog("Ingrese un numero de 00 a FF:");
							registro[rd]=Integer.parseInt(ax, 16);
							}catch(NumberFormatException e){
								throw new ErrorEjecucion("El numero ingresado no es valido");		
							}
							if(registro[rd]<0 || registro[rd]>255)
								throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
					}
					else
						registro[rd]=memoria[desp];
					break;		
				case 0x7:
					desp=registro[rd]+comp(offset);
					if(desp==255){
						JOptionPane.showMessageDialog(null, " Salida =  "+hex2(registro[rs])+" = ("+comp(registro[rs])+")d");
					}
					else{
						memoria[desp]=registro[rs];
					}
					break;	
				case 0x8:
					registro[rd]=addr;
					break;		
				case 0x9:
					if(registro[rd]==0)
						pc=pc+comp(addr);
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xA:
					if(comp(registro[rd])>0)
						pc=pc+comp(addr);
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xB:
					registro[rd]=pc;
					pc=addr;
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xC:
					pc=registro[rd];
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xD:
					registro[rd]=(comp(registro[rd])+1) & 255;
					break;		
				case 0xE:
					registro[rd]=(comp(registro[rd])-1) & 255;
					break;	
				default:
					throw new ErrorEjecucion("Opcode Invalido cuando pc= "+hex2(pc));
			}
			if(registro[rd]>255 || registro[rd]<-128)
				throw new ErrorEjecucion("Overflow cuando PC="+hex2(pc));
			//Wb
			registro[rd]=registro[rd] & 255;
			mostrarRegistros(table);
			mostrarMemoria(mem);
		}
	}

//	private void ejecutar() throws ErrorSintactico, ErrorEjecucion {
//		int instruccion, opcode, rd, rs, rt, addr, offset,desp;
//		int pc= direccionInicio;
//		System.out.println("\n       Ejecucion del Programa\n");
//		while(true) { // lazo eterno de fetch, decode y execute
//			// Etapa fetch
//			instruccion =(memoria[pc] << 8)+ memoria[pc+1];
//			pc = pc + 2;
//			
//			//Etapa decode
//			opcode=(instruccion>>12)&15; 
//			rd=(instruccion >> 8) & 15;
//			rs=(instruccion >> 4) & 15;
//			rt=(instruccion >> 0) & 15;
//			addr=(instruccion >> 0) & 255;
//			offset=rt;
//			if(rt>=8)
//				offset=rt-16;
//			
//			
//			if(traza==1)
//				System.out.print("PC="+hex2(pc)+",  Rd=R"+hex(rd)+"="+hex2(registro[rd])+",  Rs1=R"+hex(rs)+"="+hex2(registro[rs])+",  Rs2=R"+hex(rt)+"="+hex2(registro[rt])+",  Dir="+hex2(addr));
//				
//			if(traza==2)
//				mostrarRegistros(inicio,dirFin,pc);
//			
//			if(rd==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
//				throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+pc);
//			
//			
//			//Etapa execute
//			if(opcode == 0xF)
//				break;
//			
//			switch(opcode){
//				case 0x0: 
//					registro[rd] = registro[rs] + registro[rt];
//					break; // add
//				case 0x1: 
//					registro[rd] = registro[rs] - registro[rt];
//					break; // subtract
//				case 0x2: 
//					registro[rd] = registro[rs] & registro[rt];
//					break; // bitwise and
//				case 0x3: 
//					registro[rd] = registro[rs] ^ registro[rt];
//					break; // bitwise xor
//				case 0x4: 
//					registro[rd] = registro[rs] << registro[rt];
//					break; // shift left
//				case 0x5: 
//					registro[rd] = (short) registro[rs] >> registro[rt];
//					break;		
//				
//				case 0x6:
//					desp=((registro[rs] + offset + 256) & 255);
//					
//					if(desp==255){
//						try{
//							System.out.print("\n\nIngrese un numero de 00 a FF:");
//							registro[rd]=sc.nextInt(16);
//							if(registro[rd]<0 || registro[rd]>255)
//								throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
//						}catch(java.util.InputMismatchException e){
//							throw new ErrorEjecucion("El numero ingresado no es valido");
//						}
//						System.out.print("								");
//							
//					}
//					else
//						registro[rd]=memoria[desp];
//					break;		
//				case 0x7:
//					desp=((registro[rd] + offset + 256) & 255);
//					
//					if(desp==255){
//						System.out.println("\n\n	--------------------------|| Salida =  "+hex2(registro[rs])+" = ("+comp(registro[rs])+")d ||--------------------------\n");
//						System.out.print("								");					
//					}
//					else{
//						memoria[desp]=registro[rs];
//					}
//					break;	
//				case 0x8:
//					registro[rd]=addr;
//					break;		
//				case 0x9:
//					if((short)registro[rd]==0)
//						pcmostrarMemoria=pc+addr;
//					break;		
//				case 0xA:
//					if((short)registro[rd]>0)
//						pc=pc+addr;
//					break;		
//				case 0xB:
//					registro[rd]=pc;
//					pc=addr;
//					break;		
//				case 0xC:
//					pc=registro[rd];
//					break;		
//				case 0xD:
//					registro[rd]=(registro[rd])++;
//					break;		
//				case 0xE:
//					registro[rd]=(registro[rd])--;
//					break;	
//				default:
//					throw new ErrorEjecucion("Opcode Invalido cuando pc= "+hex2(pc));
//			}
//			
//			registro[rd] = registro[rd] & 0xFF; 	// wrap around del registro destino
//			pc = pc & 255;							// wrap around del registro PC
//		
//			if(traza==1)
//				System.out.println("  || Luego de Ejecutar --> ||  PC="+hex2(pc)+",  Rd=R"+hex(rd)+"="+hex2(registro[rd]));
//		}
//		System.out.println();
//	
//	}

	
	
	private int comp(int i) {
		int j=i;
		if(i>127)
			j=-(256-i);
		if(i<0)
			j=256+i;
		return j;
	}

	private String hex2(int i) {
		String a="(";
		if (i<16)
			a+="0";
		return a+hex(i)+")h";
	}
	private String hex2Dig(int i) {
		String a="";
		if (i<16)
			a+="0";
		return a+hex(i);
	}
	private void mostrarRegistros(JTable table) {
		String[] titulos = {"Registro","Contenido"};
		DefaultTableModel m=new DefaultTableModel(null,titulos);
		
		String [][] registros=new String [16][2];
		for(int i=0;i<16;i++){
			registros[i][0]="R"+hex(i);
			if(registro[i]<16)
				registros[i][1]= "0"+hex(registro[i]);
			else
				registros[i][1]= ""+hex(registro[i]);
			m.addRow(registros[i]);
		}
		table.setModel(m);
	}
	private void mostrarMemoria(JTable table) {
		String[] titulos = {"Dir","Memoria"};
		DefaultTableModel m=new DefaultTableModel(null,titulos);
		
		String [] mem=new String [2];
		for(int i=0;i<256;i++){
			mem[0]=hex2Dig(i);
			mem[1]=hex2Dig(memoria[i]);
			m.addRow(mem);
		}
		table.setModel(m);
	}
	private String hex(int a){
		return Integer.toHexString(a).toUpperCase();
	}
	public void resetearRegistros(){
		registro= new int[16];
	}
	private int pcPAP;
	public boolean ejecutarPaP(JTable table,JTable mem,JLabel PC,JLabel Inst) throws ErrorSintactico, ErrorEjecucion {
		pcPAP=direccionInicio;
		return pasoAdelante(table,mem,PC,Inst);
	}
	public boolean pasoAdelante(JTable table,JTable mem,JLabel PC,JLabel Inst) throws ErrorSintactico, ErrorEjecucion {
		boolean hayOtroPaso=true;
		int instruccion=0, opcode, rd, rs, rt, addr, offset,desp;
		int pc= pcPAP;
			// Etapa fetch
			instruccion =(memoria[pc] << 8)+ memoria[pc+1];
			Inst.setText(mostrarInstruccion(pc));
			pc = (pc + 2) & 255;
			PC.setText("PC= "+hex2Dig(pc));
			
			//Etapa decode
			opcode=(instruccion>>12)&15; 
			rd=(instruccion >> 8) & 15;
			rs=(instruccion >> 4) & 15;
			rt=(instruccion >> 0) & 15;
			addr=(instruccion >> 0) & 255;
			offset=rt;
			if(rt>=8)
				offset=rt-16;
			
			
			if(rd==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
				throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+pc);
			
			
			//Etapa execute
			if(opcode == 0xF)
				return false;
			switch(opcode){
				case 0x0:
					registro[rd]=comp(registro[rs])+comp(registro[rt]);
					if(registro[rd]>127 || registro[rd]< -128)
						throw new ErrorEjecucion("Overflow cuando PC="+hex2Dig(pc));
					registro[rd]=comp(registro[rd]);
					break;		
				case 0x1:
					registro[rd]=comp(registro[rs])-comp(registro[rt]);
					if(registro[rd]>127 || registro[rd]< -128)
						throw new ErrorEjecucion("Overflow cuando PC="+hex2Dig(pc));
					registro[rd]=comp(registro[rd]);
					break;		
				case 0x2:
					registro[rd]=(comp(registro[rs]) & comp(registro[rt]));
					break;		
				case 0x3:
					registro[rd]=(comp(registro[rs]) ^ comp(registro[rt]));
					break;		
				case 0x4:
					registro[rd]=(registro[rd]<<registro[rt]) & 256;
					break;		
				case 0x5:
					registro[rd]=(registro[rd]>>1) & 256;
					break;		
				case 0x6:
					desp=(registro[rs]+comp(offset));
					if(desp==255){
						try{
							String ax = JOptionPane.showInputDialog("Ingrese un numero de 00 a FF:");
							registro[rd]=Integer.parseInt(ax, 16);
							}catch(NumberFormatException e){
								throw new ErrorEjecucion("El numero ingresado no es valido");		
							}
							if(registro[rd]<0 || registro[rd]>255)
								throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
					}
					else
						registro[rd]=memoria[desp];
					break;		
				case 0x7:
					desp=registro[rd]+comp(offset);
					if(desp==255){
						JOptionPane.showMessageDialog(null, " Salida =  "+hex2(registro[rs])+" = ("+comp(registro[rs])+")d");
					}
					else{
						memoria[desp]=registro[rs];
					}
					break;	
				case 0x8:
					registro[rd]=addr;
					break;		
				case 0x9:
					if(registro[rd]==0)
						pc=pc+comp(addr);
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xA:
					if(comp(registro[rs])>0)
						pc=pc+comp(addr);
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xB:
					registro[rd]=pc;
					pc=addr;
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xC:
					pc=registro[rd];
					PC.setText("PC= "+hex2Dig(pc));
					break;		
				case 0xD:
					registro[rd]=(comp(registro[rd])+1) & 255;
					break;		
				case 0xE:
					registro[rd]=(comp(registro[rd])-1) & 255;
					break;	
				default:
					throw new ErrorEjecucion("Opcode Invalido cuando pc= "+hex2(pc));
			}
			if(registro[rd]>255 || registro[rd]<-128)
				throw new ErrorEjecucion("Overflow cuando PC="+hex2Dig(pc));
			//Wb
			registro[rd]=registro[rd] & 255;
			mostrarRegistros(table);
			mostrarMemoria(mem);
			pcPAP=pc;
			
			return hayOtroPaso;
	}

}









