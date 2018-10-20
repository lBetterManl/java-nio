/**
 * Project: hanson-nio
 * File: PipeTests.java
 * Package: com.hanson.nio
 * Date: 2018年10月3日 上午10:35:39
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;

import org.junit.Test;

/**
 * <p>Description: Pipe管道，线程1单向向线程2传输数据</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年10月3日 上午10:35:39
 */
public class PipeTests {

	@Test
	public void send() throws IOException {
		// 1.获取管道
		Pipe pipe = Pipe.open();
		// 2.将缓冲区中的数据写入管道
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SinkChannel sinkChannel = pipe.sink();
		buffer.put("通过单向管道发送数据".getBytes());
		buffer.flip();
		sinkChannel.write(buffer);
		// 3.读取缓冲区中的数据
		SourceChannel sourceChannel = pipe.source();
		buffer.flip();
		int len = sourceChannel.read(buffer);
		System.out.println(new String(buffer.array(),0,len));
		
		sourceChannel.close();
		sinkChannel.close();
	}
}
