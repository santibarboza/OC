package Utilitarios;


public class Hexadecimal {
	private static Hexadecimal instancia;
	
	private Hexadecimal(){
		
	}
	public static Hexadecimal crearInstancia(){
		if(instancia==null)
			instancia=new Hexadecimal();
		return instancia;
	}

	public static int comp(int i) {
		int j=i;
		if(i>127)
			j=-(256-i);
		if(i<0)
			j=256+i;
		return j;
	}
	public static String hex2Dig(int i) {
		String a="";
		if (i<16)
			a+="0";
		return a+hex(i);
	}
	public static String hex(int a){
		return Integer.toHexString(a).toUpperCase();
	}

	public static String hex2(int i) {
		String a="(";
		if (i<16)
			a+="0";
		return a+hex(i)+")h";
	}
}
