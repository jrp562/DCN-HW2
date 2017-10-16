import java.io.File;
import java.io.FileWriter;
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
    public static void main(String args[]) throws IOException {
        
    	int rand_port, num;
        String output_file = "output.txt";
        String set_port = args[0];
        
        // Convert input argument to int
        int int_port = Integer.parseInt(set_port);
        
        // Set up server socket on specified port
        ServerSocket ssock = new ServerSocket(int_port);
        
        // Accept connections for server socket
        Socket serv = ssock.accept();
        Scanner scan = new Scanner(serv.getInputStream());
        num = scan.nextInt();
        
        // Generate random int for port numbers between 1024 and 65535
        rand_port = ThreadLocalRandom.current().nextInt(1024, 65535 + 1);
        String rand_port_string = Integer.toString(rand_port);
        String stage1 = "Negotiation detected. Please select the random port " + rand_port_string;
        System.out.println(stage1);
        
        // Send random port to client via OuputStream
        PrintStream p = new PrintStream(serv.getOutputStream());
        p.println(rand_port);
		
        // TCP sockets close
		ssock.close();
		serv.close();
		scan.close();
		
		// STAGE TWO: Begin UDP
		File out_file = new File(output_file);
		DatagramSocket udp_sock = new DatagramSocket(rand_port);
		byte[] in_buf = new byte[5];
		DatagramPacket udp_in = new DatagramPacket(in_buf, 5);
		udp_sock.receive(udp_in);
		
		// Create or set to overwrite file
		out_file.createNewFile();
		
		FileWriter file_write = new FileWriter(out_file.getAbsoluteFile());
		
		byte[] test2 = new byte[4];
		for(int i = 0; i < 4; i++){
			test2[i] = in_buf[i];
		}
		byte file_end = in_buf[4];
		
		// Write to file and set up input/output packets
		String in_data = new String(udp_in.getData());
		String out_data = in_data.toUpperCase();
		file_write.write(out_data);
		file_write.close();
		byte[] out_buf = out_data.getBytes(StandardCharsets.US_ASCII);
		InetAddress ip_in = udp_in.getAddress();
		DatagramPacket udp_out = new DatagramPacket(out_buf, 5, ip_in, udp_in.getPort());
		udp_sock.send(udp_out);
		
		// Repeat till end of file
		while(file_end != (byte)0x31){
			byte[] next_in_buf = new byte[5];
			DatagramPacket next_udp_in = new DatagramPacket(next_in_buf, 5);
			udp_sock.receive(next_udp_in);
			
			byte[] next_test2 = new byte[4];
			for(int i = 0; i < 4; i++){
				next_test2[i] = next_in_buf[i];
			}
			byte next_file_end = next_in_buf[4];
			
			String next_in_data = new String(next_udp_in.getData());
			String next_out_data = next_in_data.toUpperCase();
			
			byte[] next_out_buf = next_out_data.getBytes(StandardCharsets.US_ASCII);
			DatagramPacket next_udp_out = new DatagramPacket(next_out_buf, 5, ip_in, udp_in.getPort());
			udp_sock.send(next_udp_out);
			
			FileWriter next_file_write = new FileWriter(out_file.getAbsoluteFile(), true);
			next_file_write.write(next_out_data);
			next_file_write.close();
			if(next_file_end == 1){
				break;
			}
		}
		
		udp_sock.close();
    }
}