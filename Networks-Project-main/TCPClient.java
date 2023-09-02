import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String equation;
        String calculatedEquation;

        // input from client terminal
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        /*
         * client contacts server by:
         * Creating TCP socket, specifying IP address, port number of server process
        */


        Socket clientSocket = new Socket("127.0.0.1", 6789);

        // for output to server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // for input from server
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        
        // enable client to request as many equations needed 
        while (true) {

            System.out.println(inFromServer.readLine());

            // get the equation to send to server
            equation = inFromUser.readLine();

            // disconnect client on "Q" or "q"
            if (equation.compareTo("Q") == 0 || equation.compareTo("q") == 0) {
                break;
            }

            // write to server
            outToServer.writeBytes(equation + '\n');

            // read from the server
            calculatedEquation = inFromServer.readLine();

            System.out.println("Answer: " + calculatedEquation + "\n");

        }

        clientSocket.close();
        System.exit(0);
    }

}