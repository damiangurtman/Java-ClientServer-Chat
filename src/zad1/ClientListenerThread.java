package zad1;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JTextArea;

public class ClientListenerThread extends Thread {
	private final JTextArea chatWindow;
	private final BufferedReader reader;

	public ClientListenerThread(JTextArea chatWindow, BufferedReader reader) {
		this.chatWindow = chatWindow;
		this.reader = reader;
	}

	@Override
	public void run() {
		while (true) {
			try {
				chatWindow.append(reader.readLine() + "\n");
			} catch (IOException e) {
				return;
			}
		}
	}
}
