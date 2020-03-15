
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.io.File;
import java.io.FileOutputStream;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 
 * A class representing a chat user as an HTTP client that communicates
 * with an HTTP server that serves information such as web pages for end users.
 * This HTTP Client Program supports HTTP/1.1. 
 * To run the program enter 4 arguments divided by one space: Args = [command, uri, portnumber] 
 * The client supports the GET, HEAD, PUT and POST commands.
 * 
 * @author Robin Van Genechten & Quentin Stroobants
 *
 */
public class HttpURLConnectionExample {
 
	/**
	 * The main method to invoke the Client.
	 * 
	 * @param 	args
	 * 			Args = [command, uri, portnumber]
	 * @throws 	...
	 */
 public static void main(String[] args) throws Exception {
 
  HttpURLConnectionExample http = new HttpURLConnectionExample();
  //Define Default Arguments
  for (String s: args) {
      System.out.println(s);
  }
  String language = "NL";
  int port = 80;
  String host= "www.google.com";
  String command = "GET";
  String outputdir = "/Users/robin/eclipse-workspace/Computer Networks/src/";
 switch (args.length){
 	case 5 :
	 	outputdir = args[4];
 	case 4 : 
 		language = args[3];
 	case 3 :
 		port = Integer.parseInt(args[2]);
 	case 2:
 		host = args[1];
 	case 1:
 		command = args[0];
 	default : break;
 }
 
  if (command.contentEquals("GET")) {   // Get the page, parse images and translate
	  System.out.println("Processing GET command");
  http.sendingGetRequest(host, port, outputdir);
  http.findingimage(outputdir + host + ".HTML", host, port,outputdir);
  }
  if (command.contentEquals("HEAD")) {   // Get the page, parse images and translate
	  System.out.println("Processing HEAD command");
  http.sendingHeadRequest(host, port, outputdir);
  }
 }
 // HTTP HEAD request
 /**
	 * Execute the HEAD request and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url as a string.
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	outputdir
	 * 			The local directory to save the result and downloaded image files.
	 * @throws 	...
	 */
 private void sendingHeadRequest(String url,int port, String outputdir) throws Exception {
 
   
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  Socket s=new Socket(url,port);
  //// By default it is GET request
  //con.setRequestMethod("GET");
  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
  System.out.println("Sending get request "+ url);
  out.println("HEAD / HTTP/1.1");
  out.println("Host: " +url+ ":"+port);
  out.println("");      
 
  
  
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  try { 
	  s.setSoTimeout(5000);
	  String output;
	   System.out.println("----HEADER----");
		  while ((output = in.readLine())!=null) {
			//  System.out.println(output);
				  System.out.println(output);
				  response.append(output);
					  
				  }
				  System.out.println("----END OF HEADER----");
  }
  catch (Exception e) {
	  System.out.println("Time Out");
  }
  //printing result from response
  System.out.println("result:");
  System.out.println(response.toString());
  String str = response.toString();
  
  FileWriter myWriter = new FileWriter("HEAD" + outputdir + url + ".HTML");
  
  //schrijft heel de website naar een file
  myWriter.write(str);
  myWriter.close();
  //gaat opzoek naar image
  
  in.close();
  out.close();  
	s.close();  
	System.out.println("closed");
	System.out.println("GET Webpage IS DONE");
	
}
 // HTTP GET request
 /**
	 * Execute the GET request and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url as a string.
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	outputdir
	 * 			The local directory to save the result and downloaded image files.
	 * @throws 	...
	 */
 private void sendingGetRequest(String url,int port, String outputdir) throws Exception {
 
   
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  Socket s=new Socket(url,port);
  //// By default it is GET request
  //con.setRequestMethod("GET");
  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
  System.out.println("Sending get request "+ url);
  out.println("GET / HTTP/1.1");
  out.println("Host: " +url+ ":"+port);
  out.println("");      
 
  
  
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  try { 
	// TIMEOUT IS TER VERVANGING VAN KIJKEN NAAR CHUNKS OF CONTENTH LENGTH;
	// NORMAAL AANTAL BYTES BINNEN HALEN PER CHUNK EN CHECKEN OP VOLGENDE CHUNK TOT EEN CHUNK NUL IS 
	// OF HEEL CONTENTH LENGTH IN EENS BINNEN HALEN
	  s.setSoTimeout(5000); 
	  String output;
	   boolean header = true;
	   System.out.println("----HEADER----");
		  while ((output = in.readLine())!=null) {
			//  System.out.println(output);
			  if (header) {
				  System.out.println(output);
				  if (output.isEmpty()) {
					  header = false;
					  System.out.println("----END OF HEADER----");
				  }
			  }
			  else{
			  response.append(output);
			}
  }
  }
  catch (Exception e) {
	  System.out.println("Time Out");
  }
  //printing result from response
  System.out.println("result:");
  System.out.println(response.toString());
  String str = response.toString();
  
  FileWriter myWriter = new FileWriter("GET" +outputdir + url + ".HTML");
  
  //schrijft heel de website naar een file
  myWriter.write(str);
  myWriter.close();
  //gaat opzoek naar image
  
  in.close();
  out.close();  
	s.close();  
	System.out.println("closed");
	System.out.println("GET Webpage IS DONE");
	
}

