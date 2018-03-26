package Tokens;

import java.util.Hashtable;

public class Reglas {
	protected static Reglas r=null;
	protected static Hashtable<String,Integer>opcodes;
	protected static Hashtable<Character,String> casosTriviales;
	
	private Reglas(){
		cargarSentencias();
		cargarTriviales();
	}
	public static Reglas crearInstancia(){
		if(r==null)
			r=new Reglas();
		return r;
	}
	protected void cargarSentencias(){
		opcodes= new Hashtable<String,Integer>();
		try{
		opcodes.put("add",0);
		opcodes.put("sub",1);
		opcodes.put("and",2);
		opcodes.put("xor",3);
		opcodes.put("lsh",4);
		opcodes.put("rsh",5);
		opcodes.put("load",6);
		opcodes.put("store",7);
		opcodes.put("lda",8);
		opcodes.put("jz",9);
		opcodes.put("jg",10);
		opcodes.put("call",11);
		opcodes.put("jmp",12);
		opcodes.put("inc",13);
		opcodes.put("dec",14);
		opcodes.put("hlt",15);
		}catch(NullPointerException e){
		}
	}
	private void cargarTriviales(){
		casosTriviales= new Hashtable<Character,String>();
		try{
			casosTriviales.put('(',"T_ParenIni");
			casosTriviales.put(')',"T_ParenFin");
			casosTriviales.put(':',"T_Puntos");
			casosTriviales.put(',',"T_Coma");
		}catch(NullPointerException e){
		}
	}
	public boolean esTrivial(char tokenId){
		return (casosTriviales.get(tokenId)!=null);
	}
	public String getIDTrivial(char tokenId){
		return casosTriviales.get(tokenId);
	}
	public int getIDSentencia(String lexema){
		return opcodes.get(lexema);
	}
	public boolean esSentencia(String lexema){
		return opcodes.get(lexema)!=null;
	}
	public String getLexemaSentencia(int id){
		
		if(id>=0 && id<=5)
			return "T_SentenciaOperacion";
		else if(id==6 ||id==7)
				return "T_SentenciaMemoria";
			else if(id>=8 && id<=11)
					return "T_SentenciaAddress";
				else if(id>=12 && id<=14)
						return "T_SentenciaT3";
					else if(id==15)
							return "T_Halt";
						else
							return "ErrorSentencia";
	}
}
