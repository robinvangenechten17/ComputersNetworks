


	//Source :
	//https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application?fbclid=IwAR0G-fVyHqCruyhofnJmAQtQqjn7D6k8B2GTqS5n8Bn0dUW8ssw0jFtssig


	import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
	import java.net.HttpURLConnection;
	import java.net.URL;
	import java.net.URLEncoder;
import java.nio.charset.Charset;

	public class translator2 {
		 static String outputdir = "/Users/robin/eclipse-workspace/Computer Networks/src/";
		// static String page = "GET_www.google.be2.txt";
		 static String page = "GET_www.tcpipguide.com.txt";
	    @SuppressWarnings("resource")
		public static void main(String[] args) throws IOException {
	    	File file = new File("C:" + outputdir + page);
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer str = new StringBuffer();
			String output;
			String vertalingoutput;
			String taal1= "en";
			String taal2= "nl";
				 while ((output = br.readLine())!=null) {
						//  System.out.println(output);
					 try{ 
						 vertalingoutput=translate(taal1, taal2, output.toString());
						 str.append(vertalingoutput);
						 str.append(System.getProperty("line.separator"));
					 }
					 catch(Exception e) {
						 System.out.println(e);
						 System.out.println("vertaling niet gelukt");
						 str.append(output);
						 str.append(System.getProperty("line.separator"));
					 }
							  }
			String text =str.toString();	 
	       // String text = "Hello world! My name is Quentin.";
	    	//String text = "Hallo wereld! Ik ben Quentin."; 
			FileWriter myWriter = new FileWriter( outputdir + "VERTALER_" +"FROM"+taal1+"TO"+taal2 + page + ".HTML");
			myWriter.write(text);
			myWriter.close();
	        System.out.println("Translated text: " + text);
	    	//System.out.println("Translated text: " + translate("nl", "fr", text));
	    }

	    private static String translate(String langFrom, String langTo, String text) throws IOException {
	        // INSERT YOU URL HERE
	        String urlStr = "https://script.google.com/macros/s/AKfycbzQQmxNvc0FRtP0KxOyypAn70HLcOCW1mxlnYuytpuf6kb18oDW/exec" +
	                "?q=" + URLEncoder.encode(text, "UTF-8") +
	                "&target=" + langTo +
	                "&source=" + langFrom;
	        URL url = new URL(urlStr);
	        StringBuilder response = new StringBuilder();
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	      //  HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	       // InputStream inputStream = url.openStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(((HttpURLConnection) (new URL(urlStr)).openConnection()).getInputStream(), Charset.forName("UTF-8")));
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");
	       // BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        
	        return response.toString();
	    }

	}

