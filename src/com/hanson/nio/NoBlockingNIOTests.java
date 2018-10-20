/**
 * Project: hanson-nio
 * File: NoBlockingNIOTests.java
 * Package: com.hanson.nio
 * Date: 2018年10月3日 上午9:57:13
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * <p>Description: 非阻塞NIO</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年10月3日 上午9:57:13
 */
public class NoBlockingNIOTests {
	
	// 客户端
	@Test
	public void client() throws IOException {
		// 1. 获取通道
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		// 2.切换非阻塞模式
		sChannel.configureBlocking(false);
		// 3.分配指定大小的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// 4.发送数据给服务端
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String str = scanner.next();
			buffer.put((LocalDateTime.now().toString()+"\n"+str).getBytes());
			buffer.flip();
			sChannel.write(buffer);
			buffer.clear();
		}
		
		// 5.关闭通道
		sChannel.close();		
	}
	
	// 服务端
	@Test
	public void server() throws IOException {
		// 1.获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		// 2.切换非阻塞模式
		ssChannel.configureBlocking(false);
		// 3.绑定链接
		ssChannel.bind(new InetSocketAddress(9898));
		// 4.获取选择器
		Selector selector = Selector.open();
		// 5.将通道注册到选择器，斌指定监听事件
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		// 6.轮询式获取选择器上已经准备就绪的事件
		while (selector.select()>0) {
			// 7.获取所有注册的选择键
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				// 8.获取准备就绪的事件
				SelectionKey key = (SelectionKey) iterator.next();
				// 9.判断具体是什么事件就绪
				if (key.isAcceptable()) {
					// 10.若接收就绪，获取客户端连接
					SocketChannel sChannel = ssChannel.accept();
					// 11.切换非阻塞模式
					sChannel.configureBlocking(false);
					// 12.将该通道注册到选择器
					sChannel.register(selector, SelectionKey.OP_READ);
				} else if (key.isReadable()) {
					// 13.获取当前选择器上的读就绪状态的通道
					SocketChannel sChannel = (SocketChannel)key.channel();
					// 14.读取数据
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					
					int len = 0;
					while ((len = sChannel.read(buffer))>0) {
						buffer.flip();
						System.out.println(new String(buffer.array(),0,len));
						buffer.clear();
					}
				}
				// 15.取消选择键
				iterator.remove();
			}
			
		}
	}

}
