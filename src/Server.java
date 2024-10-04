import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server {

    /**
     * Used to communicate with TCPClient to perform PUT, GET, DELETE operations over TCP protocol.
     * @param serverIP The IP Address or hostname the server will be hosted on.
     * @param port The port the server will listen on.
     * @param hMap The HashMap used to store and perform operations on Key, Value pairs specified by the TCPClient.
     */
    public static void TCPServer(String serverIP, int port, HashMap<String, String> hMap) {

        try {
            // Translate String IP or hostname to InetAddress type
            InetAddress ip = InetAddress.getByName(serverIP);

            ServerSocket listenSocket = new ServerSocket(port, 50, ip);
            System.out.println("Server listening on IP " + ip + " port " + port);

            while(true) { // Server listens until ctrl-c is pressed or exception occurs
                // Look for and accept single incoming connection
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connection accepted on port " + port);

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                try {
                    // listen for type of operation: PUT, GET, DELETE
                    String operation = in.readUTF();

                    if(operation.equals("PUT")){
                        // confirm to server that PUT operation is commencing
                        out.writeUTF("Server initializing PUT operation");

                        // Get key from client
                        String key = in.readUTF();
                        out.writeUTF("Key " + key + " received by server");

                        // Get value from client
                        String value = in.readUTF();
                        out.writeUTF("Value " + value + " received by server");

                        // Write key, value to hMap
                        hMap.put(key, value);
                        out.writeUTF(key + value + " have been written to the server");

                    } else if(operation.equals("GET")) {
                        // confirm to server that GET operation is commencing
                        out.writeUTF("Server initializing GET operation");

                        String key = in.readUTF();
                        out.writeUTF("Key " + key + " received by server");

                        if(hMap.containsKey(key)) {
                            // Return value to client
                            String value = hMap.get(key);
                            out.writeUTF(value);

                        } else { // If key cannot be found in hMap
                            // Return 'cannot be found' message to client
                            out.writeUTF("Key " + key + " cannot be found");

                        }

                    } else if(operation.equals("DELETE")) {
                        // confirm to server that DELETE operation is commencing
                        out.writeUTF("Server initializing DELETE operation");

                        String key = in.readUTF();
                        out.writeUTF("Key " + key + " received by server");

                        if(hMap.containsKey(key)) {
                            // If key exists, delete key from hMap
                            hMap.remove(key);
                            out.writeUTF("Key " + key + " deleted from server");
                        } else {
                            // If key is not found in server
                            out.writeUTF("Key " + key +  " cannot be found in server");
                        }

                    } else {
                        // Faulty operation provided, send back error message
                        // TODO: figure out error message here
                        out.writeUTF("Faulty operation detected");
                    }

                } catch (Exception e) {
                    // TODO: logging
                    System.out.println("Error handling client request: " + e.getMessage());
                } finally {
                    clientSocket.close();
                    System.out.println("Client connection closed");
                }
            }
        } catch (UnknownHostException e) {
            // TODO: logging
            throw new RuntimeException(e);
        } catch (IOException e) {
            // TODO: logging
            //throw new RuntimeException(e);
            System.out.println("RuntimeException " + e.getMessage());
        }

    }

    /**
     * Used to communicate with UDPClient to perform PUT, GET, DELETE operations over UDP protocol.
     * @param serverIP The IP Address or hostname the server will be hosted on.
     * @param port The port the server will listen on.
     * @param hMap The HashMap used to store and perform operations on Key, Value pairs specified by the UDPClient.
     */
    public static void UDPServer(String serverIP, int port, HashMap<String, String> hMap) {
        DatagramSocket s = null;
        try {
            String val;
            byte[] byteResponse;

            // Translate String IP or hostname to InetAddress type
            InetAddress ip = InetAddress.getByName(serverIP);

            // Create new socket at provided port and InetAddress
            s = new DatagramSocket(port, ip);

            byte[] buffer = new byte[1024];
            while(true) { // Server listens until ctrl-c is pressed or exception occurs

                // Get type of incoming request from client
                DatagramPacket typePacket = new DatagramPacket(buffer, buffer.length);
                s.receive(typePacket); // Get type of request
                String typeMsg = new String(typePacket.getData(), 0, typePacket.getLength());
                System.out.println("1 " + typeMsg);

                // Get key from client
                DatagramPacket keyPacket = new DatagramPacket(buffer, buffer.length);
                s.receive(keyPacket); // Get type of request
                String keyMsg = new String(keyPacket.getData(), 0, keyPacket.getLength());
                System.out.println("2" + keyMsg);


                if(typeMsg.equals("PUT")) {
                    // If operation is PUT, retrieve value
                    DatagramPacket valPacket = new DatagramPacket(buffer, buffer.length);
                    s.receive(valPacket); // Get type of request
                    String valMsg = new String(valPacket.getData(), 0, valPacket.getLength());
                    System.out.println("3 " + valMsg);

                    hMap.put(keyMsg, valMsg);

                    byteResponse = ("Entry for " + keyMsg + " successfully created").getBytes();
                    DatagramPacket response = new DatagramPacket(byteResponse, byteResponse.length,
                            typePacket.getAddress(), typePacket.getPort());
                    s.send(response);

                } else if (typeMsg.equals("GET")) {
                    if(hMap.containsKey(keyMsg)) { // if key exists, return value
                        val = hMap.get(keyMsg);
                    } else { // Else return 'cannot be found' message
                        val = ("Key " + keyMsg + " cannot be found in server");
                    }
                    byteResponse = val.getBytes();
                    DatagramPacket response = new DatagramPacket(byteResponse, byteResponse.length,
                            typePacket.getAddress(), typePacket.getPort());
                    s.send(response);

                } else if(typeMsg.equals("DELETE")) {
                    if(hMap.containsKey(keyMsg)) { // if key exists, return value
                        hMap.remove(keyMsg);
                        val = "Key " + keyMsg + " deleted from server";

                    } else { // Else return 'cannot be found' message
                        val = ("Key " + keyMsg + " cannot be found in server");

                    }
                    byteResponse = val.getBytes();
                    DatagramPacket response = new DatagramPacket(byteResponse, byteResponse.length,
                            typePacket.getAddress(), typePacket.getPort());
                    s.send(response);

                } else {
                    // Faulty operation provided
                    // TODO: send error message
                    byteResponse = "Faulty operation detected".getBytes();
                    DatagramPacket response = new DatagramPacket(byteResponse, byteResponse.length,
                            typePacket.getAddress(), typePacket.getPort());
                    s.send(response);
                }
            }

        } catch (SocketException e) {
            // TODO: logging
            throw new RuntimeException(e);
        } catch (IOException e) {
            // TODO: logging
            throw new RuntimeException(e);
        }
    }

    /**
     * Asks the user for the communication type they wish to use with the server.
     * User must enter '1' for TCP or '2' for UDP.
     * If provided input is not '1' or '2', function will rerun until appropriate input is given.
     * @param scanner The Scanner used for taking user input from System.in.
     */
    public static void askForCommType(Scanner scanner, String serverIP, int port, HashMap<String,String> hMap) {
        try {
            System.out.println("Enter '1' to use TCP or enter '2' to use UDP");
            int selection = scanner.nextInt();

            if (selection == 1) {
                System.out.println("TCP Communication Selected");
                TCPServer(serverIP, port, hMap);

            } else if (selection == 2) {
                System.out.println("UDP Communication Selected");
                UDPServer(serverIP, port, hMap);

            } else { // Rerun if input doesn't match '1' or '2'
                System.out.println("Invalid Input");
                askForCommType(scanner, serverIP, port, hMap);
            }
        } catch (InputMismatchException e) {
            System.out.println("Input mismatch detected: exiting");
        }
    }


    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("Proper input format must be 'java Server.java <server_ip> <port>'");
            return;
        }

        String serverIP = null;
        int port = -1;

        // Stores all keys, values provided by client
        HashMap<String, String> hMap = new HashMap<>();

        try {
            serverIP = args[0];
            port = Integer.parseInt(args[1]);

        } catch (Exception e) {
            System.out.println("<server_ip> must be type String and <port> must be type int");
        }

        // Create scanner for selecting TCP or UDP
        Scanner scanner = new Scanner(System.in);
        askForCommType(scanner, serverIP, port, hMap);

    }
}
