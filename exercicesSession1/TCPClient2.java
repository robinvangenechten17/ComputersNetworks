import java.net.*;  
import java.io.*;  

class TCPClient2{  
	public static void main(String args[])throws Exception{  
		Socket s=new Socket("localhost",3333);  
	
		DataInputStream din=new DataInputStream(s.getInputStream());  
		
		DataOutputStream dout=new DataOutputStream(s.getOutputStream()); 
		
		//om te typen op keyboard
		//BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
		
		File file = new File("/Users/Quentin/Downloads/hello.txt");
		  
		BufferedReader br = new BufferedReader(new FileReader(file));
		   
		String str="",str2="";
		
		while(!str.equals("stop")){  
			str=br.readLine();  
			dout.writeUTF(str);  
			dout.flush();  
			str2=din.readUTF();  
			System.out.println("Server says: "+str2);  
		}  
		  
		dout.close();  
		s.close();  
	}
}  