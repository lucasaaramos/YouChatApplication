/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.youchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Lucas Albuquerque Student Number: 2020343
 */
public class YouChatClient {

    //Declaring the Atributes of the class 
    private String name;
    private String surname;
    private String address;
    private int port;
    private String username;

    public static void main(String[] args) {
        //start the client on the port 1099. This run the method run()
        YouChatClient client = new YouChatClient("localhost", 1099);
        client.start();
    }

    
    public YouChatClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start() {
        
        /**
         * Everything that the user see on screen is declared here.
         * 
         * Created a responsive design to guide the user on what to do.
         * 
         * ask the user to inform name and surname, store this in the attribute username and 
         * use this here and in the server class
         * 
         */
        
        try {
            Socket socket = new Socket(address, port);
            System.out.println("Connected to YouChat server on port number " + port);

            System.out.println("\n>>> Welcome to YouChat <<<");
            System.out.println("Please enter your details to access the system:\n");

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            name = scanner.nextLine();

            System.out.print("Enter your surname: ");
            surname = scanner.nextLine();

            username = name + " " + surname;

            outputStream.writeObject(username);
            outputStream.flush();

            //Apply concurrency using threads. the start will look for the method run() and run what is in there
            Thread messageThread = new Thread(new MessageClientThread(inputStream));
            messageThread.start();

            System.out.println("\nWelcome to YouChat, " + username + "!");

            //System.out.println("Would you like to start a new chat? (Y/N)");
            //String response = scanner.nextLine();
            boolean newChat = true;

            while (newChat) {
                System.out.println("\nWould you like to start a new chat? (Y/N)");
                String response = scanner.nextLine();

                /**
                 * Menu option that allow the user to start a chat or exit;
                 * If the user type quit, there is a second security step to confirm if the user want to exit.
                 * While the user don't exit, the client will be running.
                */
                if (response.equalsIgnoreCase("Y")) {
                    System.out.println("\nType your message below or type 'quit' to exit.");
                    while (true) {
                        String message = scanner.nextLine();
                        outputStream.writeObject(message);
                        outputStream.flush();

                        if (message.equalsIgnoreCase("quit")) {

                            System.out.println("\nAre you sure you want to exit? (Y/N)");
                            String exitOption = scanner.nextLine();

                            if (exitOption.equalsIgnoreCase("Y")) {
                                System.out.println("\nThanks for using out service, " + username + "!");
                                break;
                            } else {
                                System.out.println("\nType your message below or type 'quit' to exit.");
                                //continue;

                            }

                        }
                    }

                    newChat = false;

                } else if (response.equalsIgnoreCase("N")) {
                    //EXIT
                    System.out.println("\nThanks for using out service, " + username + "!");
                    break;
                } else {
                    //in case the user type something different than Y or N
                    System.out.println("\n***Not a valid input!***");
                    newChat = true;
                }

            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that implements concurrency with method run().
     * This method will run when we have start in the main method
     */
    private class MessageClientThread implements Runnable {

        private ObjectInputStream inputStream;

        public MessageClientThread(ObjectInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = (String) inputStream.readObject();
                    System.out.println(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
