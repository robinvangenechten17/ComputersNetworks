

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
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * @author Robin Van Genechten en Quentin Stroobants
 */
public class HttpURLConnectionExample {
	/**
	 * Processes 4 different commands clientside; HEAD GET PUT POST
	 * @param args
	 * 			command = args[0]
	 * 			host = args[1]
	 * 			Integer.parseInt(args[2])
	 * 			language = args[3]
	 * 			webpage = args[4]
	 * 			outputdir = args[5]
	 * @throws Exception
	 */
 
 public static void main(String[] args) throws Exception {
  HttpURLConnectionExample http = new HttpURLConnectionExample();
  //Define Default Arguments
  for (String s: args) {
      System.out.println(s);
  }
  Boolean vertaling = true; //PUT ON TRUE FOR TRANSLATION; Takes alot of time
  String language = "NL"; //vertalen naar
  String languageFrom = "NL"; //vertalen van
  int port = 80;
  String host= "www.google.com";
  String command = "GET";
  String outputdir = "/Users/robin/eclipse-workspace/Computer Networks/src/";
  String webpage = "/";
 switch (args.length){
 	case 6 :
	 	outputdir = args[5];
 	case 5 :
 		webpage = args[4];
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
 PrintWriter out = new PrintWriter(s.getOutputStream(),true);
 BufferedReader in = new BufferedReader(
         new InputStreamReader(s.getInputStream()));
  if (command.contentEquals("GET")) {   // Get the page, parse images and translate
	  System.out.println("Processing GET command");
  http.sendingGetRequest(host, port, outputdir,s, webpage, out , in);
  http.findingimage(outputdir +"GET_"+ host + ".HTML", host, port,outputdir,s,out);
  }
  if (command.contentEquals("HEAD")) {   // Get the page, parse images and translate
	  System.out.println("Processing HEAD command");
  http.sendingHeadRequest(host, port, outputdir,s, webpage, out, in);
  }
  if (command.contentEquals("PUT")) {   // Get the page, parse images and translate
	  System.out.println("Processing PUT command");
  http.sendingPutRequest(port,s);
  }
  if (command.contentEquals("POST")) {   // Get the page, parse images and translate
	  System.out.println("Processing POST command");
  http.sendingPostRequest(port,s);
  }
  if (vertaling) {
	  System.out.println("Processing Translation");
  http.vertaler(outputdir, host, languageFrom, language);
  }
  
  System.out.println("socket status closed= " + (s.isClosed()==true) +" => now closing connection");
  s.close();
  System.out.println("socket status closed= " + (s.isClosed()==true));
  
  in.close();
  out.close();  
 }
 // HTTP HEAD request
 /**
	 * Execute the HEAD request and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url; the host as a string.
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	outputdir
	 * 			The local directory to save the result and downloaded image files.
	 * @param 	webpage
	 * 			The webpage we like to search.
	 * @param 	s
	 * 			The active socket
	 * @param 	out
	 * 			The PrintWriter of outputstream
	 * @param 	in
	 * 			BufferedReader of inputstream
	 * @throws 	Exception e
	 */
 private void sendingHeadRequest(String url,int port, String outputdir, Socket s, String webpage, PrintWriter out, BufferedReader in) throws Exception {
 
 
  System.out.println("Sending get request "+ url);
  out.println("HEAD " +webpage +" HTTP/1.1");
  out.println("Host: " +url+ ":"+port);
  out.println("Connection: keep-alive");
  out.println("");      
 
  
  
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
 // BufferedReader in = new BufferedReader(
 //         new InputStreamReader(s.getInputStream()));
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
  
  //schrijft naar een file
  myWriter.write(str);
  myWriter.close();
 
  
  // in.close();
  // out.close();  
	//s.close();  
	//System.out.println("closed");
	System.out.println("HEAD Webpage IS DONE");
	
}
 // HTTP GET request
 /**
	 * Execute the GET request and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url; the host as a string.
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	outputdir
	 * 			The local directory to save the result and downloaded image files.
	 * @param 	webPage
	 * 			The webpage we like to search.
	 * @param 	s
	 * 			The active socket
	 * @param 	out
	 * 			The PrintWriter of outputstream
	 * @param 	in
	 * 			BufferedReader of inputstream
	 * @throws 	Exception e
	 */
 private void sendingGetRequest(String url,int port, String outputdir, Socket s, String webPage, PrintWriter out, BufferedReader in ) throws Exception {
 
   
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  //// By default it is GET request
  //con.setRequestMethod("GET");
 // PrintWriter out = new PrintWriter(s.getOutputStream(),true);
  System.out.println("Sending get request "+ url);
  out.println("GET " + webPage +" HTTP/1.1");
  // remark, this implementation only gets root index file of the url
  out.println("Host: " +url+ ":" +port);
  out.println("Connection: keep-alive");
  out.println("");      
 
  
  
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
 // BufferedReader in = new BufferedReader(
 //         new InputStreamReader(s.getInputStream()));
  try { 

	  s.setSoTimeout(1000);

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
				  if (headerProp.containsKey("Content-length")) {
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
	  System.out.println(e);
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
  
  FileWriter myWriter2 = new FileWriter( outputdir + "GET_" + url + ".txt");
  
  //schrijft heel de website naar een file
  myWriter2.write(str);
  myWriter2.close();
  
  
 // in.close();
 // out.close();  
	//s.close();  
	//System.out.println("closed");
	System.out.println("GET Webpage IS DONE");
	
}
 /**
	 * Execute the GET request for images and saves them into local files.
	 * 
	 * @param 	url
	 * 			The given url; the host as a string.
	 * @param 	imagelocation
	 * 			The url of the imagelocation as a string
	 * @param 	port
	 * 			The given port , mostly 80.
	 * @param 	outputdir
	 * 			The local directory to save the result and downloaded image files.
	 * @param 	s
	 * 			The active socket
	 * @param 	out
	 * 			The PrintWriter of outputstream
	 * @param 	in
	 * 			BufferedReader of inputstream
	 * @throws 	Exception e
	 */
 private void sendingGetRequestforImage(String url, String imagelocation, int port, String outputdir, Socket s, PrintWriter out) throws Exception {
	 
	  
	  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  System.out.println("Sending get request : "+ url);
	
	  out.println("GET " +imagelocation +" HTTP/1.1");
	  out.println("Host: " +url +":"+port); 
	  out.println("Connection: keep-alive");
	  out.println(""); 
	 
	  // Create file to save the image (inlcuding a relative path)
	  File newFile = new File(outputdir + imagelocation);
	  newFile.getParentFile().mkdirs();
	  newFile.createNewFile();
	  final FileOutputStream imageFile= new FileOutputStream(outputdir + imagelocation);
	  
	  
	  
	  try { 
		  Properties headerProp = new Properties() ;
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
				else {
					boolean imageNotFound = false;
					for (int i =0; i < 2045; i++) {
						if (readBytes[i]==13 && readBytes[i+1]==10 && readBytes[i+2]==13 && readBytes[i+3]==10   ) { //EMPTY LINE
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
	  
	  
	//  out.close();  
	//	s.close();
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
	 * @param 	s
	 * 			The active socket
	 * @param 	out
	 * 			The PrintWriter of outputstream
	 * @throws 	Exception
	 */
 private void findingimage(String fileName , String serverName, int port, String outputdir, Socket s , PrintWriter out  ) throws Exception {
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
						 
				
				 sendingGetRequestforImage(serverName, src, port, outputdir,s, out); 
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
	 * @param 	port
	 * 			the given port , mostly 80.
	 * @param 	s
	 * 			The active socket
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
	  System.out.println("Done with sending our header");
	  // Send post request
	  out.println(str2);
	  // Letting the server now request is done
	  out.println(); 
	  out.println("done");
	  System.out.println("Put Data : " + str2);
	  //  System.out.println("Response Code : " + responseCode);

	  //waiting for server response
	  String st; 
	  System.out.println("From server:");
	  while((st = in.readLine()) != null){  
		  System.out.println(st);
		  if (st.equalsIgnoreCase("Received your message")) {
			  System.out.println("Server received our message");
             break;
         }
	  }
	  System.out.println("POST IS DONE");
	 }
 /**
	 * Execute the PUT request.
	 * @param 	port
	 * 			the given port , mostly 80.
	 * @param 	s
	 * 			The active socket
	 */
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
	  System.out.println("Done with sending our header");
	  // Send post request
	  out.println(str2);
	  // Letting the server now request is done
	  out.println(); 
	  out.println("done");
	  System.out.println("Put Data : " + str2);
	  //  System.out.println("Response Code : " + responseCode);

	  //waiting for server response
	  String st; 
	  System.out.println("From server:");
	  while((st = in.readLine()) != null){  
		  System.out.println(st);
		  if (st.equalsIgnoreCase("Received your message")) {
			  System.out.println("Server received our message");
              break;
          }
	  }
	  System.out.println("PUT IS DONE");
	 }
 /**
  * Translates a webpage to a given language
  * @param outputdir 
  * 	   The places of our files and where we like to save the files to
  * @param url
  * 	   The webpage we like to translate
  * @param taalfrom
  * 	   The original language of the webpage
  * @param taalTo
  * 	   The language we like to translate the webpage to
  * @throws IOException
  */
 public void vertaler(String outputdir , String url, String taalfrom , String taalTo) throws IOException {
	 if (taalfrom.equals(taalTo)) {
		return;
	 }
	 File file = new File(outputdir + "GET_" + url + ".txt");
	 BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer str = new StringBuffer();
		String output;
		String vertalingoutput;
		String taal1= taalfrom;
		String taal2= taalTo;
			 while ((output = br.readLine())!=null) {
					//  System.out.println(output);
				 try{ 
					 vertalingoutput=translate(taal1, taal2, output.toString());
					 str.append(vertalingoutput);
					 str.append(System.getProperty("line.separator"));
				 }
				 catch(Exception e) {
				//	 System.out.println(e);
					 System.out.println("In deze lijn was niets te vertalen");
					 str.append(output);
					 str.append(System.getProperty("line.separator"));
				 }
						  }
		String text =str.toString();	 
		FileWriter myWriter = new FileWriter( outputdir + "VERTALER_" +"FROM"+taal1+"TO"+taal2 + url + ".HTML");
		myWriter.write(text);
		myWriter.close();
  System.out.println("Translated text: " + text);
	 }
/**
 *  
 * @param langFrom 
 * 		  The original language of the text
 * @param langTo
 * 		  The language we like the text to be
 * @param text
 * 		  String of text; example; a website
 * @return
 * @throws IOException
 */
 private static String translate(String langFrom, String langTo, String text) throws IOException {
     // INSERT YOU URL HERE
     String urlStr = "https://script.google.com/macros/s/AKfycbzQQmxNvc0FRtP0KxOyypAn70HLcOCW1mxlnYuytpuf6kb18oDW/exec" +
             "?q=" + URLEncoder.encode(text, "UTF-8") +
             "&target=" + langTo +
             "&source=" + langFrom;
     URL url = new URL(urlStr);
     StringBuilder response = new StringBuilder();
     HttpURLConnection con = (HttpURLConnection) url.openConnection();
     BufferedReader in = new BufferedReader(new InputStreamReader(((HttpURLConnection) (new URL(urlStr)).openConnection()).getInputStream(), Charset.forName("UTF-8")));
     con.setRequestProperty("User-Agent", "Mozilla/5.0");
     String inputLine;
     while ((inputLine = in.readLine()) != null) {
         response.append(inputLine);
     }
     in.close();
     
     return response.toString();
 }
 
 /**
  * Slices a string; helpfunction
  * @param s
  * 		  String to slice
  * @param endIndex
  * 		  Place to slice
  * @return
  */
 public static String slice_end(String s, int endIndex) {
	    if (endIndex < 0) endIndex = s.length() + endIndex;
	    return s.substring(0, endIndex);
	}
}