 private void sendingGetRequestforImage(String url, String imagelocation, int port, String outputdir) throws Exception {

 /**
	 * Execute the GET request for an image and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url as a string.
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	imagelocation
	 * 			The src of the image.
	 * @param	outputdir
	 * 			the location on local disk to put the image files
	 * @throws 	...
	 */
	 
	  
	  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  Socket s=new Socket(url,port);
	  System.out.println("Sending get request : "+ url);
	  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
	  System.out.println("Sending get request "+ url);
	  out.println("GET " +imagelocation +" HTTP/1.1");
	  out.println("Host: " +url+ ":"+port);
	  out.println(""); 
	 
	  // Create file to save the image (inlcuding a relative path)
	  File newFile = new File(outputdir + imagelocation);
	  newFile.getParentFile().mkdirs();
	  newFile.createNewFile();
	  final FileOutputStream imageFile= new FileOutputStream(outputdir + imagelocation);
	  
	  
	  
	  try { 
		  s.setSoTimeout(5000);
		  BufferedInputStream in = new BufferedInputStream(s.getInputStream());
		  boolean header = true;
		  byte[] readBytes = new byte[2048];
		  // ASSUMPTION: HEADER WILL NOT BE LONGER THEN 2048 ( or at least the New line empty line mark will not be broken in 2 by the 2048 mark)
		  int counter;
		  while ((counter = in.read(readBytes))!= -1) {
				if (!header) { // Means header is ended
					imageFile.write(readBytes,0,counter); // write all read bytes to file
					
				}
				else
					for (int i =0; i < 2045; i++) {
						if (readBytes[i]==13 && readBytes[i+1]==10 && readBytes[i+2]==13 && readBytes[i+3]==10   ) {
							header = false; // found end of header
							String imgheader = new String(Arrays.copyOfRange(readBytes, 0, i-1));
							System.out.println ("----IMG HEADER ---");
							System.out.println(imgheader);
							System.out.println("----END OF IMG HEADER ---");
							System.out.println("Image Header Parsed, now saving Image");
							imageFile.write(readBytes,i+4,2048-i-4);
							break;
						}
						else
						{
							
						}
					}
		  }
			
					 
	  }
	  
	  catch (Exception e) {
		  System.out.println("Time Out");
	  }
	  //printing result from response
	  //gaat opzoek naar image
	  
	  
	  out.close();  
		s.close();  
		imageFile.close();
		System.out.println("closed");
		System.out.println("GET IMAGE IS DONE");
	}

 /**
	 * Finds all images on a HTML page and stores them in local files
	 * @param 	fileName
	 * 			The name of the new local files to store the images.
	 * @param 	ServerName
	 * 			The given server as a string.
	 * @param 	port
	 * 			he given port , mostly 80.
	 * @param 	outputdir
	 * 			The location on local disk to store the downloaded images
	 * @throws 	...
	 */
 private void findingimage(String fileName , String serverName, int port, String outputdir) throws Exception {
	 File input = new File(fileName);
	 Document doc = Jsoup.parse(input,"UTF-8",serverName);
	 Elements img = doc.getElementsByTag("img");
	 if (img.size()>0) {
		 System.out.println("retreiving "+ img.size()+" image(s)");
		 int counter = 0;
		 for (Element image : img) {
			 counter +=1;
			 String src = image.attr("src");
			 System.out.println("retreiving image nbr " + counter+ " src: " + src);
			 sendingGetRequestforImage(serverName, src, port, outputdir);
		 }
		 } else
	 	{
	 		System.out.println("No images Found");
	 	}
	 }
 
 
 // HTTP Post request
 /**
	 * Execute the POST request.
	 */
 private void sendingPostRequest() throws Exception {
 
  Socket s=new Socket("localhost",80); 
  DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
  System.out.println("Sending get request "+ url);
  out.println("GET / HTTP/1.1");
  out.println("Host: " +url+ ":"+port);
  out.println(""); 
	     
        // Setting basic post request
 // con.setRequestMethod("POST");
// con.setRequestProperty("User-Agent", USER_AGENT);
// con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
// con.setRequestProperty("Content-Type","application/json");
  BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
  String str2 ="";
  str2=br.readLine();  
//  String postJsonData = "{"+"id"+":5"+","+"countryName"+":"+"USA"+","+"population"+":8000"+"}";
  
  // Send post request
//  con.setDoOutput(true);
  DataOutputStream wr = new DataOutputStream(s.getOutputStream());
  wr.writeBytes(str2);
  wr.flush();
  wr.close();
 
  //int responseCode = con.getResponseCode();
  System.out.println("Post Data : " + str2);
//  System.out.println("Response Code : " + responseCode);
 
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  String output;
  StringBuffer response = new StringBuffer();
 
  while ((output = in.readLine()) != null) {
   response.append(output);
  }
  System.out.println(response.toString());
  System.out.println("closing");
  in.close();
  s.close();
  dout.close();  
  System.out.println("closed");
  //printing result from response
  
  System.out.println("POST IS DONE");
 }
}