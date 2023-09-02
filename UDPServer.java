import java.net.*;
import java.util.logging.*;
import java.io.*;

class UDPServer {
    public static void main(String args[]) throws Exception {
        Logger logger = Logger.getLogger(UDPServer.class.getName());
        String equation;
        String formattedEquation;

        double result = 0;

        // establish handshake socket
        ServerSocket welcomeSocket = new ServerSocket(6789);

        // loop for processing clients
        while (true) {

            // accept client's connection socket from welcome socket
            Socket connectionSocket = welcomeSocket.accept();

            // get the client's IP address
            String clientIP = connectionSocket.getLocalSocketAddress().toString();

            // log that the client is connected to the server
            logger.info("Client " + clientIP + " connected.\n");

            // input and output things
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // loop for processing equations from a client
            while (true) {
                // get the equation from the client
                equation = inFromClient.readLine();

                try {
                    // get rid of spaces
                    formattedEquation = equation.replace(" ", "");

                    // regex matches for number then operator or operator then number and then splits on the operator
                    String[] arguments = formattedEquation.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");

                    // get the operands and operator from the equation
                    double number1 = Double.parseDouble(arguments[0]);
                    String operator = arguments[1];
                    double number2 = Double.parseDouble(arguments[2]);

                    // perform the calculation depending on the operator given by the client
                    switch (operator) {
                        case "+":
                            result = number1 + number2;
                            break;
                        case "-":
                            result = number1 - number2;
                            break;
                        case "/":
                            result = number1 / number2;
                            break;
                        case "*":
                            result = number1 * number2;
                            break;
                    }
                } catch (Exception e) {
                    Thread.currentThread().getStackTrace();
                }

                
                // attempts to write out result to client if connection is still established
                try{
                    outToClient.writeBytes(String.valueOf(result) + "\n");

                    // log that calculation was performed by client
                    logger.info("Calculation performed for client " + clientIP);

                } catch(Exception e){
                    // else log that client is disconnected and break out of while loop
                    logger.info("Client " + clientIP + " disconnected.");
                    break;
                }
                
            }
        }
    }
}