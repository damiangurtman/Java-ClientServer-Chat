/**
 *
 *  @author Gurtman Damian S13937
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
	private static final String START_COMMAND = "#START";

	private ServerSocketChannel ssc;
	private Selector selector;
	private ByteBuffer buf = ByteBuffer.allocate(256);

	public Server(int port) throws IOException {
		prepareConfiguration(port);
		startServer();
	}

	private void prepareConfiguration(int port) throws IOException, ClosedChannelException {
		this.ssc = ServerSocketChannel.open();
		this.ssc.socket().bind(new InetSocketAddress(port));
		this.ssc.configureBlocking(false);
		this.selector = Selector.open();
		this.ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void startServer() {
		try {
			System.out.println("[NIO SERVER]: Started!");
			Iterator<SelectionKey> iter;
			while (this.ssc.isOpen()) {
				selector.select();
				iter = this.selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					processClient(iter);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processClient(Iterator<SelectionKey> iter) throws IOException {
		SelectionKey key;
		key = iter.next();
		iter.remove();
		if (key.isAcceptable())
			accept(key);
		if (key.isReadable())
			read(key);
	}

	private void accept(SelectionKey key) throws IOException {
		SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ, getClientAddress(sc));
	}

	private String getClientAddress(SocketChannel sc) {
		return (new StringBuilder(sc.socket().getInetAddress().toString())).append(":").append(sc.socket().getPort()).toString();
	}

	private void read(SelectionKey key) throws IOException {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			sendToAll(readMessage(key, socketChannel));
		} catch (Exception e) {
			key.cancel();
		}

	}

	private String readMessage(SelectionKey key, SocketChannel ch) throws IOException {
		StringBuilder sb = new StringBuilder();
		buf.clear();
		int read = 0;
		while ((read = ch.read(buf)) > 0) {
			buf.flip();
			byte[] bytes = new byte[buf.limit()];
			buf.get(bytes);
			sb.append(new String(bytes));
			buf.clear();
		}
		String msg = null;
		if (read < 0) {
			ch.close();
		} else {
			if (sb.toString().startsWith(START_COMMAND)) {
				attachToChat(key, sb);
			}
			msg = key.attachment() + ": " + sb.toString();
		}
		return msg;
	}

	private void attachToChat(SelectionKey key, StringBuilder sb) throws IOException {
		String login = sb.toString().split(";")[1];
		sendToAll(login + ", witaj na czacie!");
		key.attach(login);

	}

	private void sendToAll(String msg) throws IOException {
		if (msg.contains(START_COMMAND))
			return;
		msg = msg.replaceAll("\r\n", " ") + "\r\n";
		ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
		for (SelectionKey key : selector.keys()) {
			if (key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel sch = (SocketChannel) key.channel();
				sch.write(msgBuf);
				msgBuf.rewind();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new Server(1111);
	}
}
