import java.io.*;
import java.net.*;
import java.util.*;
  
// Client class
class Client {
    private String serverAddress = "localhost";
    private int serverPort = 3333;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner sc;

    Client(String serverAddress,int serverPort)
    {
        setServerAddress(serverAddress);
        setServerPort(serverPort);
    }
    public void runTCP()
    {
        try {
            // establish a connection by providing host and port
            // number
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true); // writing to server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // reading from server
            sc = new Scanner(System.in);
            String line = null;
            while (!"exit".equalsIgnoreCase(line)) {

                // reading from user
                line = sc.nextLine();

                // sending the user input to server
                out.println(line);
                out.flush();

                // displaying server reply
                System.out.println("Server replied " + in.readLine());
            }
            closeEverything();

        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void closeEverything()
    {
        try {
            // closing the scanner object
            sc.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setServerAddress(String serverAddress)
    {
        this.serverAddress = serverAddress;
    }

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }
    public static void main(String[] args)
    {
        Client client = new Client("localhost",3333);
        client.runTCP();
    }
}