import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    /**
     * Performs communication with the server over TCP.
     * @param serverIP The IP Address or hostname of the server.
     * @param port The port number the server is listening on.
     * @throws Exception Throws exception if the server connection is interrupted or unsuccessful.
     */
    public static void TCPClient(String serverIP, int port) throws Exception {
        Socket s = null;

        try {
            // Connect to server
            s = new Socket(serverIP, port);

            // Input and Output streams for server communication
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            Scanner scanner = new Scanner(System.in);

            // Choose type of operation
            System.out.println("Enter '1' to perform PUT");
            System.out.println("Enter '2' to perform GET");
            System.out.println("Enter '3' to perform DELETE");

            int selection = scanner.nextInt();
            scanner.nextLine(); // deal with \n left by scanner.nextInt()


            if(selection == 1) {
                System.out.println("PUT operation selected");
                System.out.print("Enter key to PUT: ");
                String key = scanner.nextLine();
                System.out.print("Enter value to PUT: ");
                String value = scanner.nextLine();

                TCPput(key, value, in, out);

            } else if(selection == 2) {

                System.out.println("GET operation selected");

                System.out.print("Enter key to GET: ");
                String key = scanner.nextLine();

                TCPget(key, in, out);

            } else if(selection == 3) {
                System.out.println("DELETE operation selected");
                System.out.print("Enter key to DELETE: ");
                String key = scanner.nextLine();

                TCPdelete(key, in, out);

            } else { // Rerun if input doesn't match '1', '2', or '3'
                System.out.println("Invalid Input");
                TCPClient(serverIP, port);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        finally {
            if(s != null && !s.isClosed()) {
                s.close();
            }
        }
    }

    /**
     * Handles the client-side execution of a TCP DELETE operation.
     * If object exists in server's HashMap, server deletes object associated with the key and returns acknowledgement.
     * If object does not exist, server returns a 'cannot be found' message.
     * @param key The Key of the object to be deleted on the server.
     * @param in The datastream used to receive messages from the server.
     * @param out The datastream used to send messages to the server.
     * @throws IOException
     */
    public static void TCPdelete(String key, DataInputStream in, DataOutputStream out) throws IOException {
        // Tell server a DELETE operation is commencing
        out.writeUTF("DELETE");
        String data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Send key and get response from server
        out.writeUTF(key);
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Receive confirmation of deletion
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);
    }

    /**
     * Handles the client-side execution of a TCP GET operation.
     * If object exists in server's HashMap, server returns the value associated with the key.
     * If object does not exist, server returns a 'cannot be found' message.
     * @param key The Key of the object to be retrieved from the server.
     * @param in The datastream used to receive messages from the server.
     * @param out The datastream used to send messages to the server.
     * @throws IOException
     */
    public static void TCPget(String key, DataInputStream in, DataOutputStream out) throws IOException {
        // Tell server a GET operation is commencing, then print confirmation response from server
        out.writeUTF("GET");
        String data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Get value from server
        out.writeUTF(key);
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        data = in.readUTF();
        System.out.println("RESPONSE: " + data);
    }

    /**
     * Handles the client-side execution of a TCP PUT operation.
     * @param key The Key of the object to be saved in the server's HashMap.
     * @param value The Value associated with the Key to be saved in the server's HashMap.
     * @param in The datastream used to receive messages from the server.
     * @param out The datastream used to send messages to the server.
     * @throws IOException
     */
    public static void TCPput(String key, String value, DataInputStream in, DataOutputStream out) throws IOException {

        // Tell server a PUT operation is commencing, then receive confirmation response from server
        out.writeUTF("PUT");
        String data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Then write key to server and receive confirmation that key is received
        out.writeUTF(key);
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Write value to server and receive confirmation that value is received
        out.writeUTF(value);
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);

        // Receive confirmation that key, value pair has been created
        data = in.readUTF();
        System.out.println("RESPONSE: " + data);

    }

    /**
     * Performs communication with the server over UDP.
     * @param serverIP The IP Address or hostname of the server.
     * @param port The port number the server is listening on.
     */
    public static void UDPClient(String serverIP, int port){
        DatagramSocket s = null;

        try {
            s = new DatagramSocket();
            s.setSoTimeout(10000); // Set timeout to 10 seconds

            // Determines IP Address of the server given a hostname or IP
            InetAddress host = InetAddress.getByName(serverIP);

            Scanner scanner = new Scanner(System.in);

            // Choose type of operation
            System.out.println("Enter '1' to perform PUT");
            System.out.println("Enter '2' to perform GET");
            System.out.println("Enter '3' to perform DELETE");

            int selection = scanner.nextInt();
            scanner.nextLine(); // deal with \n left by scanner.nextInt()

            if(selection == 1) { // PUT operation
                System.out.println("PUT operation selected");
                System.out.print("Enter key to PUT: ");
                String key = scanner.nextLine();
                System.out.print("Enter value to PUT: ");
                String value = scanner.nextLine();

                UDPOperation(key, value, "PUT", host, port, s);

            } else if(selection == 2) { // GET operation
                System.out.println("GET operation selected");
                System.out.print("Enter key to GET: ");
                String key = scanner.nextLine();

                UDPOperation(key, "", "GET", host, port, s);

            } else if(selection == 3) { // DELETE operation
                System.out.println("DELETE operation selected");
                System.out.print("Enter key to DELETE: ");
                String key = scanner.nextLine();

                UDPOperation(key, "", "DELETE", host, port, s);

            } else { // Rerun if input doesn't match '1', '2', or '3'
                System.out.println("Invalid Input");
                UDPClient(serverIP, port);

            }

        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if(s != null) {
                s.close();
            }
        }
    }

    /**
     * Handles client-side PUT, GET, DELETE operations for UDP communication.
     * @param key The Key of the object to perform an operation on.
     * @param value The Value of the object to perform an operation on.
     * @param type The type of operation to be performed - PUT, GET, DELETE.
     * @param host The InetAddress corresponding to the server.
     * @param port The port the server is listening on.
     * @param s The DatagramSocket used to communicate with the server.
     * @throws IOException
     */
    public static void UDPOperation(String key, String value, String type, InetAddress host, int port, DatagramSocket s) throws IOException {
        byte[] byteKey = key.getBytes();
        byte[] byteVal = value.getBytes();
        byte[] byteType = type.getBytes(); // Type of request

        // Inform server of incoming request type
        DatagramPacket typeRequest = new DatagramPacket(byteType, byteType.length, host, port);
        s.send(typeRequest); // Send request to server

        // Package Key into Datagram packet and send to server
        DatagramPacket keyRequest = new DatagramPacket(byteKey, byteKey.length, host, port);
        s.send(keyRequest);

        if(type.equals("PUT")){
            // Package Value into Datagram packet and send to server
            DatagramPacket valRequest = new DatagramPacket(byteVal, byteVal.length, host, port);
            s.send(valRequest);
        }

        // Receive response from server and print
        byte[] buffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        s.receive(response);
        String responseMsg = new String(response.getData(), 0, response.getLength());
        System.out.println("Reply: " + responseMsg);

        s.close(); // Close socket

    }

    /**
     * Asks the user for the communication type they wish to use with the server.
     * User must enter '1' for TCP or '2' for UDP.
     * If provided input is not '1' or '2', function will rerun until appropriate input is given.
     * @param scanner The Scanner used for taking user input from System.in.
     */
    public static void askForCommType(Scanner scanner, String serverIP, int port) throws Exception {
        try {
            System.out.println("Enter '1' to use TCP or enter '2' to use UDP");
            int selection = scanner.nextInt();

            if (selection == 1) {
                System.out.println("TCP Communication Selected");
                TCPClient(serverIP, port);

            } else if (selection == 2) {
                System.out.println("UDP Communication Selected");
                UDPClient(serverIP, port);

            } else { // Rerun if input doesn't match '1' or '2'
                System.out.println("Invalid Input");
                askForCommType(scanner, serverIP, port);
            }
        } catch (InputMismatchException e) {
            System.out.println("Input mismatch detected: exiting");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2){ // Check that 2 args are provided
            System.out.println("Proper input format must be 'java Client.java <server_ip> <port>'");
            return;
        }

        String serverIP = null;
        int port = -1;

        // Ensure provided args are correct types
        try{
            serverIP = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("<server_ip> must be type String and <port> must be type int");
        }

        // Create scanner for accepting user input
        Scanner scanner = new Scanner(System.in);
        askForCommType(scanner, serverIP, port);

    }
}
