package Analisis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import Excepciones.ErrorLexico;

public class Alex_AnalizadorLexico {
	protected FileReader fr;
	protected BufferedReader br;
	protected String linea;
	protected int indice;
	protected int nro_linea;
	protected static String[] sentencias=
		{"T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaOperacion",
		 "T_SentenciaMemoria",
		 "T_SentenciaMemoria",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaAddress",
		 "T_SentenciaT3",
		 "T_SentenciaT3",
		 "T_SentenciaT3",
		 "T_Halt"};
	protected static Hashtable<String,Integer>opcodes;
	protected Hashtable<Character,String> casosTriviales;
	
	public Alex_AnalizadorLexico(String file) throws IOException{
		nro_linea=0;
		System.out.println("aaaaa"+file);
		File archivo = new File (file);
		fr= new FileReader (archivo);
		br = new BufferedReader(fr);
		recargarLinea();
		CargarSentencias();
		cargarTriviales();
	}
	
	/**
	 * Recarga la linea con la siguiente linea del archivo.
	 */
	private Token recargarLinea() throws IOException{
		indice=0;
		do{
			nro_linea++;
			linea=br.readLine();
		}while(linea!=null && linea.length()==0);//saltea multiples enters	
		return new Token("T_Salto","\\n",nro_linea,indice);
	}
	/**
	 * Retorna un Token por Demanda
	 */
	public Token getToken()throws ErrorLexico,IOException{
		if(linea!=null && indice== linea.length()){
			return recargarLinea();
		}
		if(linea==null)
			return findeArchivo();

		String T_ID=null;
		char c=linea.charAt(indice);
		
		if(c==' ' || c==(char)9)
			return saltarBlancos();
		else if((T_ID=casosTriviales.get(c))!=null){
			indice++;
			return new Token(T_ID,c+"",nro_linea,indice+1);
		}else if(c=='/'){
			if((indice+1)<linea.length() && linea.charAt(indice+1)=='/')			//revisar indice
				return comentarioSimple();
			else if((indice+1)<linea.length() && linea.charAt(indice+1)=='*')
				return comentarioMultilinea();
			else
				throw new ErrorLexico("Error Lexico en "+nro_linea+":"+indice+" = El caracter /"+c+" no esta valido en el lenguaje");
		}else if(c=='-' && digitoDe0a8(indice+1)){
				int i=(linea.charAt(indice-1)-48); //num
				int complemento=16-i;
				indice+=2;
				return new Token("Lit_Desp",complemento,nro_linea,indice);
		}else if((c>='0' && c<='9')){
			//literal
			indice++;
			if(digitoHexa(indice)){
				indice++;
				return new Token("Lit_Dir",Integer.parseInt(""+c+linea.charAt(indice-1),16),nro_linea,indice-1);
			}else if(c<='7')
				return new Token("Lit_Desp",(c-48),nro_linea,indice);
			else
				throw new ErrorLexico("ErrorLexico en "+nro_linea+":"+indice+"= Desplazamiento fuera de rango");
		}else if(c>='A' && c<='Z'){
			if(c=='R'){
					if(digitoHexa(indice+1))
						if(!esCharIdent(indice+2)){
							//es un registro
							indice=indice+2;
							int numero=linea.charAt(indice-1);
							if(numero>57)
								numero-=7;
							return new Token("Id_Reg",numero -48,nro_linea,indice-1);
						}
			}else if(digitoHexa(indice)&& digitoHexa(indice+1)){
				//literal dir
				indice=indice+2;
				return new Token("Lit_Dir",Integer.parseInt(""+c+linea.charAt(indice-1),16),nro_linea,indice-1);				
			}
			else{
				String lex =armarIdentificador();
				return new Token("Id_Etiq",lex,nro_linea,indice+1);
			}
		}else if(c>='a' && c<='z')
			return identificador();
		
		throw new ErrorLexico("ErrorLexico en "+nro_linea+":"+indice+"= El caracter "+c+" no esta valido en el lenguaje");
	}

	/** Determina sies un digito hexa*/
	private boolean digitoHexa(int indice){
		if(indice>=linea.length())
			return false;
		char c2=linea.charAt(indice);
		return ((c2>='0' && c2<='9')||(c2>='A' && c2<='F'));
	}

	/*determina si es un caracter de 0 a 8*/
	private boolean digitoDe0a8(int indice){
		if(indice>=linea.length())
			return false;
		char c2=linea.charAt(indice);
		return ((c2>='0' && c2<='8'));
	}
	
	/**
	 * Saltea el comentario Simple
	 * @return el siguiente Token
	 * @throws ErrorLexico
	 */
	private Token comentarioSimple() throws ErrorLexico,IOException{
		return recargarLinea();
	}
	/**
	 * Saltea el comentario MultiLinea
	 * @return el siguiente Token
	 * @throws ErrorLexico
	 */
	private Token comentarioMultilinea() throws ErrorLexico,IOException {
		int linea_inicio=nro_linea,nrocol=indice+1;
		indice+=2; //salteo /*
		Token t=null;
		while(linea.indexOf("*/",indice)==-1)
		{
			t= recargarLinea();
			if(linea==null)
				throw new ErrorLexico("ErrorLexico en "+linea_inicio+":"+nrocol+"= comentario multilinea empieza pero nunca termina");
		}
		indice=linea.indexOf("*/",indice)+2; 
		return t;
	}
	/**
	 * Saltea los espacios en blancos y las tabulaciones
	 * @return el token que sigue a los espacios en blanco
	 */
	private Token saltarBlancos() throws ErrorLexico,IOException {
		while(indice<linea.length() && (linea.charAt(indice)==' ' || linea.charAt(indice)==(char)9)){
			indice++;
		}
		return getToken();
	}
	/**
	 * Arma un Identificador de IDMetVar o IDClase
	 * @return
	 */
	private String armarIdentificador() {
		String lex="";
		while(esCharIdent(indice)){
			lex+=linea.charAt(indice);
			indice++;
		}
		return lex;
	}
	/**
	 * decide si elcaracter el la posicion indice siexiste es una letra o un nuemro o un _
	*/
	private boolean esCharIdent(int indice){
		char c;
		return indice<linea.length() && (((c=linea.charAt(indice))>='a'&& c<='z')||(c>='A'&& c<='Z')||(c>='0' && c<='9')|| c=='_'); 
	}

	/**
	*Arma un identificador quesino esuna palabra de lassentencias es una etiqueta
	*/

	private Token identificador() {
		int nrocol=indice+1;
		String lex =armarIdentificador();
		Token ret=null;
		Integer id=opcodes.get(lex);
		if(id==null)
			ret=new Token("Id_Etiq",lex,nro_linea,nrocol);
		else
			ret=new Token(sentencias[id.intValue()],id.intValue(),nro_linea,nrocol);
	
		return ret;	
	}
	
		
	/**
	 * Carga las Sentencias
	 */
	private void CargarSentencias(){

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
	/**
	 * Carga las casos triviales
	 */
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
	/**
	 * Cierra el archivo y devuelve el token EOF
	 * @return un token de tipo EOF
	 */
	private Token findeArchivo(){
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Token("EOF","EOF",nro_linea,indice);
	}
	
}
