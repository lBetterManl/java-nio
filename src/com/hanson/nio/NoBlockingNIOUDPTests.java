/**
 * Project: hanson-nio
 * File: NoBlockingNIOUDPTests.java
 * Package: com.hanson.nio
 * Date: 2018年10月3日 上午10:24:48
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * <p>Description: DatagramChannel</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年10月3日 上午10:24:48
 */
public class NoBlockingNIOUDPTests {

	@Test
	public void send() throws IOException {
		DatagramChannel dChannel = DatagramChannel.open();
		dChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		Scanner scanner = new Scanner(System.in);
		
		while (scanner.hasNext()) {
			String str = scanner.next();
			buffer.put((LocalDateTime.now().toString()+"\n"+str).getBytes());
			buffer.flip();
			dChannel.send(buffer, new InetSocketAddress("127.0.0.1", 9898));
			buffer.clear();
		}
		
		dChannel.close();
	}
	
	@Test
	public void receive() throws IOException {
		DatagramChannel dChannel = DatagramChannel.open();
		dChannel.configureBlocking(false);
		dChannel.bind(new InetSocketAddress(9898));
		Selector selector = Selector.open();
		dChannel.register(selector, SelectionKey.OP_READ);
		
		while (selector.select()>0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = (SelectionKey) iterator.next();
				if (key.isReadable()) {
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					dChannel.receive(buffer);
					buffer.flip();
					System.out.println(new String(buffer.array(),0,buffer.limit()));
					buffer.clear();
				}
			}
			iterator.remove();
		}
	}
}
