/**
 * Project: hanson-nio
 * File: BlockingNIO.java
 * Package: com.hanson.nio
 * Date: 2018年10月3日 上午9:19:48
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * <p>Description: 阻塞NIO</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年10月3日 上午9:19:48
 */
public class BlockingNIOTests {

	// 客户端
	@Test
	public void client() throws IOException {
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (inChannel.read(buffer) != -1) {
			buffer.flip();
			sChannel.write(buffer);
			buffer.clear();
		}
		
		// 发完数据
		sChannel.shutdownOutput();

		// 接受服务端的反馈
		int len = 0;
		while ((len = sChannel.read(buffer)) != -1) {
			buffer.flip();
			System.out.println(new String(buffer.array(), 0, len));
			buffer.clear();
		}

		inChannel.close();
		sChannel.close();
	}

	// 服务端
	@Test
	public void server() throws IOException {
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		ssChannel.bind(new InetSocketAddress(9898));
		SocketChannel sChannel = ssChannel.accept();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		while (sChannel.read(buffer)!=-1) {
			buffer.flip();
			outChannel.write(buffer);
			buffer.clear();			
		}
		
		// 发送反馈给客户端
		buffer.put("服务端接收成功".getBytes());
		buffer.flip();
		sChannel.write(buffer);
		
		sChannel.close();
		outChannel.close();
		ssChannel.close();
	}
}
