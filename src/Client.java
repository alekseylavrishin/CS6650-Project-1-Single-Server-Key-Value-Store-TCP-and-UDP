import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
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



    public static void TCPdelete(String key, DataInputStream in, DataOutputStream out) throws IOException {
        // Tell server a DELETE operation is commencing
        out.writeUTF("DELETE");
        String data = in.readUTF();
        System.out.print("Response from the server: " + data);

        // Write key and get response from server
        out.writeUTF(key);
        data = in.readUTF();
        System.out.print("Response from the server: " + data);
    }

    public static void TCPget(String key, DataInputStream in, DataOutputStream out) throws IOException {
        // Tell server a GET operation is commencing, then print confirmation response from server
        out.writeUTF("GET");
        String data = in.readUTF();
        System.out.print("Response from the server: " + data);

        // Get value from server
        out.writeUTF(key);
        data = in.readUTF();
        System.out.print("Response from the server: " + data);
    }

    public static void TCPput(String key, String value, DataInputStream in, DataOutputStream out) throws IOException {

        // Tell server a PUT operation is commencing, then receive confirmation response from server
        out.writeUTF("PUT");
        String data = in.readUTF();
        System.out.print("Response from the server: " + data);

        // Then write key to server and receive confirmation response
        out.writeUTF(key);
        data = in.readUTF();
        System.out.print("Response from the server: " + data);

        // Lastly, write value to server and receive confirmation back
        out.writeUTF(value);
        data = in.readUTF();
        System.out.print("Response from the server: " + data);



    }

    /**
     * Performs communication with the server over UDP.
     * @param serverIP The IP Address or hostname of the server.
     * @param port The port number the server is listening on.
     */
    public static void UDPClient(String serverIP, int port){
        DatagramSocket s = null;
        String testString = "Test string";
        try {
            s = new DatagramSocket();

            // Encode string into bytes for transmission
            byte[] byteStr = testString.getBytes();

            // Determines IP Address of the server given a hostname or IP
            InetAddress host = InetAddress.getByName(serverIP);

            // Package into DatagramPacket and send to server
            DatagramPacket request = new DatagramPacket(byteStr, byteStr.length, host, port);
            s.send(request);

            byte[] buffer = new byte[1000]; // Buffer used to hold incoming datagram
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length); // Receive response

            // Test connection to server by sending message and receiving a reply
            s.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(s != null) {
                s.close();
            }
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
