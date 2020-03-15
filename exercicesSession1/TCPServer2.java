import java.net.*;  
import java.io.*;  

class TCPServer2{  
	public static void main(String args[])throws Exception{  
		
		ServerSocket ss = new ServerSocket(3333);  
		
		Socket s=ss.accept();  
		
		DataInputStream din=new DataInputStream(s.getInputStream());  
		
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
		
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
		  
		String str="",str2="";  
		
		while(!str.equals("stop")){  
			str=din.readUTF();  
			FileWriter myWriter = new FileWriter("/Users/Quentin/Downloads/result.txt");
		    //myWriter.write("Files in Java might be tricky, but it is fun enough!");
			myWriter.write(str);
		    myWriter.close();
		    System.out.println("Successfully wrote to the file.");
			System.out.println("client says: "+str);  
			str2=br.readLine();  
			dout.writeUTF(str2);  
			dout.flush();  
		}  
		din.close();  
		s.close();  
		ss.close();  
	}
} 