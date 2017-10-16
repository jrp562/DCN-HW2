//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class emulator {
    public emulator() {
    }

    public static void main(String[] args) {
        if (args.length != 8) {
            System.err.println("Invalid arguments for emulator. Exiting.");
            System.exit(-1);
        }

        int receiveFrom_port = Integer.parseInt(args[0]);
        int sendToClient_port = Integer.parseInt(args[1]);
        int sendToServer_port = Integer.parseInt(args[2]);
        String clientName = args[3];
        String serverName = args[4];
        int seed = Integer.parseInt(args[5]);
        double prob = Double.valueOf(args[6]).doubleValue();
        int vmode = Integer.parseInt(args[7]);
        if (prob < 0.0D || prob > 1.0D || seed < 0 || vmode < 0 || vmode > 1) {
            System.out.println("Invalid parameter value. Exiting.");
            System.exit(-1);
        }

        if (vmode == 1) {
            System.out.println();
            System.out.println("Input arguments are the following:");
            System.out.println("receivePort: " + receiveFrom_port);
            System.out.println("sendToClient-Port: " + sendToClient_port);
            System.out.println("sendToServer-Port: " + sendToServer_port);
            System.out.println("clientName: " + clientName);
            System.out.println("serverName: " + serverName);
            System.out.println("seed: " + seed);
            System.out.println("dropProb: " + prob);
            System.out.println("verboseFlag: " + vmode + "\n");
        }

        byte[] recBuf = new byte[1024];
        byte[] sendBuf = new byte[1024];
        DatagramSocket csSocket = null;
        InetAddress address = null;
        DataOutputStream outstream = null;
        DataInputStream instream = null;
        Random rnd = new Random((long)seed);
        boolean dropIt = false;
        boolean var18 = false;

        try {
            csSocket = new DatagramSocket(receiveFrom_port);
            boolean done = false;

            while(true) {
                packet pkt;
                ByteArrayOutputStream oSt;
                ObjectOutputStream ooSt;
                DatagramSocket newSocket;
                DatagramPacket npacket;
                do {
                    while(true) {
                        do {
                            if (done) {
                                return;
                            }

                            System.out.println("-----------------------------------------------------------\n\n");
                            dropIt = false;
                            recBuf = new byte[1024];
                            DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
                            csSocket.receive(recpacket);
                            ByteArrayInputStream inSt = new ByteArrayInputStream(recBuf);
                            ObjectInputStream oinSt = new ObjectInputStream(inSt);
                            pkt = (packet)oinSt.readObject();
                            if (vmode == 1) {
                                System.out.println("Emulator RECEIVED a packet w/contents:");
                                pkt.printContents();
                            }

                            double doWeDrop = rnd.nextDouble();
                            System.out.println("Random value: " + doWeDrop + " and drop probability set to: " + prob);
                            if (doWeDrop <= prob && (pkt.getType() == 0 || pkt.getType() == 1)) {
                                dropIt = true;
                                if (vmode == 1) {
                                    System.out.println("Emulator DROPPED packet w/contents:");
                                    pkt.printContents();
                                }
                            }
                        } while(dropIt);

                        if (pkt.getType() != 1 && pkt.getType() != 3) {
                            break;
                        }

                        oSt = new ByteArrayOutputStream();
                        ooSt = new ObjectOutputStream(oSt);
                        ooSt.writeObject(pkt);
                        ooSt.flush();
                        sendBuf = oSt.toByteArray();
                        address = InetAddress.getByName(serverName);
                        newSocket = new DatagramSocket();
                        npacket = new DatagramPacket(sendBuf, sendBuf.length, address, sendToServer_port);
                        newSocket.send(npacket);
                        if (vmode == 1) {
                            System.out.println("Emulator SENT packet to " + serverName + " w/contents: ");
                            pkt.printContents();
                        }
                    }
                } while(pkt.getType() != 0 && pkt.getType() != 2);

                oSt = new ByteArrayOutputStream();
                ooSt = new ObjectOutputStream(oSt);
                ooSt.writeObject(pkt);
                ooSt.flush();
                sendBuf = oSt.toByteArray();
                address = InetAddress.getByName(clientName);
                newSocket = new DatagramSocket();
                npacket = new DatagramPacket(sendBuf, sendBuf.length, address, sendToClient_port);
                newSocket.send(npacket);
                if (vmode == 1) {
                    System.out.println("Emulator SENT packet to " + clientName + " w/contents: ");
                    pkt.printContents();
                }
            }
        } catch (UnknownHostException var30) {
            System.out.println("Unknown host.");
        } catch (IOException var31) {
            System.out.println("I/O error");
        } catch (Exception var32) {
            var32.printStackTrace();
        }

    }
}
