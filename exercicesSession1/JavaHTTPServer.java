//package Practicum;

import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.PrintWriter;

import java.net.ServerSocket;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import java.util.StringTokenizer;


// Started from https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java 15/03/2020

// Each Client Connection will be managed in a dedicated Thread

public class JavaHTTPServer implements Runnable{ 

	static final File WEB_ROOT = new File(".");

	static final String DEFAULT_FILE = "htmlPages/index.html";

	static final String FILE_NOT_FOUND = "htmlPages/404.html";
	
	static final String BAD_REQUEST = "htmlPages/400.html";
	
	static final String SERVER_ERROR = "htmlPages/500.html";

	static final String METHOD_NOT_SUPPORTED = "htmlPages/not_supported.html";

	// port to listen connection

	static final int PORT = 80;
	
	// verbose mode

	static final boolean verbose = true;

	// Client Connection via Socket Class

	private Socket connect;
	
	public JavaHTTPServer(Socket c) {

		connect = c;

	}
	//To store files 
	String outputdir = "/Users/robin/eclipse-workspace/Computer Networks/src/";

	public static void main(String[] args) {

		try {

			ServerSocket serverConnect = new ServerSocket(PORT);

			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

			// we listen until user halts server execution

			while (true) {

				JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());
				
				
				
				if (verbose) {

					System.out.println("Connecton opened. (" + new Date() + ")");

				}

				// create dedicated thread to manage the client connection

				Thread thread = new Thread(myServer);

				thread.start();

			}

		} catch (IOException e) {
			
			System.err.println("Server Connection 500 error : " + e.getMessage());

		}

	}

	@Override

	public void run() {

		// we manage our particular client connection

		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;

		String fileRequested = null;

		String Httpversion = null;

		try {

			// we read characters from the client via input stream on the socket

			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));

			// we get character output stream to client (for headers)

			out = new PrintWriter(connect.getOutputStream());

			// get binary output stream to client (for requested data)

			dataOut = new BufferedOutputStream(connect.getOutputStream());

			// get first line of the request from the client

			String input = in.readLine();

			// we parse the request with a string tokenizer

			StringTokenizer parse = new StringTokenizer(input);

			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

			// we get file requested

			fileRequested = parse.nextToken().toLowerCase();

			// we get the HTTP version used
			
			Httpversion = parse.nextToken().toUpperCase();

			// we support only GET and HEAD methods, we check

			if (!method.equals("GET")  &&  !method.equals("HEAD") && !method.equals("POST") && !method.equals("PUT") ) {

				if (verbose) {

					System.out.println("501 Not Implemented : " + method + " method.");

				}
				try {

					MethodNotSupported(out, dataOut, fileRequested);

				} catch (IOException ioe) {
					try {
						ServerError(out, dataOut, "");
					} catch (IOException e) {
						System.err.println("Server error : " + ioe);
					}
					System.err.println("Error with MethodNotSupported : " + ioe);

				}
			} else {
				
				if (Httpversion.equals("HTTP/1.1")){  
					
					// GET or HEAD method
	
					if ( fileRequested.endsWith("/")) {
	
						fileRequested += DEFAULT_FILE;
	
					}
					if (method.equals("GET")  ||  method.equals("HEAD")) {
					File file = new File(WEB_ROOT, fileRequested);
	
					int fileLength = (int) file.length();
	
					String content = getContentType(fileRequested);
	
					if (method.equals("GET")) { // GET method so we return content
	
						byte[] fileData = readFileData(file, fileLength);
	
						
	
						// send HTTP Headers
	
						out.println("HTTP/1.1 200 OK");
	
						out.println("Server: Java HTTP Server from Quentin  and Robin : 1.0");
	
						out.println("Date: " + new Date());
	
						out.println("Content-type: " + content);
	
						out.println("Content-length: " + fileLength);
	
						out.println(); // blank line between headers and content, very important !
	
						out.flush(); // flush character output stream buffer
	
						
	
						dataOut.write(fileData, 0, fileLength);
	
						dataOut.flush();
	
					}
					if (verbose) {
	
						System.out.println("File " + fileRequested + " of type " + content + " returned");
	
					}
					}
					else if (method.equals("PUT")) {
						try {
							Put(connect,in,outputdir,out);
						} catch (Exception e) {
							System.out.println(e);
							ServerError(out, dataOut, "PUT");
						}
					}
					else if (method.equals("POST")) {
						try {
							Post(in,outputdir,out);
						} catch (Exception e) {
							System.out.println(e);
							ServerError(out, dataOut,"POST");
						}
					}
				}else {
					try {

						BadRequest(out, dataOut, fileRequested);

					} catch (IOException ioe) {
						try {
							ServerError(out, dataOut,"");
						} catch (IOException e) {
							System.err.println("Server error : " + ioe);
						}
						System.err.println("Error with BadRequest : " + ioe);

					}
				}
			}
		} catch (FileNotFoundException fnfe) {

			try {

				fileNotFound(out, dataOut, fileRequested);

			} catch (IOException ioe) {
				try {
					ServerError(out, dataOut,"");
				} catch (IOException e) {
					System.err.println("Server error : " + ioe);
				}
				System.err.println("Error with file not found exception : " + ioe.getMessage());

			}
		} catch (IOException ioe) {
			try {
				ServerError(out, dataOut,"");
			} catch (IOException e) {
				System.err.println("Server error : " + ioe);
			}
			System.err.println("Server error : " + ioe);

		} finally {

			try {

				in.close();

			//	out.close();

				dataOut.close();

			//	connect.close(); // we close socket connection

			} catch (Exception e) {

				System.err.println("Error closing stream : " + e.getMessage());

			} 
			if (verbose) {

				System.out.println("Connection closed.\n");

			}
		}
	}

	

	private byte[] readFileData(File file, int fileLength) throws IOException {

		FileInputStream fileIn = null;

		byte[] fileData = new byte[fileLength];

		

		try {

			fileIn = new FileInputStream(file);

			fileIn.read(fileData);

		} finally {

			if (fileIn != null) 

				fileIn.close();

		}

		

		return fileData;

	}

	

	// return supported MIME Types

	private String getContentType(String fileRequested) {

		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))

			return "text/html";

		else

			return "text/plain";

	}

	

	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {

		File file = new File(WEB_ROOT, FILE_NOT_FOUND);

		int fileLength = (int) file.length();

		String content = "text/html";

		byte[] fileData = readFileData(file, fileLength);

		

		out.println("HTTP/1.1 404 File Not Found");

		out.println("Server: Java HTTP Server from SSaurel : 1.0");

		out.println("Date: " + new Date());

		out.println("Content-type: " + content);

		out.println("Content-length: " + fileLength);

		out.println(); // blank line between headers and content, very important !

		out.flush(); // flush character output stream buffer

		

		dataOut.write(fileData, 0, fileLength);

		dataOut.flush();

		

		if (verbose) {

			System.out.println("File " + fileRequested + " not found");

		}

	}
	private void BadRequest(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, BAD_REQUEST);

		int fileLength = (int) file.length();

		String contentMimeType = "text/html";

		//read content to return to client

		byte[] fileData = readFileData(file, fileLength);

			

		// we send HTTP Headers with data to client

		out.println("HTTP/1.1 400 BAD REQUEST");

		out.println("Server: Java HTTP Server from Robin and Quentin : 1.0");

		out.println("Date: " + new Date());

		out.println("Content-type: " + contentMimeType);

		out.println("Content-length: " + fileLength);

		out.println(); // blank line between headers and content, very important !

		out.flush(); // flush character output stream buffer

		// file

		dataOut.write(fileData, 0, fileLength);

		dataOut.flush();

		

	}
	private void MethodNotSupported(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		// we return the not supported file to the client

		File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);

		int fileLength = (int) file.length();

		String contentMimeType = "text/html";

		//read content to return to client

		byte[] fileData = readFileData(file, fileLength);

			

		// we send HTTP Headers with data to client

		out.println("HTTP/1.1 501 Not Implemented");

		out.println("Server: Java HTTP Server from Robin and Quentin : 1.0");

		out.println("Date: " + new Date());

		out.println("Content-type: " + contentMimeType);

		out.println("Content-length: " + fileLength);

		out.println(); // blank line between headers and content, very important !

		out.flush(); // flush character output stream buffer

		// file

		dataOut.write(fileData, 0, fileLength);

		dataOut.flush();
	}
	private void ServerError(PrintWriter out, OutputStream dataOut, String explanation) throws IOException {
		// we return the not supported file to the client

		File file = new File(WEB_ROOT, SERVER_ERROR);

		int fileLength = (int) file.length();

		String contentMimeType = "text/html";

		//read content to return to client

		byte[] fileData = readFileData(file, fileLength);

			

		// we send HTTP Headers with data to client

		out.println("HTTP/1.1 500 SERVER ERROR " + explanation);

		out.println("Server: Java HTTP Server from Robin and Quentin : 1.0");

		out.println("Date: " + new Date());

		out.println("Content-type: " + contentMimeType);

		out.println("Content-length: " + fileLength);

		out.println(); // blank line between headers and content, very important !

		out.flush(); // flush character output stream buffer

		// file

		dataOut.write(fileData, 0, fileLength);

		dataOut.flush();
	}
