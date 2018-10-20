/**
 * Project: hanson-nio
 * File: ChannelTests.java
 * Package: com.hanson.nio
 * Date: 2018年9月22日 下午4:48:10
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

/**
 * <p>Description: Channel 通道</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年9月22日 下午4:48:10
 * 
 * 一、通道：用于源节点与目标节点的连接。只负责传输，本身不存储数据。
 * 
 * 二、通道的主要实现类
 * java.nio.channek.Channel接口：
 * 		|--FileChannel
 * 		|--SocketChannel
 * 		|--ServerSocketChannel
 * 		|--DatagramChannel
 * 
 * 三、获取通道
 * 1、Java针对本地支持通道的类提供了getChannel()方法
 * 		本地IO：
 * 		FileInputStream/FileOutStream
 * 		RandomAccessFile
 * 
 * 		网路IO：
 * 		Socket
 * 		ServerSocket
 * 		Datagram
 * 2、在JDK 1.7中的NIO针对各个通道提供了静态方法open()
 * 3、在JDK 1.7中的NIO的Files工具类的newByteChannel()
 * 
 * 四、通道间的数据传输
 * transferFrom()
 * transferTo()
 * 
 * 五、分散（Scatter）与聚集（Gather）
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区
 * 聚集写入（Gathering Writes）：将多个缓冲区的数据聚集到通道中
 * 
 * 六、字符集：Charset  
 * 编码：字符串转换成字节数组
 * 解码：字节数组转换成字符串
 */
public class ChannelTests {

	// 字符集
	@Test
	public void test6() {
		Charset charSet = Charset.forName("UTF-8");
		
		// 获取编码器
		CharsetEncoder encoder = charSet.newEncoder();
		
		// 获取解码器
		CharsetDecoder decoder = charSet.newDecoder();
		
		CharBuffer charBuffer = CharBuffer.allocate(1024);
		charBuffer.put("如果那个人曾出现过，其他人就会变成将就，可我不愿将就！");
		charBuffer.flip();
				
		try {
			// 编码
			ByteBuffer byteBuffer = encoder.encode(charBuffer);
			
			// 解码
			CharBuffer charBuffer2 = decoder.decode(byteBuffer);
			
			System.out.println(charBuffer2.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 字符集
	@Test
	public void test5() {
		long start = System.currentTimeMillis();
		Map<String, Charset> map = Charset.availableCharsets();
		Set<Entry<String, Charset>> entrySet = map.entrySet();
		for (Entry<String, Charset> entry : entrySet) {
			System.out.println(entry.getKey() + " <> " + entry.getValue());
		}
		long time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + time);
	}

	// 分散和聚集
	@Test
	public void test4() {
		long start = System.currentTimeMillis();
		try {
			RandomAccessFile inRandom = new RandomAccessFile("1.txt", "rw");

			// 1.获取通道
			FileChannel inChannel = inRandom.getChannel();

			// 2.分配指定大小的缓冲区(按顺序)
			ByteBuffer byteBuffer1 = ByteBuffer.allocate(100);
			ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);

			// 3.分散读取
			ByteBuffer[] buffers = { byteBuffer1, byteBuffer2 };
			inChannel.read(buffers);

			for (ByteBuffer byteBuffer : buffers) {
				byteBuffer.flip();
			}

			System.out.println(new String(buffers[0].array(), 0, buffers[0].limit()));
			System.out.println("------------------------");
			System.out.println(new String(buffers[1].array(), 0, buffers[1].limit()));

			// 4.聚集写入
			RandomAccessFile outRandom = new RandomAccessFile("2.txt", "rw");
			FileChannel outChannel = outRandom.getChannel();

			outChannel.write(buffers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + time);
	}

	// 通道之间数据传输（直接缓冲区）
	@Test
	public void test3() {
		long start = System.currentTimeMillis();
		try (FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
				FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,
						StandardOpenOption.READ, StandardOpenOption.CREATE)) {

			// 两者功能一样
			inChannel.transferTo(0, inChannel.size(), outChannel);
			outChannel.transferFrom(inChannel, 0, inChannel.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + time);
	}

	// 使用直接缓冲区完成文件复制（内存映射文件）
	// 直接缓冲区效率高，但是占用率高，一般只有在效率明显提升时使用
	@Test
	public void test2() {
		long start = System.currentTimeMillis();
		try (FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
				FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,
						StandardOpenOption.READ, StandardOpenOption.CREATE)) {

			// 内存映射文件
			MappedByteBuffer inMappedByteBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
			MappedByteBuffer outMappedByteBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());

			// 直接对缓冲区进行数据的读写操作
			byte[] dst = new byte[inMappedByteBuf.limit()];
			inMappedByteBuf.get(dst);
			outMappedByteBuf.put(dst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + time);
	}

	// 利用通道完成文件的复制(非直接缓冲区)
	@Test
	public void test1() {
		long start = System.currentTimeMillis();
		try (FileInputStream inputStream = new FileInputStream("1.jpg");
				FileOutputStream outputStream = new FileOutputStream("2.jpg");
				// 1.获取通道
				FileChannel inChannel = inputStream.getChannel();
				FileChannel outChannel = outputStream.getChannel()) {

			// 2.分配指定大小的缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);

			// 3.
			while (inChannel.read(buffer) != -1) {
				buffer.flip();// 切换成读取数据
				// 4.将缓冲区中的数据写入通道
				outChannel.write(buffer);
				buffer.clear();// 清空缓冲区
			}

		} catch (Exception e) {

		}
		long time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + time);
	}

}
