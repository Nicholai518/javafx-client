package com.example.javafxclient;

import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

	// fields
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	// constructors
	public Client(Socket socket){

		try{
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch(IOException e){
			e.printStackTrace();
			System.out.println("Error creating client.");
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	// methods
	public void sendMessageToServer(String messageToServer){

		try {
			bufferedWriter.write(messageToServer);        // Writes message to buffer
			bufferedWriter.newLine();                    // terminating character
			bufferedWriter.flush();                        // flush buffer manually to push message to server
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error sending message to the client");
			// close for security purposes
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	public void receiveMessageFromServer(VBox vBox){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (socket.isConnected()) {
					try {
						String messageFromServer = bufferedReader.readLine();
						Controller.addLabel(messageFromServer, vBox);
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Error receiving message from the client");
						closeEverything(socket, bufferedReader, bufferedWriter);

						// if there is an error we want to break out of the while loop
						break;
					}
				}
			}
		}).start();

	}
	// used to close objects for security purposes
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