public static void Post(BufferedReader in , String outputdir,PrintWriter out)throws Exception{   
	System.out.println("in Postfunction");
	String str="";  
	StringBuffer response = new StringBuffer();
	String st; 
	  while (((st = in.readLine()) != null)) {
		  if (st.equalsIgnoreCase("done")) {
			  System.out.println("found done");
              break;
          }
	    response.append(st); 
	    response.append(System.getProperty("line.separator"));
	  } 
	System.out.println("out of while");
	str=response.toString();  
	System.out.println("client says: "+str);  
	System.out.println("Saving in a file");
	FileWriter myWriter = new FileWriter( outputdir + "Post" );
	//myWriter.write("Files in Java might be tricky, but it is fun enough!");
	myWriter.write(str);
	myWriter.close();
	System.out.println("Successfully wrote to the file.");
	System.out.println("client said: "+str);  	
	out.println("Received your message");
	out.flush();
	System.out.println("Done with request of client");
	}
public static void Put(Socket s, BufferedReader in , String outputdir,PrintWriter out)throws Exception{ 
	System.out.println("in putfunction");
	String str="";  
	StringBuffer response = new StringBuffer();
	String st; 
	int k = 0;
	  while (((st = in.readLine()) != null)) {
		  if (st.equalsIgnoreCase("done")) {
			  System.out.println("found done");
              break;
          }
		 k += 1;
		 System.out.println(k);
		//String test = slice_end(st,4);
		//System.out.println(test);
	    response.append(st); 
	    response.append(System.getProperty("line.separator"));
	    System.out.println(st);
	    System.out.println(response);
	  } 
	System.out.println("out of while");
	SocketAddress id =s.getRemoteSocketAddress();
	String id2 = id.toString().substring(1,id.toString().length());
	System.out.println("socketid; " + id);
	System.out.println("socketid2; " + id2);
	str=response.toString();  
	System.out.println("client says: "+str);  
	System.out.println("Saving in a file");
	FileWriter myWriter = new FileWriter( outputdir + "Put" + id2 ); // + id
	//myWriter.write("Files in Java might be tricky, but it is fun enough!");
	myWriter.write(str);
	myWriter.close();
	System.out.println("Successfully wrote to the file.");
	System.out.println("client said: "+str);  	
	out.println("Received your message");
	out.flush();
	System.out.println("Done with request of client");
	}
public static String slice_end(String s, int endIndex) {
    if (endIndex < 0) endIndex = s.length() + endIndex;
    if (endIndex > s.length()) endIndex = s.length();
    return s.substring(0, endIndex);
}

	}
	

