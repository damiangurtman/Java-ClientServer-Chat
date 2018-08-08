/**
 *
 *  @author Gurtman Damian S13937
 *
 */

package zad1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame {

	private JTextArea chatWindow;
	private BufferedReader reader;
	private PrintWriter writer;
	private String login;
	private JTextField messageSender;

	public Client() throws Exception {
		initializeServerConnection();
		askForLogin(writer);
		setTitle("Czat dla klienta: " + login);
		chatWindow = new JTextArea();
		JScrollPane chatScrollPane = new JScrollPane(chatWindow);
		chatScrollPane.setPreferredSize(new Dimension(550, 450));
		chatWindow.setEditable(false);
		add(chatScrollPane, BorderLayout.CENTER);
		JPanel control = new JPanel(new BorderLayout());
		messageSender = new JTextField();
		control.add(messageSender, BorderLayout.CENTER);
		JButton send = new JButton("Wyslij");
		control.add(send, BorderLayout.EAST);
		add(control, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		new ClientListenerThread(chatWindow, reader).start();
		send.addActionListener(this::sendMessage);

	}

	private void initializeServerConnection() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 1111);
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void sendMessage(ActionEvent evt) {
		writer.println(messageSender.getText());
		messageSender.setText("");
	}

	private void askForLogin(final PrintWriter writer) {
		login = JOptionPane.showInputDialog("Wprowadz swoj login");		
		writer.println("#START;" + login);
	}

	public static void main(String[] args) throws Exception {
		new Client();
	}

}
