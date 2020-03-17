//package Practicum;

import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;


// Started from https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java 15/03/2020

// Each Client Connection will be managed in a dedicated Thread

public class JavaHTTPServer implements Runnable{ 

	static final File WEB_ROOT = new File("C:\\Users\\robin\\eclipse-workspace\\Computer Networks\\src2");

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
	String outputdir = "/Users/robin/eclipse-workspace/Computer Networks/src2/";

	public static void main(String[] args) {

		try {

			ServerSocket serverConnect = new ServerSocket(PORT);

			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

			// we listen until user halts server execution

			while (true) {

				JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());
				
				
				
				if (verbose) {
					System.out.println("==========================================================================");
					System.out.println("Connection opened. (" + new Date() + ")");

				}

				// create dedicated thread to manage the client connection

				Thread thread = new Thread(myServer);

				System.out.println ("A connection was started on the server, Thread nbr " +thread.getId() + " is started to serve this client");
				
				thread.start();

			}

		} catch (IOException e) {
			
			System.err.println("Server Connection 500 error : " + e.getMessage());

		}

	}

	@Override

	public void run() {

		// we manage our particular client connection, for as long as the connection is open

		while (!(connect.isClosed())) {
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;

		
		Properties headerProp = new Properties() ; // for each new try of lecture, we make new properties
		headerProp.setProperty("headerReceived","false");
		try {

			// we read characters from the client via input stream on the socket

			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));

			// we get character output stream to client (for headers)

			out = new PrintWriter(connect.getOutputStream());

			// get binary output stream to client (for requested data)

			dataOut = new BufferedOutputStream(connect.getOutputStream());

			// get first line of the request from the client
			String headerLine;  
			
			boolean firstline = true;
			while ((headerLine = in.readLine())!=null) {
				
				headerProp.setProperty("headerReceived","true");
				if (firstline) {
					System.out.println("");
					System.out.println("--- START OF HEADER ---");
					System.out.println(headerLine);
					String[] statusline=headerLine.split(" ",3);
					headerProp.setProperty("method",statusline[0]);
					headerProp.setProperty("fileRequested",statusline[1]);
					headerProp.setProperty("Httpversion",statusline[2]);
					firstline = false;
				} else {
					if (headerLine.isEmpty()) {
						//headerProp.list(System.out);
						System.out.println("--- END OF HEADER ---");
						System.out.println("");
						break;
					} else // reading rest of header line by line
					{
						System.out.println(headerLine);
						headerProp.load((new StringReader(headerLine)));
					}
				}
			}
			
			if (headerProp.getProperty("headerReceived")=="true") {
			
			
			
				// we support only GET and HEAD methods, we check
				//while ( (input= in.readLine())!=null) {System.out.println(input);} // FUTURE DEV: threat rest of header, thrown away now
				if (!headerProp.getProperty("method").equals("GET")  &&  !headerProp.getProperty("method").equals("HEAD") && !headerProp.getProperty("method").equals("POST") && !headerProp.getProperty("method").equals("PUT") ) {
	
					if (verbose) {
	
						System.out.println("501 Not Implemented : " + headerProp.getProperty("method") + " method.");
						
					}
					try {
	
						MethodNotSupported(out, dataOut, headerProp.getProperty("fileRequested"));
	
					} catch (IOException ioe) {
						try {
							ServerError(out, dataOut, "");
						} catch (IOException e) {
							System.err.println("Server error : " + ioe);
						}
						System.err.println("Error with MethodNotSupported : " + ioe);
	
					}
				} else {
					
					if (headerProp.getProperty("Httpversion").equals("HTTP/1.1")){  
						
						// GET or HEAD method
		
						if ( headerProp.getProperty("fileRequested").endsWith("/")) {
		
							headerProp.setProperty("fileRequested",  headerProp.getProperty("fileRequested") + DEFAULT_FILE);
							
		
						}
						if (headerProp.getProperty("method").equals("GET")  ||  headerProp.getProperty("method").equals("HEAD")) {
						
							File file = new File(WEB_ROOT,headerProp.getProperty("fileRequested"));
		
						int fileLength = (int) file.length();
		
						String content = getContentType(headerProp.getProperty("fileRequested"));
		
						if (headerProp.getProperty("method").equals("GET")) { // GET method so we return content
		
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
		
							System.out.println("File " + headerProp.getProperty("fileRequested") + " of type " + content + " returned");
		
						}
						}
						else if (headerProp.getProperty("method").equals("PUT")) {
							try {
								Put(connect,in,outputdir,out);
							} catch (Exception e) {
								System.out.println(e);
								ServerError(out, dataOut, "PUT");
							}
						}
						else if (headerProp.getProperty("method").equals("POST")) {
							try {
								Post(in,outputdir,out);
							} catch (Exception e) {
								System.out.println(e);
								ServerError(out, dataOut,"POST");
							}
						}
					}else {
						try {
	
							BadRequest(out, dataOut, headerProp.getProperty("fileRequested"));
	
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
			}
		} 
		catch (FileNotFoundException fnfe) {

			try {

				fileNotFound(out, dataOut, headerProp.getProperty("fileRequested"));

				} catch (IOException ioe) {
					try {
						ServerError(out, dataOut,"");
					} catch (IOException e) {
						System.err.println("Server error : " + ioe);
					}
					System.err.println("Error with file not found exception : " + ioe.getMessage());

				}
			} 
		catch (IOException ioe) {
			try {
				ServerError(out, dataOut,"");
				} 
				catch (IOException e) {
					System.err.println("Server error : " + e);
				}
			System.err.println("Server error : " + ioe);
			
			} 
		
		//catch (Exception e) {
			//	System.err.println(" Server error : " + e);
		//}
		
		finally {

			try {

				//in.close();
				
				out.flush();
				//out.close();
				dataOut.flush();
				//dataOut.close();
			

				//connect.close(); // we close socket connection
				
			} catch (Exception e) {

				System.err.println("Error flushing stream : " + e.getMessage());

			} 
			
		}
		if (verbose) {
			if (connect.isClosed()) {
			System.out.println("Connection closed.\n");
			}
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
	str=response.toString();  
	System.out.println("client says: "+str);  
	System.out.println("Saving in a file");
	FileWriter myWriter = new FileWriter( outputdir + "Post" );
	//myWriter.write("Files in Java might be tricky, but it is fun enough!");
	myWriter.write(str);
	myWriter.close();
	System.out.println("Successfully wrote to the file.");
	System.out.println("client said: "+str);  	
	// send HTTP Headers
	
		out.println("HTTP/1.1 200 OK");

		out.println("Server: Java HTTP Server from Quentin  and Robin : 1.0");

		out.println("Date: " + new Date());
		
		out.println(); // blank line between headers and content, very important !
		
	out.println("Received your message"); // Agreement to end Post/Put request
	out.flush();
	System.out.println("Done with request of client");
	}
public static void Put(Socket s, BufferedReader in , String outputdir,PrintWriter out)throws Exception{ 
	System.out.println("in putfunction");
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
	SocketAddress id =s.getRemoteSocketAddress();
	String id2 = id.toString().substring(1,id.toString().length()).replaceFirst(":", ".");
	System.out.println("socketid; " + id);
	System.out.println("socketid2; " + id2);
	str=response.toString();  
	System.out.println("client says: "+str);  
	System.out.println("Saving in a file");
	FileWriter myWriter = new FileWriter( outputdir + "Put" + id2); // + id
	myWriter.write(str);
	myWriter.close();
	System.out.println("Successfully wrote to the file.");
	System.out.println("client said: "+str);  
	// send HTTP Headers
	
	out.println("HTTP/1.1 200 OK");

	out.println("Server: Java HTTP Server from Quentin  and Robin : 1.0");

	out.println("Date: " + new Date());
	
	out.println(); // blank line between headers and content, very important !
	
	out.println("Received your message"); // Agreement to end Post/Put request
	out.flush();
	System.out.println("Done with request of client");
	}
public static String slice_end(String s, int endIndex) {
    if (endIndex < 0) endIndex = s.length() + endIndex;
    if (endIndex > s.length()) endIndex = s.length();
    return s.substring(0, endIndex);
}

	}
	

