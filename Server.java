import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;
  
// Server class
class Server {

    static ExecutorService exec = null;
    private String userName;
    public Server()
    {
        exec = Executors.newFixedThreadPool(10);
        exec.submit(() -> this.startTCP());
        exec.submit(() -> this.startUDP());
    }

    public void startTCP() {
        ServerSocket server = null;
  
        try {
  
            // server is listening on port 1234
            server = new ServerSocket(3333);
            server.setReuseAddress(true);
  
            // running infinite loop for getting
            // client request
            while (true) {
  
                // socket object to receive incoming client
                // requests
                Socket client = server.accept();
  
                // Displaying that new client is connected
                // to server
                System.out.println("New client connected at: "
                                   + client.getInetAddress()
                                         .getHostAddress());
  
                // create a new thread object
                ClientHandlerTCP clientSock
                    = new ClientHandlerTCP(client,userName);
  
                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void startUDP() {
        new Thread(new Runnable() {
            private Random random;
            private int id;
            
			@Override
			public void run() {
                this.random = new Random();
                this.id = random.nextInt();

				try (DatagramSocket socket = new DatagramSocket(3333)) {
					byte[] buf = new byte[socket.getReceiveBufferSize()];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);

					System.out.println("Listening on UDP port 3333");
                    socket.receive(packet);
                    userName = new String(packet.getData(),0, packet.getLength());
                    System.out.println(userName+" has join the server!");
					while (true) {
						socket.receive(packet);
                        System.out.println(userName + ": " + new String(packet.getData(),0, packet.getLength()));
                        // QOTD Server
                        InetAddress clientAddress = packet.getAddress();
                        int clientPort = packet.getPort();

                        byte[] buffer = ("Hello"+userName+" ".getBytes()).getBytes();
                        DatagramPacket response = new DatagramPacket(buffer, 0, buffer.length, clientAddress, clientPort);
                        
						socket.send(response);
					}
				} catch (IOException ioe) {
					System.err.println("Cannot open the port on UDP");
					ioe.printStackTrace();
				} finally {
					System.out.println("Closing UDP server");
				}
			}
		}).start();
    }
    // ClientHandler class
    static class ClientHandlerTCP implements Runnable {
        private Random random;
        private int id;
        private final Socket clientSocket;
        private String userName = "anonymous";
  
        // Constructor
        public ClientHandlerTCP(Socket socket,String userName)
        {
            this.random = new Random();
            this.id = random.nextInt();
            this.clientSocket = socket;
            this.userName = userName;
        }
  
        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                    
                  // get the outputstream of client
                out = new PrintWriter(clientSocket.getOutputStream(), true);
  
                  // get the inputstream of client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  
                String line;
                while ((line = in.readLine()) != null) {
  
                    // writing the received message from
                    // client
                    System.out.printf(" Sent from client " + userName + ": %s\n", line);
                    out.println(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientHandlerUDP implements Runnable {
        private Random random;
        private int id;
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivePacket;
    
        // Constructor
        public ClientHandlerUDP(DatagramSocket socket, DatagramPacket packet) {
            this.random = new Random();
            this.id = random.nextInt();
            this.serverSocket = socket;
            this.receivePacket = packet;
        }
    
        public void run() {
            try {
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
    
                byte[] receiveData = receivePacket.getData();
                System.out.println("recive dataaa---"+receiveData.toString());
                int length = receivePacket.getLength();
    
                // Convert received data to a string
                String receivedMessage = new String(receiveData, 0,length);
                
                // Process the received message
                System.out.printf("Received from client %d: %s%n", this.id, receivedMessage);
    
                // Send a response back to the client
                String responseMessage = receivedMessage.toUpperCase(); // Example: Convert to uppercase
                byte[] responseData = responseMessage.getBytes();
    
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                serverSocket.send(responsePacket);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serverSocket.close();
            }
        }
    }
}