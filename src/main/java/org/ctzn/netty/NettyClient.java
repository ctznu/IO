package org.ctzn.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        // client need one event loop group
        EventLoopGroup group = new NioEventLoopGroup();


        try {
            // create client bootstrap
            Bootstrap bootstrap = new Bootstrap();

            // set related parameters
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("Client is ok...");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }


    }
}
