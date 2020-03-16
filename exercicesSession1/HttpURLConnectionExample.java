

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileOutputStream;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * @author Robin Van Genechten en Quentin Stroobants
 */
public class HttpURLConnectionExample {
 
 
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
 Socket s=new Socket(host,port);
  if (command.contentEquals("GET")) {   // Get the page, parse images and translate
	  System.out.println("Processing GET command");
  http.sendingGetRequest(host, port, outputdir,s);
  http.findingimage(outputdir +"GET_"+ host + ".HTML", host, port,outputdir,s);
  }
  if (command.contentEquals("HEAD")) {   // Get the page, parse images and translate
	  System.out.println("Processing HEAD command");
  http.sendingHeadRequest(host, port, outputdir,s);
  }
  if (command.contentEquals("PUT")) {   // Get the page, parse images and translate
	  System.out.println("Processing PUT command");
  http.sendingPutRequest(port,s);
  }
  if (command.contentEquals("POST")) {   // Get the page, parse images and translate
	  System.out.println("Processing POST command");
  http.sendingPostRequest(port,s);
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
 private void sendingHeadRequest(String url,int port, String outputdir, Socket s) throws Exception {
 
   
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
 
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
  
  FileWriter myWriter = new FileWriter(  outputdir + "HEAD" + url + ".HTML");
  
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
 private void sendingGetRequest(String url,int port, String outputdir, Socket s) throws Exception {
 
   
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  //// By default it is GET request
  //con.setRequestMethod("GET");
  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
  System.out.println("Sending get request "+ url);
  out.println("GET / HTTP/1.1");
  // remark, this implementation only gets root index file of the url
  out.println("Host: " +url+ ":" +port);
  out.println("");      
 
  
  
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  try { 

	  //s.setSoTimeout(5000);

	// TIMEOUT KAN TER VERVANGING VAN KIJKEN NAAR CHUNKS OF CONTENTH LENGTH;
	// NORMAAL AANTAL BYTES BINNEN HALEN PER CHUNK EN CHECKEN OP VOLGENDE CHUNK TOT EEN CHUNK NUL IS 
	// OF HEEL CONTENTH LENGTH IN EENS BINNEN HALEN
	  String output;
	  
	   boolean header = true;
	   boolean firstline = true;
	   int chunkLength = 0;
	   Properties headerProp = new Properties() ;
	   System.out.println("----HEADER----");
		  while ((output = in.readLine())!=null) {
			//  System.out.println(output);
			  
			  if (header) {
				  if (firstline) {
					  System.out.println(output);
					  String[] statusline=output.split(" ",3);
					  
					  headerProp.setProperty("Protocol",statusline[0]);
					  headerProp.setProperty("StatusCode",statusline[1]);
					  headerProp.setProperty("StatusTxt",statusline[2]);
					  firstline = false;
				  } else {
				  
					  if (output.isEmpty()) {
						  header = false;
						  //headerProp.list(System.out);
						  System.out.println("--- END OF HEADER ---");
					  } else // reading rest of header line by line
					  {
						  headerProp.load((new StringReader(output)));
						  System.out.println(output);
					  }
				  }
			  }
			  else{
				  if (headerProp.containsKey("Content-Length")) {
					  response.append(output);
					  response.append(System.getProperty("line.separator"));
				  } else {
					  // Chuncked content
					  chunkLength = Integer.parseInt(output,16);
					  if (chunkLength > 0) {
						  int counter = 0;
						  while (counter < chunkLength) {
							  // read lines until we have the chunck
						   int responseLengthbefore = response.length();
							  response.append(in.readLine());
							  response.append(System.getProperty("line.separator"));
						   counter = counter + response.length()-responseLengthbefore;
						  }
					  }else
					  {
						  // chunckLength = 0
						  break;
					  }
				  }
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
  
  FileWriter myWriter = new FileWriter( outputdir + "GET_" + url + ".HTML");
  
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
 private void sendingGetRequestforImage(String url, String imagelocation, int port, String outputdir, Socket s) throws Exception {
	 
	  
	  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
		  Properties headerProp = new Properties() ;
	  	  s.setSoTimeout(500);
		  BufferedInputStream in = new BufferedInputStream(s.getInputStream());
		  boolean header = true;
		  byte[] readBytes = new byte[2048];
		  // ASSUMPTION: HEADER WILL NOT BE LONGER THEN 2048 ( or at least the New line empty line mark will not be broken in 2 by the 2048 mark)
		  int counter;
		  while ((counter = in.read(readBytes))!= -1) {
				if (!header) { // Means header is ended
					imageFile.write(readBytes,0,counter); // write all read bytes to file
					
				}
				else {
					boolean imageNotFound = false;
					for (int i =0; i < 2045; i++) {
						if (readBytes[i]==13 && readBytes[i+1]==10 && readBytes[i+2]==13 && readBytes[i+3]==10   ) {
							header = false; // found end of header
							String imgheader = new String(Arrays.copyOfRange(readBytes, 0, i));
							System.out.println ("----IMG HEADER ---");
							System.out.println(imgheader);
							String[] statusline=imgheader.split(System.getProperty("line.separator"))[0].split(" ",3);
							headerProp.setProperty("Protocol",statusline[0]);
							  headerProp.setProperty("StatusCode",statusline[1]);
							  headerProp.setProperty("StatusTxt",statusline[2]);
							  headerProp.load((new StringReader(imgheader)));
							
							System.out.println("----END OF IMG HEADER ---");
							if (headerProp.getProperty("StatusCode").contentEquals("200")) {
								System.out.println("Image Header Parsed, now saving Image");
								imageFile.write(readBytes,i+4,counter-i-4);			
								break;
							}else {
								imageNotFound = true;
								System.out.println("This file could not be found on server");
								System.out.println("Errorcode: "+headerProp.getProperty("StatusCode")+" "+headerProp.getProperty("StatusTxt"));
								
							}
								
						}
						
					}
					if (imageNotFound) {
						break;
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
		System.out.println("ImageSize is: "+ newFile.length());
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
 private void findingimage(String fileName , String serverName, int port, String outputdir, Socket s) throws Exception {
	 File input = new File(fileName);
	 Document doc = Jsoup.parse(input,"UTF-8",serverName);
	 Elements img = doc.getElementsByTag("img");
	 if (img.size()>0) {
		 System.out.println("");
		 System.out.println("retreiving "+ img.size()+" image(s)");
		 int counter = 0;
		 for (Element image : img) {
			 counter +=1;
			 String src = image.attr("src");
			 if (!(src.isEmpty())) {
				 System.out.println("");
				 System.out.println("retreiving image nbr " + counter+ " src: " + src);
				 if (src.charAt(0) != '/'){
						 if (src.regionMatches(0, "http", 0, 4)) {
							 System.out.println("image nbr "+counter+ " is absolutely referenced and will thus not be retreived");
							 
							 continue;
						 }
						 else{
							 src = "/"+src; // SIMPLIFICATION:we should find the folder of the webpage downloaded, but in our examples we always use root pages, so adding root is ok
						 };
				 }
						 
				 
				 sendingGetRequestforImage(serverName, src, port, outputdir,s);
			 	} else
			 	{
			 		System.out.println("uri of images is empty, can not retreive image nbr "+counter);
			 	}
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
 private void sendingPostRequest(int port, Socket s) throws Exception {
	  //Making connection to server
	  //Reading in data
	  BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
	  BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	  System.out.println("waiting for input");
	  TimeUnit.SECONDS.sleep(10);
	  String str2 ="testing";
	  while (str2.equals("testing")) {
	  str2=br.readLine();
	  }
	  // Sending to server
	  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
	  System.out.println("Sending PUT request ");
	  // Setting basic post request
	  out.println("POST / HTTP/1.1");
	  out.println("Host: " +"localhost" + ":"+port);
	  out.println("Content-Type: String");
	  out.println(" Content-Length: " + str2.length());
	  out.println(""); 
	  System.out.println("done with header");
	  // Send post request
	  out.println(str2);
	  // Letting the server now request is done
	  out.println(""); 
	  out.println("done");
	  System.out.println("Put Data : " + str2);
	  //  System.out.println("Response Code : " + responseCode);

	  //waiting for server response
	  String st; 
	  int k = 0;
	  while((st = in.readLine()) != null){  
		  if (st.equalsIgnoreCase("Received your message")) {
			  System.out.println("Server received our message");
             break;
         }
		k += 1;
		System.out.println(k);
	  }
	  System.out.println("POST IS DONE");
	 }
 private void sendingPutRequest(int port, Socket s) throws Exception {
	  //Making connection to server
	  //Reading in data
	  BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
	  BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	  System.out.println("waiting for input");
	 // TimeUnit.SECONDS.sleep(10);
	  String str2 ="testing";
	  while (str2.equals("testing")) {
	  str2=br.readLine();
	  }
	  // Sending to server
	  PrintWriter out = new PrintWriter(s.getOutputStream(),true);
	  System.out.println("Sending PUT request ");
	  // Setting basic post request
	  out.println("PUT / HTTP/1.1");
	  out.println("Host: " +"localhost" + ":"+port);
	  out.println("Content-Type: String");
	  out.println(" Content-Length: " + str2.length());
	  out.println(""); 
	  System.out.println("done with header");
	  // Send post request
	  out.println(str2);
	  // Letting the server now request is done
	  out.println(""); 
	  out.println("done");
	  System.out.println("Put Data : " + str2);
	  //  System.out.println("Response Code : " + responseCode);

	  //waiting for server response
	  String st; 
	  int k = 0;
	  while((st = in.readLine()) != null){  
		  if (st.equalsIgnoreCase("Received your message")) {
			  System.out.println("Server received our message");
              break;
          }
		k += 1;
		System.out.println(k);
	  }
	  System.out.println("PUT IS DONE");
	 }
 public static String slice_end(String s, int endIndex) {
	    if (endIndex < 0) endIndex = s.length() + endIndex;
	    return s.substring(0, endIndex);
	}
}