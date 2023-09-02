import java.net.*;
import java.util.logging.*;
import java.io.*;
import java.sql.Timestamp;



class TCPServer {
    public static void main(String args[]) throws Exception {

        // sets the format of the logger to suppress repetitive datetime statements
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
        
        // create logger object (to be passed to client handler)
        Logger logger = Logger.getLogger(TCPServer.class.getName());

        // creates FileHandler object for writing logs to a text file
        FileHandler fh; 


        try {  

            // this block configures the logger with handler and formatter  
            fh = new FileHandler("serverLog.txt");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  

        } catch (IOException e1) {  
            e1.printStackTrace();  
        }  

        // establish handshake socket
        ServerSocket welcomeSocket = new ServerSocket(6789);

        // loop for processing clients
        while (true) {

            // accept client's connection socket from welcome socket
            Socket connectionSocket = welcomeSocket.accept();
            
            Timestamp connectionTime = new Timestamp(System.currentTimeMillis());

            // get the client's IP address
            String clientIP = connectionSocket.getLocalSocketAddress().toString();

            // log that the client is connected to the server
            logger.info("Client " + clientIP + " on port " + connectionSocket.getPort() + " connected at time " + connectionTime + "\n");

            TCPClientHandler clientSocket = new TCPClientHandler(connectionSocket, logger, clientIP, connectionTime);

            new Thread(clientSocket).start();

        }
    }

    // ClientHandler class
    private static class TCPClientHandler implements Runnable {
        private final Socket clientSocket;
        private Logger logger;
        private String clientIP;

        private String equation;
        private String formattedEquation;

        private double result = 0;

        private Timestamp startTime;

        // Constructor
        public TCPClientHandler(Socket socket, Logger logger, String clientIP, Timestamp startTime) {
            this.clientSocket = socket;
            this.logger = logger;
            this.clientIP = clientIP;
            this.startTime = startTime;
        }

        public void run() {
            
            // input and output things
            BufferedReader inFromClient;
            try {
                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                // loop for processing equations from a client
                while (true) {
                    outToClient.writeBytes("Enter an equation or press 'q' to quit: \n");

                    // get the equation from the client
                    equation = inFromClient.readLine();

                    try {
                        // get rid of spaces
                        formattedEquation = equation.replace(" ", "");

                        // regex matches for number then operator or operator then number and then
                        // splits on the operator
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
                    try {
                        outToClient.writeBytes(String.valueOf(result) + "\n");

                        // log that calculation was performed by client
                        logger.info("Calculation performed for client " + clientIP + " on port " + clientSocket.getPort() + "\n");

                    } catch (Exception e) {
                        // else log that client is disconnected and break out of while loop
                        Timestamp endTime = new Timestamp(System.currentTimeMillis());
                        long duration = (endTime.getTime() - startTime.getTime()) / 1000;
                        logger.info("Client " + clientIP + " on port " + clientSocket.getPort() + " disconnected at time " + endTime);
                        logger.info("Client " + clientIP + " on port " + clientSocket.getPort() + " total service time: " + duration + " seconds\n");
                        break;
                    }

                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

}