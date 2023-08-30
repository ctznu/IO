package org.ctzn.nio.chatgroup;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ChatGroupServer {
    private static final int DEFAULT_PORT = 8888;
    private ServerSocketChannel listenChannel;
    private Selector selector;

    public ChatGroupServer() {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new java.net.InetSocketAddress(DEFAULT_PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() {
        try {
            while (true) {
                int count = selector.select();
                if (count > 0) {
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + " is connected.");
                        }
                        if (key.isReadable()) {
                            // read message
                            readMessage(key);
                        }
                        keys.remove();
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readMessage(SelectionKey key) {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if (count > 0) {
                String msg = new String(buffer.array());
                System.out.println("from client: " + msg);

                // send message to all other clients
                sendMessageToOtherClients(msg, channel);
            }

        } catch (IOException e) {
//            e.printStackTrace();
            try {
                System.out.println(channel.getRemoteAddress() + " is disconnected.");
                key.cancel();
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMessageToOtherClients(String msg, SocketChannel self) {
        selector.keys().stream()
                .filter(key -> key.channel() instanceof SocketChannel && key.channel() != self)
                .filter(key -> (SocketChannel) key.channel() != self)
                .map(key -> (SocketChannel) key.channel())
                .forEach(channel -> {

                    try {
                        channel.write(ByteBuffer.wrap(msg.getBytes()));
        //                System.out.println("from client: " + msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        });
    }

    public static void main(String[] args) {
        // create server
        ChatGroupServer chatGroupServer = new ChatGroupServer();
        chatGroupServer.listen();
    }
}
