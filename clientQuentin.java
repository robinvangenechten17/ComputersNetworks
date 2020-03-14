package Practicum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
//import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Parser;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Client {
	
	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		
		if(args.length < 2 || args.length > 4){
	        System.err.println("At least 2 max 4 arguments.");// port and language are not compulsory
	        System.exit(1);
		}
		
		String command = args[0];
		URI uri = new URI(args[1]);
		//uri = new URI("http://www.google.com"); //debugging purposes
		uri = new URI("www.google.com/"); //debugging purposes
		//uri = new URI("https://learn.onemonth.com/understanding-http-basics/"); //debugging purposes
		System.out.println("Given URI : " + uri);
		String hostName = "";
		String pathURI = "";
		if ( uri.getScheme()== "http" |  uri.getScheme()== "https" ) {
			hostName = uri.getHost();
			pathURI = uri.getPath();
			if (pathURI== "") {
				pathURI = "/";
			}
			//System.out.println(hostName);
			//System.out.println(relativeLocation);
		} else {
			hostName = getDomain(uri);
			pathURI = getLocation(uri);
		}

		System.out.println("hostName : " + hostName);
		System.out.println("pathURI : " + pathURI);
		
		String port = "80";
		if (args.length > 2) {
            if (isInteger(args[2])) {
                port = args[2];
            }
            else {
            	System.err.println("Port should be an int.");
    	        System.exit(1);
            }
		}
		
		String language = args[3]; //NOT USED YET
		
		String fileName = hostName;
		if (hostName.equals("localhost")) { //JSP ENCORE
			fileName = pathURI;
		}
		
		try (
				//this.clientSocket = new Socket(this.host, Integer.parseInt(this.port)); // open a socket with the host
				//this.outToServer = new DataOutputStream(clientSocket.getOutputStream()); // create output stream object
		        //this.bytesFromServer = new BufferedInputStream(clientSocket.getInputStream()); // create input stream object

			    Socket socket = new Socket(hostName, Integer.parseInt(port)); //open a socket with the hostName
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);			
				InputStream inputServer = socket.getInputStream();
			    BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in)); 
				FileWriter myWriter = new FileWriter("output/" + command+ "/" + fileName + ".txt");
				)

		{		
				socket.setSoTimeout(5000);
				out.println(command + " "+ pathURI + " " + "HTTP/1.1" + "\n");
				//out.println("Host: " + hostName + "\n");
				out.println("\n");
				out.flush();
				System.out.println("sent to server: ");
				System.out.print(command + " "+ pathURI + " " + "HTTP/1.1" + "\n");
				//System.out.print("Host: " + hostName + "\n");
				System.out.print("\n");
				
				/*
				 * Invoke different actions based on the given command. 	
				 */
				
				if (command.equals("GET") || command.equals("HEAD")) {
					
					/*
					 * Print the headers to the console and parse the contentLength.
					 */
					String line;
					int contentLength = 0;
					System.out.println("Header : ");
					System.out.println();
					while (!(line = getLineFromBytes(inputServer)).isEmpty()) {
						System.out.println(line);
						myWriter.write(line);
						myWriter.write("\n");
						if (line.contains("Content-Length: ")) {
							contentLength = Integer.parseInt(line.substring(line.indexOf(':') + 2));
						}
					}
					System.out.println();
					System.out.println("Body : ");
					System.out.println();
					while ((line = getLineFromBytes(inputServer))!= "0\r\n") {
						System.out.println(line);
						myWriter.write(line);
						myWriter.write("\n");
					}
					System.out.println("helloooooooooooooooooo");
					System.out.println("helloooooooooooooooooo");
					System.out.println("helloooooooooooooooooo");
					System.out.println("helloooooooooooooooooo");
					/*
					 * If the GET-command is invoked, retrieve webpage as html file,
					 * parse the html file as a string and retrieve its images.
					 */
					if (command == "GET") {
						String html = writeBytesFromContent(myWriter, contentLength, inputServer);
						getImages(hostName, out, inputServer, html);
					}
				}
				
				else if (command.equals("PUT")) {
					System.out.println("Enter the data to store at " + args[1] + ":");
					String userInput;
				    userInput = keyboardInput.readLine();
				    out.println(userInput + "\r");
				    Thread.sleep(2000);
				    
				} else if (command.equals("POST")) {
					System.out.println("Enter the data to send at " + args[1] + ":");
					String userInput;
				    userInput = keyboardInput.readLine();
				    out.println(userInput + "\r");
				    Thread.sleep(2000);
				    
				}
				else {
					throw new IOException("Only support GET, HEAD, PUT and POST services");
				}
						    
			} catch (UnknownHostException e) { 
			    System.err.println("UnkownHostException: " + e.getMessage());
			} catch (IOException e) {
				System.err.println("IOException: " + e.getMessage());
			}
	
	}
	
	
	
	/**
	 * Returns the domain of the given url. If uri.getdomain is used it will return everyting with the "/"
	 * at the end. We dont wan't the "/" at the end. Even if it's given. If no / is given then
	 * @param uri URI
	 * @return	domain (string)
	 */
	public static String getDomain(URI uri) {
	    String domain = uri.getPath();
	    int index = domain.indexOf("/");
	    if (index > 0) {
	    	//String domainName = domain.substring(0, Math.min(domain.length(), index)); //jsp pq min des deux
	    	String domainName = domain.substring(0, index);
	    	return domainName;
	    }
	    return domain;
	}
	
	
	/**
	 * Returns the path after the domain in the uri
	 * @param uri URI
	 * @return Path as string
	 */
	public static String getLocation(URI uri){
		String location = uri.getPath();
	    int index = location.indexOf("/");
	    if (index > 0) {
	    	String relLocation = location.substring(index);
	    	return relLocation;
	    }
	    return location;
	}
	
	
	
	/**
	 * Writes text composed of contentLength bytes 
	 * from an InputStream to a File using a Writer.
	 * Each string is composed of maximum contentLength bytes.
	 * Returns the file as one string.
	 * 
	 * @param 	writer
	 * 			The Writer writing strings on a File.
	 * @param 	contentLength
	 * 			The amount of bytes to be written.
	 * @param 	byteStream
	 * 			The InputStream 
	 * @return	The written html-file as a string.
	 * @throws 	IOException
	 * 			Signals that an I/O exception of some sort has occurred.
	 */
	private static String writeBytesFromContent(Writer writer, int contentLength, InputStream byteStream) throws IOException {
		int counter = 0, chunk;
		byte[] byteArray  = new byte[2048];
		StringBuilder stringBuilder = new StringBuilder();
		while ((counter < contentLength) && ((chunk = byteStream.read(byteArray, 0, Math.min(byteArray.length, contentLength - counter))) != -1)) {
			stringBuilder.append(new String(byteArray, "UTF-8"));
			String string = new String(Arrays.copyOfRange(byteArray, 0, chunk));
			System.out.println(string);
			writer.write(string);
			counter += chunk;
		}
		return stringBuilder.toString();
	}
	
	/**
	 * Checks if the object is an integer
	 * @param object Object to be checked
	 * @return Boolean
	 */
	public static boolean isInteger(Object object) {
		if(object instanceof Integer) {
			return true;
		} else {
			String string = object.toString();
			
			try {
				Integer.parseInt(string);
			} catch(Exception e) {
				return false;
			}	
		}
	  
	    return true;
	}
	
	/**
	 * Retrieves all url's containing images from a given html file as a String.
	 * Then passes the url to a function retrieving its content.
	 *
	 * @param 	html
	 * 			The html file to extract the url's from.
	 */
	private static void getImages(String hostName, PrintWriter out, InputStream byteStream, String html) throws NumberFormatException, IOException, URISyntaxException {
		String url = "";
		System.out.println("bug");
		Document doc = Parser.parse(html, "");
        //Document doc = Jsoup.parse(html);
		System.out.println("bug");
        Elements images = doc.select("img");
    	for (Element image : images) {
            url = image.attr("src");
            convertUrlToImage(hostName, url, out, byteStream);
    	}
	}
	
	/**
	 * Retrieves content from the given url as a String by writing commands 
	 * to a PrintWriter, using a given InputStream. Also prepares directories, 
	 * a File and a FileOutputStream for writing the content in a File.
	 * Then passes the necessary information to a function writing the content to the File.
	 * 
	 * @param 	hostName
	 * 			The domain hosting the requested File.
	 * @param 	url
	 * 			The relative path to the file as a String.
	 * @param 	out
	 * 			The PrintWriter to write the commands to.
	 * @param 	byteStream
	 * 			The InputStream the invoked server responds to.
	 * 			
	 * @throws 	NumberFormatException
	 * 			Indicates that the application has attempted to convert 
	 * 			a string to one of the numeric types, but that the string 
	 * 			does not have the appropriate format.
	 * @throws 	IOException
	 * 			Signals that an I/O exception of some sort has occurred. 
	 * @throws 	URISyntaxException
	 * 			Indicates that a string could not be parsed as a URI reference.
	 */
	private static void convertUrlToImage(String hostName, String url, PrintWriter out, InputStream byteStream) throws URISyntaxException, NumberFormatException, IOException {
		String response;
		int contentLength = 0;
		
		out.println("GET /" + url + " HTTP/1.1\r");
		out.println("Host: " + hostName + "\r");
		out.println("\r");
		out.flush();
		
		while (!(response = getLineFromBytes(byteStream)).isEmpty()) {
			if (response.contains("Content-Length: ")) {
				contentLength = Integer.parseInt(response.substring(response.indexOf(':') + 2));
			}
		}
		
		Path urlFiltered = getPath(url);
		File dirs = new File("CNOutput/" + filterDirsPath(urlFiltered));
		dirs.mkdirs();
		
		String fileName = "CNOutput/" + urlFiltered;
		File file = new File(fileName);
		file.createNewFile();
		FileOutputStream fileOut = new FileOutputStream("CNOutput/" + urlFiltered);
		writeBytesFromContentFile(fileOut, contentLength, byteStream);
	}
	
	/**
	 * Returns a substring of the given url composed 
	 * of only the directories in the url, 
	 * excluding the file in the final directory.
	 * 
	 * @param 	url
	 * 			The url as a Path to decode.
	 * @return	The directories in the url as one string.
	 */
	public static String filterDirsPath(Path url) {
		String copy = url.toString();
		int index = 0;
		if (copy.contains("/")) {
			for (index = copy.length()-1; index >= 0 ; index--) {
				if (copy.charAt(index) == '/') {
					break;
				}
			}
		return new String(copy.substring(0, index+1));
		} else {
			return null;
		}
	}
	
	/**
	 * Writes contentLength bytes from an InputStream to a File using a 
	 * FileOutputStream, in chunks of maximum 2048 bytes.
	 * 
	 * @param 	dataOut
	 * 			The FileOutputStream towards the File to be written on.
	 * @param 	contentLength
	 * 			The amount of bytes to be written.
	 * @param 	byteStream
	 * 			The InputStream 
	 * @throws 	IOException
	 * 			Signals that an I/O exception of some sort has occurred.
	 */
	private static void writeBytesFromContentFile(FileOutputStream dataOut, int contentLength,
			InputStream byteStream) throws IOException {
		int counter = 0, chunk;
		byte[] byteArray  = new byte[2048];
		while ((counter < contentLength) && ((chunk = byteStream.read(byteArray, 0, Math.min(byteArray.length, contentLength - counter))) != -1)) {
			dataOut.write(byteArray, 0, chunk);
			counter += chunk;
		}
	}
	
	/**
	 * Decodes a string of a path, to have the html file find all its sources.
	 * 
	 * @param 	path
	 * 			A string containing the path to decode.
	 * @return	Returns the decoded scheme-specific part of this URI as a string, as a Path object.
	 * @throws 	URISyntaxException
	 * 			Indicates that a string could not be parsed as a URI reference. 
	 */
	public static Path getPath(String path) throws URISyntaxException {
        return Paths.get(new URI(path).getSchemeSpecificPart());
    }
	
	/**
	 * Returns a string of decoded bytes received from an 
	 * InputStream, using the UTF-8 charSet. '\r' and '\n' are 
	 * not included in the bytes, '\n' denotes the end of the 
	 * bytes to decode.
	 * 
	 * @param 	byteStream
	 * 			The InputStream providing bytes.
	 * @return	Null when the InputStream has no bytes available, the decoded string otherwise.
	 * @throws 	IOException
	 * 			Signals that an I/O exception of some sort has occurred. 
	 */
	public static String getLineFromBytes(InputStream byteStream) throws IOException {
		int c; 
		List<Byte> byteList= new ArrayList<>();
		do
		{
		   c = byteStream.read(); 
		   if (c == '\n') {
			   	byte[] array = new byte[byteList.size()];
			   	for(int n = 0; n < byteList.size(); n++)
			   	{
			   	     array[n] = byteList.get(n); 
			   	}
			   	return new String(array, "UTF-8");
		   }
		   if (c != '\r') {
			   byteList.add((byte) c);
		   }
		}
		while(c != -1);
		return null;
	}
	
	

}
