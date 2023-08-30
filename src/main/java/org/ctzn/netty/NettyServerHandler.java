package org.ctzn.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 模拟很耗时的任务
//        Thread.sleep(10 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello client! 11", CharsetUtil.UTF_8));
        // solution 1: 用户自定义普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                    try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello client lala! current time: " + new Date(), CharsetUtil.UTF_8));

                    System.out.println("current time" + new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // solution 2: 用户自定义定时任务 该任务是提交到 scheduleTaskQueue 中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello client haha! current time: " + new Date(), CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 5, TimeUnit.SECONDS);

//        System.out.println("server ctx: " + ctx);
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("Server received: " + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("Client address: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello client! current time: " + new Date(), CharsetUtil.UTF_8));
        System.out.println("current time" + new Date());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
