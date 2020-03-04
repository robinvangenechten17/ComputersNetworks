

import java.io.*; 

public class Ex1ReadingAndPrintingFile {

	
	// Java Program to illustrate reading from FileReader 
	// using BufferedReader 
	
	public static void main(String[] args)throws Exception 
	  { 
		
		String desktop = System.getProperty ("user.home") + "/Desktop/";
		System.out.print(desktop);
	  // We need to provide file path as the parameter: 
	  // double backquote is to avoid compiler interpret words 
	  // like \test as \t (ie. as a escape sequence) 
		
	  File file = new File("/Users/Quentin/Downloads/hello.txt");
	  
	  BufferedReader br = new BufferedReader(new FileReader(file)); 
	  
	  String st; 
	  while ((st = br.readLine()) != null) 
	    System.out.println(st); 
	  } 
}
