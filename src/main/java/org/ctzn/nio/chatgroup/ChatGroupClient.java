package org.ctzn.nio.chatgroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ChatGroupClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 8888;

    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public ChatGroupClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println("You are " + username);
    }

    // send message to server
    public void send(String msg) {
        msg = username + " : " + msg;

        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // receive message from server
    public void receive() {
        try {
            int readChannels = selector.select();
            if (readChannels > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        System.out.println(new String(buffer.array()).trim());
                    }
                    keys.remove();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // start client
        ChatGroupClient chatClient = new ChatGroupClient();

        // new thread to receive message from server with lamda expression
        new Thread(() -> {
            while (true) {
                chatClient.receive();
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // send message to server
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            chatClient.send(msg);
            if (msg.equals("quit")) {
                break;
            }
        }

    }

}
