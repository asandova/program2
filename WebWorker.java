/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection. 
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring 
* the fact that the entirety of the webserver execution might be handling
* other clients, too. 
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format). 
*
**/

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;

public class WebWorker implements Runnable
{

String Code;

String FPath;
File HTMLFile;
/*
Byte[] HTML404 = { "<html><head></head><body>\n".getBytes() }

   		os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>404 Not Found</h3>".getBytes());
        	os.write("</body></html>\n".getBytes());*/

private Socket socket;

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP 
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
	String ContentType = "";
   byte[] HTMLByteArray = null;
   System.err.println("Handling connection...");
   Code = "200";
   try {
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();

	BufferedReader r = new BufferedReader(new InputStreamReader(is) );
	String line = r.readLine();
	//System.out.println(line);
	String[] L = line.split(" ");
	FPath = L[1];
	String ext = line.substring( line.lastIndexOf(".") + 1  );
	if( ext.equals("png") || ext.equals("gif") || ext.equals("png") || ext.equals("ico")){
		ContentType = "image/" + ext;
	}
	try{
		System.out.println("opening file " + FPath);
		FileInputStream F = new FileInputStream("." + FPath);
		//System.out.println("opening file");
		File BF = new File("." + FPath);
		System.out.println(BF.length());
		HTMLByteArray = new byte[ (int)BF.length() ];
 		System.out.println("entering while");
		while(true){
		try{
			while(!r.ready()){Thread.sleep(1);}
			//System.out.println(HTMLByteArray.length);
			//System.out.println((int)BF.length() );
			F.read( HTMLByteArray );
			F.close();		 
			}catch(Exception e){
				System.err.println("Output error: " + e);
				break;
			}
	System.out.println("exiting try block 1");
	}
	}catch(Exception e){
		Code = "404";
	}

      //HTML = readHTTPRequest(is);
      writeHTTPHeader(os,ContentType);
      writeContent(os, HTMLByteArray);
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("1Output error: "+e);
	Code = "404";
   }
   System.err.println("Done handling connection.");
   return;
}

/**
* Read the HTTP request header.
**/

private byte[] readHTTPRequest(InputStream is)throws Exception{
	System.out.println("in readHTTPRequest");
	//System.out.println("in try block 1");
	//BufferedReader r = new BufferedReader(new InputStreamReader(is));
	//String line = r.readLine();
	//String L[] = line.split(" ");
	//String FPath = L[1].substring(1);
	//String[] type = FPath.split(".");
	//if(type[1].equals("jpg") || type[1].equals("gif") || type[1].equals("png") ){
	//	ContentType = "image/"+type[1];
	//}
	//try{
	BufferedReader r = new BufferedReader(new InputStreamReader(is));
	FileInputStream F = new FileInputStream("." + FPath);
	File BF = new File("." + FPath);
	byte[] HTMLByteArray = new byte[ (int)BF.length() ];
 	System.out.println("entering while");
	while(true){
		try{
			while(!r.ready()){Thread.sleep(1);}
			F.read( HTMLByteArray );
			F.close();		 
			}catch(Exception e){
				System.err.println("Output error: " + e);
				break;
			}
	System.out.println("exiting try block 1");
	}
	//}catch(Exception e){
	//	Code = "404";		
	//}
	//byte[] nul = new byte[1];
	System.out.println(HTMLByteArray);
	return HTMLByteArray;
}//end of readHTTPRequest


/*i
private String readHTTPRequest(InputStream is)
{
   System.out.println("in readHTTPRequest");
   String line;
   String HTMLString = "";
   Date d = new Date();
   DateFormat DFormat = DateFormat.getDateTimeInstance();
   try{
   	BufferedReader r = new BufferedReader(new InputStreamReader(is));
   	line = r.readLine();
   	String L[] = line.split(" ");
   	String FPath = L[1].substring(1); 
   	//System.out.println( System.getProperty("user.dir") );
	System.out.println(FPath);
	BufferedReader F = new BufferedReader(new FileReader(FPath));    	
	while (true) {
		//System.out.println("in first while");//debug
      		try {
         		while (!r.ready()){ Thread.sleep(1);
				//System.out.println("in second while");//debug
				}
         			line = F.readLine();
         			//System.err.println("Request line: ("+line+")");
				if(line.equals("<cs371date>")){
					line = DFormat.format(d);
				}
				if(line.equals("<cs371server>")){
					line = "August's WebServer for CS371";
				}
				if(!line.equals(null) ){
					HTMLString += line;
				}
				if (line.length()==0) break;
     		 }catch (Exception e) {
       		  	System.err.println("Request error: "+e);
       		  	break;
      		}
   	}
	//System.out.println("exited first while");//debug
	
	Code = "200";
   }catch(Exception e){
	Code = "404";
	System.err.println("Error:" + e);
   }
   System.out.println("exiting");
   return HTMLString;
}//end of readHTTPRequest
*/

/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
{
   System.out.println("in writeHTTPheader");
System.out.println(contentType);
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
   os.write(("HTTP/1.1 " + Code).getBytes());
   if(Code == "200")
      os.write(" OK\n".getBytes());
   else
      os.write(" ERROR\n".getBytes());
   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: August's  CS371 P2\n".getBytes());
   //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
   //os.write("Content-Length: 438\n".getBytes()); 
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   System.out.println("exiting writeHTTPHeader");
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os, byte[] html) throws Exception
{
   System.out.println("in writecontent");
   //os.write("<html><head></head><body>\n".getBytes());
	//displays the html code from the requested file
	if(Code == "200"){
		os.write(html);
	}
	else{//This is the hard coded 404 message 
   		os.write("<html><head><link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\"></head><body>\n".getBytes());
		os.write("<h3>404 Not Found</h3>".getBytes());
		os.write("<h1><img src=\"./test/NOFUN.jpg\" alt=\"Something went wrong\"></h1>".getBytes());
        	os.write("</body></html>\n".getBytes());
	}

}

} // end class
