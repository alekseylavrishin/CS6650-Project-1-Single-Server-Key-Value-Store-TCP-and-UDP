import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server {

    /**
     * Used to communicate with TCP client
     * @param serverIP The IP or hostname the server will be hosted on.
     * @param port The port the server will listen on.
     */
    public static void TCPServer(String serverIP, int port) {

        try {
            while(true) { // Server listens until ctrl-c is pressed or exception occurs
                // Translate String IP or hostname to InetAddress type
                InetAddress ip = InetAddress.getByName(serverIP);

                ServerSocket listenSocket = new ServerSocket(port, 50, ip);
                System.out.println("Server listening on IP " + ip + " port " + port);

                // Look for and accept single incoming connection
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connection accepted on port " + port);

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                try {
                    // Add functionality for operations here

                } catch (Exception e) {
                    System.out.println("Error handling client request: " + e.getMessage());
                } finally {
                    clientSocket.close();
                    System.out.println("Client connection closed");
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void UDPServer(String serverIP, int port) {
        DatagramSocket s = null;
        try {
            // Create new socket at provided port
            s = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            while(true) { // Server listens until ctrl-c is pressed or exception occurs
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                s.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(),
                        request.getAddress(), request.getPort());
                s.send(reply);
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Asks the user for the communication type they wish to use with the server.
     * User must enter '1' for TCP or '2' for UDP.
     * If provided input is not '1' or '2', function will rerun until appropriate input is given.
     * @param scanner The Scanner used for taking user input from System.in.
     */
    public static void askForCommType(Scanner scanner, String serverIP, int port) {
        System.out.println("Enter '1' to use TCP or enter '2' to use UDP");
        int selection = scanner.nextInt();

        if(selection == 1) {
            //TCPServer(serverIP, port);
            System.out.println("TCP");
        } else if(selection == 2) {
            //UDPServer(serverIp, port);
            System.out.println("UDP");
        } else { // Rerun if input doesn't match '1' or '2'
            System.out.println("Invalid Input");
            askForCommType(scanner, serverIP, port);
        }
    }


    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Proper input format must be 'java Server.java <server_ip> <port>'");
            return;
        }

        String serverIP = null;
        int port = -1;

        try {
            serverIP = args[0];
            port = Integer.parseInt(args[1]);

        } catch (Exception e) {
            System.out.println("<server_ip> must be type String and <port> must be type int");
        }

        // Create scanner for selecting TCP or UDP
        Scanner scanner = new Scanner(System.in);
        askForCommType(scanner, serverIP, port);

    }
}
