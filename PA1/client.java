import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class client {
    public static void main(String args[]) throws UnknownHostException, IOException {
        
    	// STAGE ONE: Begin TCP
    	int new_port, num;
        String set_host = args[0];
        String set_port = args[1];
        String filename = args[2];
        
        int int_port = Integer.parseInt(set_port);
        
        Scanner scan = new Scanner(System.in);
        Socket sock = new Socket(set_host, int_port);
        Scanner scan2 = new Scanner(sock.getInputStream());
        num = 259;
        PrintStream p = new PrintStream(sock.getOutputStream());
        
        p.println(num);
        
        new_port = scan2.nextInt();
		
		sock.close();
		scan.close();
		scan2.close();
		p.close();
		
		// STAGE TWO: Begin UDP
		// Read from text file
		BufferedReader br = new BufferedReader(new FileReader(filename));
    	String readtext = "";
    	String line = "";
    	
    	while ((line = br.readLine()) != null) {
            readtext += line + System.lineSeparator();
        }
    	br.close();
    	byte[] read_chars = readtext.getBytes(StandardCharsets.US_ASCII);
    	
    	// Initialize socket
    	InetAddress ip_addr = InetAddress.getByName(set_host);
    	DatagramSocket udp_sock = new DatagramSocket();
    	
    	int div_four = read_chars.length / 4;
    	int remain = read_chars.length % 4;
		
    	// Work by four
    	for (int i = 0; i <= div_four; i++){
			byte[] eod = "0".getBytes(StandardCharsets.US_ASCII);
			byte[] four_char_byte = new byte[5];
			if(i == div_four){
				eod = "1".getBytes(StandardCharsets.US_ASCII);
				byte[] filler = new byte[1];
				filler[0] = (byte)0x00;
				four_char_byte[0] = filler[0];
				four_char_byte[1] = filler[0];
				four_char_byte[2] = filler[0];
				four_char_byte[3] = filler[0];
				four_char_byte[4] = eod[0];
			
	    		DatagramPacket out_pack = new DatagramPacket(four_char_byte, 5, ip_addr, new_port);
	    		udp_sock.send(out_pack);
    		}
    		else{
				four_char_byte[0] = read_chars[4*i];
	    		four_char_byte[1] = read_chars[4*i+1];
	    		four_char_byte[2] = read_chars[4*i+2];
	    		four_char_byte[3] = read_chars[4*i+3];
				if(remain == 0 & ((i+1) == div_four)){
					eod = "1".getBytes(StandardCharsets.US_ASCII);
					i++;
				}
	    		four_char_byte[4] = eod[0];
	    		
	    		DatagramPacket out_pack = new DatagramPacket(four_char_byte, 5, ip_addr, new_port);
	    		udp_sock.send(out_pack);
    		}
			
			// Get feedback from Server
			byte[] in_buf = new byte[4];
	    	DatagramPacket udp_in = new DatagramPacket(in_buf, in_buf.length);
			udp_sock.receive(udp_in);
			String in_data = new String(udp_in.getData());
			System.out.println(in_data);
    	}
    		
    	udp_sock.close();
    }
}