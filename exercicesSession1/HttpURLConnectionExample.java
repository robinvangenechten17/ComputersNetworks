

 
import java.io.BufferedReader;
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
/**
 * @author Robin Van Genechten en Quentin
 */
public class HttpURLConnectionExample {
 
 
 public static void main(String[] args) throws Exception {
 
  HttpURLConnectionExample http = new HttpURLConnectionExample();
 
     // Sending get request
 // http.sendingGetRequest();
  
    // Sending post request
  http.sendingPostRequest();
 
 }
 
 // HTTP GET request
 private void sendingGetRequest() throws Exception {
 
  String url = "www.example.com"; 
  // HttpURLConnection con = (HttpURLConnection) url.openConnection();
  Socket s=new Socket(url,80);
  //// By default it is GET request
  //con.setRequestMethod("GET");
  DataOutputStream dout=new DataOutputStream(new DataOutputStream(s.getOutputStream()));  
  dout.writeBytes("GET http://www.example.com  HTTP/1.1 \n\n");
	      
 // dout.writeBytes("GET / HTTP/1.1\n");
 // dout.writeBytes("Host: wlab.cs.bilkent.edu.tr:80\n\n"); 

  ////add request header
  // con.setRequestProperty("User-Agent", USER_AGENT);
 
  //int responseCode = con.getResponseCode();
  System.out.println("Sending get request : "+ url);
//  System.out.println("Response code : "+ responseCode);
 
  // Reading response from input Stream
  BufferedReader in = new BufferedReader(
          new InputStreamReader(s.getInputStream()));
  String output;
  StringBuffer response = new StringBuffer();
	  while ((output = in.readLine())!=null) {
		  System.out.println(output);
		  response.append(output);
		}
  System.out.println("out while");
  //printing result from response
  System.out.println("result:");
  System.out.println(response.toString());
  String str = response.toString();
  FileWriter myWriter = new FileWriter("/Users/robin/eclipse-workspace/Computer Networks/src/result");
  
  //myWriter.write("Files in Java might be tricky, but it is fun enough!");
  myWriter.write(str);
  myWriter.close();
  in.close();
  dout.close();  
	s.close();  
	System.out.println("closed");
	System.out.println("GET IS DONE");
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
  in.close();
  s.close();
  dout.close();  
  System.out.println("closed");
  //printing result from response
  
  System.out.println("GET IS DONE");
 }
}