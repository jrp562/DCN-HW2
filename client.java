import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


// Server writes to arrival.log and filename specified by user
public class client {
    public static void main(String args[]) throws UnknownHostException, IOException {
        
        String set_host = args[0];
		String em_port = args[1];
        String set_port = args[2];
        String filename = args[3];
        
        int set_port_int = Integer.parseInt(set_port);
        int em_port_int = Integer.parseInt(em_port);
	
		// Read from text file
		BufferedReader br = new BufferedReader(new FileReader(filename));
    	String readtext = "";
    	String line = "";
    	
    	while ((line = br.readLine()) != null) {
            readtext += line + System.lineSeparator();
        }
    	br.close();
    	
    	// Initialize socket
    	InetAddress ip_addr = InetAddress.getByName(set_host);
    	DatagramSocket udp_sock = new DatagramSocket();
    		
	    packet new_pack = new packet(0, 0, readtext.length(), readtext);
	    
	    ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
		ObjectOutputStream obj = new ObjectOutputStream(out_byte);
		obj.writeObject(new_pack);
		byte[] out_byte_array = out_byte.toByteArray();
		
	    // Serialize packet object to byte
	    DatagramPacket out_pack = new DatagramPacket(out_byte_array, out_byte_array.length, ip_addr, em_port_int);
	    udp_sock.send(out_pack);
		/*	
		// Get feedback from Server
		byte[] in_buf = new byte[4];
	    DatagramPacket udp_in = new DatagramPacket(in_buf, in_buf.length);
		udp_sock.receive(udp_in);
		String in_data = new String(udp_in.getData());
		System.out.println(in_data);
		*/
    	udp_sock.close();
    }
}