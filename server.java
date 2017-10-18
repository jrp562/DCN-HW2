import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class server {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        
    	String set_host = args[0];
        String serv_port = args[1];
		String em_port = args[2];
        String output_file = args[3];

        int serv_port_int = Integer.parseInt(serv_port);
		int em_port_int = Integer.parseInt(em_port);
		
		File out_file = new File(output_file);
		File arrival_log = new File("arrival.log");
		
		
		DatagramSocket udp_sock = new DatagramSocket(serv_port_int);
		byte[] in_buf = new byte[1024];
		
		
		// Create or set to overwrite file
		out_file.createNewFile();
		arrival_log.createNewFile();
		
		FileWriter file_write_output = new FileWriter(out_file.getAbsoluteFile());
		FileWriter arrival_write = new FileWriter(arrival_log.getAbsoluteFile());
		
		file_write_output.write("");
		arrival_write.write("");
		
		file_write_output.close();
		arrival_write.close();
		
		while(true){
			DatagramPacket udp_in = new DatagramPacket(in_buf, in_buf.length);
			udp_sock.receive(udp_in);
			
			// Deserialize
			ByteArrayInputStream b = new ByteArrayInputStream(udp_in.getData());
			ObjectInputStream obj = new ObjectInputStream(b);
			packet in_packet = (packet) obj.readObject();
			
			InetAddress ip_in = udp_in.getAddress();
			
			
			FileWriter file_write = new FileWriter(out_file.getAbsoluteFile(), true);
			FileWriter arrival_log_write = new FileWriter(arrival_log.getAbsoluteFile(), true);
			
			int in_seq = in_packet.getSeqNum();
			arrival_log_write.write(in_seq);
			arrival_log_write.close();
			
			String in_data = new String(in_packet.getData());
			file_write.write(in_data);
			file_write.close();
			
			if(in_packet.getType() == 3){
				
				packet ack_pack = new packet(2, in_seq, 0, null);
				ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
				ObjectOutputStream obj_out = new ObjectOutputStream(out_byte);
				obj_out.writeObject(ack_pack);
				byte[] out_byte_array = out_byte.toByteArray();
				
			    // Serialize packet object to byte
			    DatagramPacket out_pack = new DatagramPacket(out_byte_array, out_byte_array.length, ip_in, em_port_int);
			    udp_sock.send(out_pack);
			    
				break;
			}

			packet ack_pack = new packet(0, in_seq, 0, null);
			
			ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
			ObjectOutputStream obj_out = new ObjectOutputStream(out_byte);
			obj_out.writeObject(ack_pack);
			byte[] out_byte_array = out_byte.toByteArray();
			
		    // Serialize packet object to byte
		    DatagramPacket out_pack = new DatagramPacket(out_byte_array, out_byte_array.length, ip_in, em_port_int);
		    udp_sock.send(out_pack);
			
			
						
		}
		
		udp_sock.close();
    }
}