package org.ctzn.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        if (!socketChannel.connect(new InetSocketAddress("localhost", 8888))) {
            while (!socketChannel.finishConnect()) {
                System.out.println("Connecting...");
            }
        }

        // 无限循环从控制台等待用户输入
        while (true) {
            Scanner scanner = new Scanner(System.in);

            String message = scanner.nextLine();
            // 反馈信息给服务器
            System.out.println("Message sent: " + message);
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
//            System.in.read();
        }

//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        String userInput = reader.readLine();
//
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        buffer.put(userInput.getBytes());
//        buffer.flip();
//        while (buffer.hasRemaining()) {
//            socketChannel.write(buffer);
//        }
//
//        socketChannel.close();
//
//        reader.close();


    }
}
