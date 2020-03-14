

 
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * @author Robin Van Genechten en Quentin
 */
public class HttpURLConnectionExample {
 
 
 public static void main(String[] args) throws Exception {
 
  HttpURLConnectionExample http = new HttpURLConnectionExample();
 
     // Sending get request
  http.sendingGetRequest("www.google.com");
  
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
  dout.writeBytes("GET / HTTP/1.1 \n\n");
	      
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
	System.out.println("GET IS DONE");
}
 private void sendingGetRequestforImage(String page) throws Exception {
	 
	  String url = page; 
	  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  Socket s=new Socket(url,80);
	  //// By default it is GET request
	  //con.setRequestMethod("GET");
	  DataOutputStream dout=new DataOutputStream(new DataOutputStream(s.getOutputStream()));  
	  dout.writeBytes("GET /images/branding/googlelogo/2x/googlelogo_color_272x92dp.png  HTTP/1.1 \n\n");
		      
	 // dout.writeBytes("GET / HTTP/1.1\n");
	 // dout.writeBytes("Host: wlab.cs.bilkent.edu.tr:80\n\n"); 

	  ////add request header
	  // con.setRequestProperty("User-Agent", USER_AGENT);
	 
	  //int responseCode = con.getResponseCode();
	  System.out.println("Sending get request : "+ url);
	//  System.out.println("Response code : "+ responseCode);
	 
	  // Reading response from input Stream
	  StringBuffer response = new StringBuffer();
	  //BufferedInputStream in = new BufferedInputStream(s.getInputStream())
	  BufferedReader in = new BufferedReader(
	          new InputStreamReader(s.getInputStream()));
	  ByteArrayOutputStream out = new ByteArrayOutputStream();
	  try { 
		  s.setSoTimeout(5000);
		  String output;
		   boolean header = true;
			  while (((output = in.readLine())!=null)&(header=true)) {
				//  System.out.println(output);
				  
					  if (output.isEmpty()) {
						  header = false;
					  }
			  } // Skipped the header
			
			  while ()
			int n = 0;
			while (-1!=(n=in.read())) {
			 out.write(buf,0,n);
					 
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
		System.out.println("GET IS DONE");
	}

 private void findingimage(String fileName) throws Exception {
	 File input = new File(fileName);
	 Document doc = Jsoup.parse(input,"UTF-8");
	 Elements img = doc.getElementsByTag("img");
	 for (Element image : img) {
		 String src = image.absUrl("src");
		 System.out.println("Image found");
		 System.out.println("src is;" + src);
		 getImage(src);
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