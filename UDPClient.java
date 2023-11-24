import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {

        private String serverAddress = "localhost";
        private int serverPort = 3116;
        private String userName = "anonymous";
        private DatagramSocket clientSocket;
        private Scanner scanner;
        private InetAddress serverIPAddress;
        private DatagramPacket sendPacket;
    private byte[] sendData;
    public UDPClient(String serverAddress,int portNumber)
    {
        setServerAddress(serverAddress);
        setServerPort(portNumber);
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void runUDP() {
            try {
                // Create a UDP socket
                clientSocket = new DatagramSocket();
                // Create a scanner to read user input
                scanner = new Scanner(System.in);
                System.out.println("what is your name for creating a connection?");
                String user = scanner.nextLine();
                setUserName(user);
                // This part is for sending username to the server
                sendData = user.getBytes();
                // Create a datagram packet(USERNAME) with the server's address and port
                serverIPAddress = InetAddress.getByName(serverAddress);
                sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverPort);

                // Send the packet(USERNAME) to the server
                clientSocket.send(sendPacket);

                while (true) {
                    // Read a message from the user
                    System.out.print("Enter a message (or 'quit' to exit): ");
                    String message = scanner.nextLine();

                    if (message.equalsIgnoreCase("quit")) {
                        break; // Exit the loop if the user enters "quit"
                    }

                    // Prepare the message to send
                    sendData = message.getBytes();

                    // Create a datagram packet with the server's address and port
                    serverIPAddress = InetAddress.getByName(serverAddress);
                    sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverPort);

                    // Send the packet to the server
                    clientSocket.send(sendPacket);

                    // Receive the response from the server
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);

                    // Process the response
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Response from server: " + response);
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

            public void close()
        {
            // Close the scanner and the socket
            scanner.close();
            clientSocket.close();
        }


    public static void main(String[] args)
    {
        UDPClient udpClient = new UDPClient("localhost",3333);
        udpClient.runUDP();

    }
}
