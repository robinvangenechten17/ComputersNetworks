

 
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
 
     // Sending get request
  http.sendingGetRequest("www.google.com");
  //http.sendingGetRequestforImage("www.google.com", "/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png");
    // Sending post request
  //http.sendingPostRequest();
 
 }
 
 // HTTP GET request
 private void sendingGetRequest(String page) throws Exception {
 
  String url = page; 
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  Socket s=new Socket(url,80);
  //// By default it is GET request
  //con.setRequestMethod("GET");
  DataOutputStream dout=new DataOutputStream(new DataOutputStream(s.getOutputStream()));  
  dout.writeBytes("GET /  HTTP/1.1 \r\n");
  dout.writeBytes("Host: " + url +"\r\n"); 
  dout.writeBytes("\r\n");// add an emptyline
	      
 // dout.writeBytes("GET / HTTP/1.1\n");
 // dout.writeBytes("Host: wlab.cs.bilkent.edu.tr:80\n\n"); 

  ////add request header
  // con.setRequestProperty("User-Agent", USER_AGENT);
 
  //int responseCode = con.getResponseCode();
  System.out.println("Sending get request : "+ url);
//  System.out.println("Response code : "+ responseCode);
 
  // Reading response from input Stream
  StringBuffer response = new StringBuffer();
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  try { 
	  s.setSoTimeout(5000);
	  String output;
	   boolean header = true;
		  while ((output = in.readLine())!=null) {
			//  System.out.println(output);
			  if (header) {
				  if (output.isEmpty()) {
					  header = false;
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
  
  FileWriter myWriter = new FileWriter("/Users/robin/eclipse-workspace/Computer Networks/src/" + url + ".HTML");
  
  //schrijft heel de website naar een file
  myWriter.write(str);
  myWriter.close();
  //gaat opzoek naar image
  
  in.close();
  dout.close();  
	s.close();  
	System.out.println("closed");
	System.out.println("GET Webpage IS DONE");
	System.out.println("retreiving images");
	
	findingimage("/Users/robin/eclipse-workspace/Computer Networks/src/" + url + ".HTML",url);
}
 private void sendingGetRequestforImage(String server, String imagelocation) throws Exception {
	 
	  String url = server; 
	  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  Socket s=new Socket(url,80);
	  //// By default it is GET request
	  //con.setRequestMethod("GET");
	  DataOutputStream dout=new DataOutputStream(new DataOutputStream(s.getOutputStream()));  
	  dout.writeBytes("GET " +imagelocation +"  HTTP/1.1 \n\n");
		      
	 // dout.writeBytes("GET / HTTP/1.1\n");
	 // dout.writeBytes("Host: wlab.cs.bilkent.edu.tr:80\n\n"); 

	  ////add request header
	  // con.setRequestProperty("User-Agent", USER_AGENT);
	 
	  //int responseCode = con.getResponseCode();
	  System.out.println("Sending get request : "+ url);
	//  System.out.println("Response code : "+ responseCode);
	 
	  // Create file to save the image (inlcuding a relative path)
	  File newFile = new File("c:/Users/robin/eclipse-workspace/Computer Networks/src" + imagelocation);
	  newFile.getParentFile().mkdirs();
	  newFile.createNewFile();
	  final FileOutputStream imageFile= new FileOutputStream("c:/Users/robin/eclipse-workspace/Computer Networks/src" + imagelocation);
	  
	  
	  
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
							System.out.println("Header Parsed");
							imageFile.write(readBytes,i+4,2048-i-4);
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
	  
	  
	  dout.close();  
		s.close();  
		imageFile.close();
		System.out.println("closed");
		System.out.println("GET IS DONE");
	}

 private void findingimage(String fileName , String serverName) throws Exception {
	 File input = new File(fileName);
	 Document doc = Jsoup.parse(input,"UTF-8",serverName);
	 Elements img = doc.getElementsByTag("img");
	 for (Element image : img) {
		 String src = image.attr("src");
		 System.out.println("Image found");
		 System.out.println("src is;" + src);
		 sendingGetRequestforImage(serverName, src);
	 }
 }
 private void getImage(String src) {
	 
 }
 // HTTP Post request
 private void sendingPostRequest() throws Exception {
 
  String url = "www.example.com";
  //URL obj = new URL(url);
  //HttpURLConnection con = (HttpURLConnection) obj.openConnection();
  Socket s=new Socket(url,80);
  DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
  dout.writeBytes("POST http://www.example.com  HTTP/1.1 \n\n");
	     
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
  System.out.println("nSending 'POST' request to URL : " + url);
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