package Emulacion;

import Excepciones.ErrorEjecucion;
import Interfaces.Ejecucion;
import Interfaces.Memoria;
import Interfaces.OutputManager;
import Utilitarios.Hexadecimal;

public class EjecucionImpl extends Ejecucion {
	
	
	private int pc,opcode;
	private int registroDIndex, registroSIndex, registroTIndex;
	private int bufferRegistroD,bufferRegistroS,bufferRegistroT;
	private int addr, offset,desplazamiento;
	
	
	public EjecucionImpl(Memoria memory,OutputManager out){
		super(memory,out);		
	}
	
	public void Ejecutar() throws ErrorEjecucion{
		int instruccion=0;
		boolean noTermina=true;
		
		iniciarPC();	
		while(noTermina) {
			instruccion=fetch();
			decode(instruccion);
			noTermina=execute();
			writeBack();
		}
	}
	private void iniciarPC() {
		pc= memoria.getDireccionInicio();
	}
	private int fetch(){
		int instruccion =(memoria.leerMemoria(pc) << 8)+ memoria.leerMemoria(pc+1);
		output.actualizarVisualIntruccion(pc);
		pc = (pc + 2) & 255;
		output.actualizarPCVisual(pc);
		return instruccion;	
	}
	private void decode(int instruccion) throws ErrorEjecucion{	
		leerIndices(instruccion);
		leerRegistros();
		calcularOffset();
		protegerRegistroF();
	}
	private void leerIndices(int instruccion) {
		opcode=(instruccion>>12)&15; 
		registroDIndex=(instruccion >> 8) & 15;
		registroSIndex=(instruccion >> 4) & 15;
		registroTIndex=(instruccion >> 0) & 15;
		addr=(instruccion >> 0) & 255;
	}
	private void leerRegistros() {
		bufferRegistroS=memoria.leerRegistro(registroSIndex);
		bufferRegistroT=memoria.leerRegistro(registroTIndex);
		bufferRegistroD=memoria.leerRegistro(registroDIndex);
	}
	private void calcularOffset() {
		offset=registroTIndex;
		if(offset>=8)
			offset=offset-16;
	}
	private void protegerRegistroF() throws ErrorEjecucion {
		if(registroDIndex==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
			throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+pc);
	}
	private boolean execute() throws ErrorEjecucion {
		if(opcode == 0xF)
			return false;		
		switch(opcode){
			case 0x0:
				realizarAdd();
				break;		
			case 0x1:
				realizarSub();
				break;		
			case 0x2:
				realizarAnd();
				break;		
			case 0x3:
				realizarOr();
				break;		
			case 0x4:
				realizarLeftShift();
				break;		
			case 0x5:
				realizarRightShift();
				break;		
			case 0x6:
				realizarLoad();
				break;		
			case 0x7:
				realizarStore();
				break;
			case 0x8:
				realizarLda();
				break;		
			case 0x9:
				realizarJumpZero();
				break;		
			case 0xA:
				realizarJumpGreater();
				break;		
			case 0xB:
				realizarCall();
				break;		
			case 0xC:
				realizarJump();
				break;		
			case 0xD:
				realizarInc();
				break;		
			case 0xE:
				realizarDec();
				break;	
			default:
				throw new ErrorEjecucion("Opcode Invalido cuando pc= "+Hexadecimal.hex2(pc));
		}
		controlarValorRegistroD();
		return true;
	}
	

	private void realizarAdd() throws ErrorEjecucion {
		bufferRegistroD=Hexadecimal.comp(bufferRegistroS)+Hexadecimal.comp(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2Dig(pc));
		bufferRegistroD=Hexadecimal.comp(bufferRegistroD);
	}
	private void realizarSub() throws ErrorEjecucion {
		bufferRegistroD=Hexadecimal.comp(bufferRegistroS)-Hexadecimal.comp(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2Dig(pc));
		bufferRegistroD=Hexadecimal.comp(bufferRegistroD);
	}
	private void realizarAnd() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroS) & Hexadecimal.comp(bufferRegistroT));
	}
	private void realizarOr() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroS) ^ Hexadecimal.comp(bufferRegistroT));	
	}
	private void realizarLeftShift() {
		bufferRegistroD=(bufferRegistroS<<bufferRegistroT) & 256;
	}
	private void realizarRightShift() {
		bufferRegistroD=(bufferRegistroS>>bufferRegistroT) & 256;
	}
	private void realizarLoad() throws ErrorEjecucion {
		desplazamiento=(bufferRegistroS+Hexadecimal.comp(offset));
		if(desplazamiento==255){
			try{
				String ax=output.pedirDialogo("Ingrese un numero de 00 a FF:");
				bufferRegistroD=Integer.parseInt(ax, 16);
				}catch(NumberFormatException e){
					throw new ErrorEjecucion("El numero ingresado no es valido");		
				}
				if(bufferRegistroD<0 || bufferRegistroD>255)
					throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
		}
		else
			bufferRegistroD=memoria.leerMemoria(desplazamiento);

	}
	private void realizarStore() {
		desplazamiento=bufferRegistroD+Hexadecimal.comp(offset);
		if(desplazamiento==0xFF)
			output.mostrarMensaje(" Salida =  "+Hexadecimal.hex2(bufferRegistroS)+" = ("+Hexadecimal.comp(bufferRegistroS)+")d");
		else
			memoria.escribirMemoria(desplazamiento, bufferRegistroS);
	}
	private void realizarLda() {
		bufferRegistroD=addr;
	}
	private void realizarJumpZero() {
		if(bufferRegistroD==0)
			pc=pc+Hexadecimal.comp(addr);
		output.actualizarPCVisual(pc);
	}
	private void realizarJumpGreater() {
		if(Hexadecimal.comp(bufferRegistroD)>0)
			pc=pc+Hexadecimal.comp(addr);
		output.actualizarPCVisual(pc);
	}
	private void realizarCall() {
		bufferRegistroD=pc;
		pc=addr;
		output.actualizarPCVisual(pc);	
	}
	private void realizarJump() {
		pc=bufferRegistroD;
		output.actualizarPCVisual(pc);
	}
	private void realizarInc() {
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroD)+1) & 255;
	}
	private void realizarDec(){
		bufferRegistroD=(Hexadecimal.comp(bufferRegistroD)-1) & 255;
	}
	private void controlarValorRegistroD() throws ErrorEjecucion {
		if(bufferRegistroD>255 || bufferRegistroD<-128)
			throw new ErrorEjecucion("Overflow cuando PC="+Hexadecimal.hex2(pc));
	}
	private void writeBack() {
		bufferRegistroD=bufferRegistroD&255;
		memoria.escribirRegistro(registroDIndex, bufferRegistroD);
		output.mostrarRegistros();
		output.mostrarMemoria();
	}

	private int pcPAP;
	public boolean ejecutarPaP() throws ErrorEjecucion {
		pcPAP=memoria.getDireccionInicio();
		return pasoAdelante();
	}
	public boolean pasoAdelante() throws ErrorEjecucion {
		boolean hayOtroPaso=true;
		int instruccion=0;
		pc= pcPAP;
		instruccion=fetch();
		decode(instruccion);
		hayOtroPaso=execute();
		writeBack();
		pcPAP=pc;
		return hayOtroPaso;
	}

	
}
