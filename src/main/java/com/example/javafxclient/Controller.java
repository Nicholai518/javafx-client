package com.example.javafxclient;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

// implementing Initializable allows so we can work with all our fxml variables
public class Controller implements Initializable {
	@FXML
	private Label welcomeText;

	@FXML
	protected void onHelloButtonClick() {
		welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	private Button button_send;

	@FXML
	private TextField tf_message;

	@FXML
	private VBox vbox_messages;

	@FXML
	private ScrollPane sp_main;

	@FXML
	private Client client;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		try {
			client = new Client(new Socket("localhost", 1234));
			System.out.println("Connected to server.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// set vertical value
				sp_main.setVvalue((Double) newValue);
			}
		});

		client.receiveMessageFromServer(vbox_messages);

		button_send.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				String messageToSend = tf_message.getText();

				// if message is NOT empty
				if (!messageToSend.isEmpty()) {
					HBox hbox = new HBox();
					// align this message on the right
					// received messaged from server will be on the left
					// this is similar to how a text conversation looks
					hbox.setAlignment(Pos.CENTER_RIGHT);

					hbox.setPadding(new Insets(5, 5, 5, 10));
					Text text = new Text(messageToSend);
					TextFlow textFlow = new TextFlow();
					// this is the original, help from intellij
					textFlow.setStyle("-fx-color: rgb(239,242,255); " +
							"-fx-background-color: rgb(15,25,242);" +
							" -fx-background-radius: 20px;");

					//					textFlow.setStyle("-fx-color: rgb(239,242,255)");
					//					textFlow.setStyle("-fx-background-color: rgb(15,25,242)");
					//					textFlow.setStyle("-fx-background-radius: 20px");

					// set padding
					textFlow.setPadding(new Insets(5, 10, 5, 10));

					// Text color
					text.setFill(Color.color(0.934, 0.945, 0.996));

					// add to our horizontal box
					hbox.getChildren().add(textFlow);

					// Vertical box messages
					vbox_messages.getChildren().add(hbox);

					// send to client, so they can add to their GUI
					client.sendMessageToServer(messageToSend);

					// finally, we want to clear our text field
					// allowing a new message to be entered
					tf_message.clear();
				}
			}
		});
	}

	public static void addLabel(String msgFromServer, VBox vBox) {

		HBox hBox = new HBox();
		// position is center left, this is visually similar to
		// how incoming text messages look on our phones
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.setPadding(new Insets(5, 5, 5, 10));

		Text text = new Text(msgFromServer);

		TextFlow textFlow = new TextFlow();

//		textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
//				"-fx-background-radius: 20px");

				textFlow.setStyle("-fx-background-color: rgb(233,233,235)");
				textFlow.setStyle("-fx-background-radius: 20px;");

		textFlow.setPadding(new Insets(5,10,5,10));
		hBox.getChildren().add(textFlow);

		// In JavaFX we cannot update the GUI from a thread other than the application thread
		// in other words, we cannot be using another thread to add our hbox
		// to our scroll pane or to our vertical box
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				vBox.getChildren().add(hBox);
			}
		});
	}
}
