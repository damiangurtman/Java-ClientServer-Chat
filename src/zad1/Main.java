/**
 *
 *  @author Gurtman Damian S13937
 *
 */

package zad1;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		new Thread(Main::startServer).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		startClient(2);

	}

	private static void startClient(int amount) {
		for (int i = 0; i < amount; i++) {
			new Thread(Main::createClient).start();
		}
	}

	public static void startServer() {
		try {
			new Server(1111);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createClient() {
		try {
			new Client();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
