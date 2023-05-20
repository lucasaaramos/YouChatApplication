/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.youchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lucas Albuquerque Student Number: 2020343
 */
public class YouChatServer {

    private Map<String, ObjectOutputStream> client;
    //private int clientCount;

    public static void main(String[] args) {

        //Declaring the port number. Used port 1099 but could be other. we only can't have 2 applications running on 
        //the same port at the same time. If this happen, java will finish the program and inform that the port is in use.
        YouChatServer server = new YouChatServer();
        server.start(1099);
    }

    public YouChatServer() {
        client = new HashMap<>();
        //clientCount = 0;
    }

    public void start(int port) {

        //class that receives the port number declared in the main method and try-catch that will start the server on the port given 
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("YouChat server has started on port number " + port);

            System.out.println("\nYou can start clients now running the YouChatClient class!");

            /**
             * This loop limit the quantity of users in 2 user, then it is
             * possible to have only clients at the time. Otherwise would be
             * possible to create many clients and it would be a group chat. As
             * the assignment requires only individual chats.
             *
             * If the the user tries to run the 3rd client, the server will stop
             * for this new client and it will not work, but the server will be
             * still running for the 2 clients on chat.
             *
             * In order to create other clients, it is necessary close any
             * client using the option "quit"
             *
             * When e client quit, the system allow again maximum 2 clients for
             * a chat.
             *
             * All the chats with other user remains registered on the screen.
             */
            //while (clientCount < 3) {
            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected: " + clientSocket);

                //Using thread to work with client using concurrency 
                Thread clientThread = new Thread(new YouChatServerThread(clientSocket));
                clientThread.start();
                //clientCount++;

                //If the user try to crate the 3rd client, the clientSocher closed and the following message is output
                //The user is not able to crate the 3rd client
//                if (clientCount == 3) {
//                    System.out.println("Connection refused: Maximum number of clients reached.");
//                    clientSocket.close();
//                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //receives the sender and message and configure the way it will be returned on the screen
    private void broadcastMessage(String sender, String message) {
        for (ObjectOutputStream stream : client.values()) {
            try {
                stream.writeObject(sender + ": " + message);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * Applying concurrency programming declaring the method ClientHandler that
     * implements Runnable.
     *
     * Could be ClientHandler extends Thread.
     *
     * The main class declares this class and when the user start, the run
     * method will return
     */
    private class YouChatServerThread implements Runnable {

        private Socket clientSocket;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private String username;

        public YouChatServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        //This determines what happens once the user start a client
        @Override
        public void run() {

            try {
                input = new ObjectInputStream(clientSocket.getInputStream());
                output = new ObjectOutputStream(clientSocket.getOutputStream());

                username = (String) input.readObject();
                client.put(username, output);

                System.out.println("New client '" + username + "' is connected.");

                while (true) {
                    String message = (String) input.readObject();
                    System.out.println("Received message from " + username + ": " + message);
                    broadcastMessage(username, message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                //reducing -1 on the clientCount, then the user is able to create more clients when there are maximum 2 clients
                //clientCount--;

                //return on the server once any client is disconnected.
                client.remove(username);
                System.out.println("User '" + username + "' disconnected");
            }
        }
    }
}
