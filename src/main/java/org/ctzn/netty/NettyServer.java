package org.ctzn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            // 可以用集合管理 SocketChannel, 在推送消息时，可以将业务加入到各个 channel 对应的 NIOEventLoop 的 taskQueue
                            // 或 scheduleTaskQueue
                            System.out.println("Client socketchannel hashcode: " + sc.hashCode());
                            sc.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("server is ready...");
            ChannelFuture cf = bootstrap.bind(6668).sync();
            // 注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("server is success");
                    } else {
                        System.out.println("server is fail");
                    }
                }
            });

            // 对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
