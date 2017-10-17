import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.net.InetAddress;

public class server {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        
    	int rand_port, num;
    	String set_host = args[0];
        String serv_port = args[1];
		String em_port = args[2];
        String output_file = args[3];

        int serv_port_int = Integer.parseInt(serv_port);
		int em_port_int = Integer.parseInt(em_port);
		
		File out_file = new File(output_file);
		DatagramSocket udp_sock = new DatagramSocket(serv_port_int);
		byte[] in_buf = new byte[1024];
		DatagramPacket udp_in = new DatagramPacket(in_buf, in_buf.length);
		udp_sock.receive(udp_in);
		
		ByteArrayInputStream b = new ByteArrayInputStream(udp_in.getData());
		ObjectInputStream obj = new ObjectInputStream(b);
		packet in_packet = (packet) obj.readObject();
		
		
		// Create or set to overwrite file
		out_file.createNewFile();
		
		FileWriter file_write_output = new FileWriter(out_file.getAbsoluteFile());
		
		/*
		byte[] test2 = new byte[4];
		for(int i = 0; i < 4; i++){
			test2[i] = in_buf[i];
		}
		byte file_end = in_buf[4];
		*/
		
		// Write to file and set up input/output packets
		String in_data = new String(in_packet.getData());
		
		file_write_output.write(in_data);
		file_write_output.close();
		
		/*
		 * 
		byte[] out_buf = in_data.getBytes(StandardCharsets.US_ASCII);
		InetAddress ip_in = udp_in.getAddress();
		DatagramPacket udp_out = new DatagramPacket(out_buf, 5, ip_in, udp_in.getPort());
		udp_sock.send(udp_out);
		
		// Repeat till end of file
		
		byte[] next_in_buf = new byte[1024];
		DatagramPacket next_udp_in = new DatagramPacket(next_in_buf, 5);
		udp_sock.receive(next_udp_in);
			
		String next_in_data = new String(next_udp_in.getData());
		String next_out_data = next_in_data.toUpperCase();
			
		byte[] next_out_buf = next_out_data.getBytes(StandardCharsets.US_ASCII);
		DatagramPacket next_udp_out = new DatagramPacket(next_out_buf, 5, ip_in, udp_in.getPort());
		udp_sock.send(next_udp_out);
			
		FileWriter next_file_write = new FileWriter(out_file.getAbsoluteFile(), true);
		next_file_write.write(next_out_data);
		next_file_write.close();
		*
		*/	
		udp_sock.close();
    }
}