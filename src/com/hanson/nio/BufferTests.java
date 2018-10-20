/**
 * Project: hanson-nio
 * File: BufferTests.java
 * Package: com.hanson.nio
 * Date: 2018年9月22日 下午3:22:40
 * Copyright (c) 2018, hanson.yan@qq.com All Rights Reserved.
 */
package com.hanson.nio;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * <p>Description: Buffer 缓冲区</p>
 * @author Hanson
 * @since  JDK 1.8
 * @time 2018年9月22日 下午3:22:40
 * 
 * 一、缓冲区（Buffer）：在Java NIO中负责数据的存取。缓冲区就是数组。用户存储不同数据类型的数据
 * 
 * 根据数据类型不同（boolean除外），提供了相应的缓冲区:
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 
 * 上述缓冲区的管理方式几乎一致，通过allocate()获取缓冲区
 * 
 * 二、缓冲区存取数据的两个核心方法
 * put() 存入数据到缓冲区
 * get() 获取缓冲区中的数据
 * 
 * 三、缓冲区的四个核心属性
 * capacity 缓冲区容量，最大不能超过此数，一旦声明不能改变
 * position 存取位置，为0
 * limit 界限，等于容量，limit后的数据不能读写  
 * 
 * mark() 标记，记录当前position的位置。可以通过reset()恢复到标记位置
 * 
 * 0 <= mark <= position <= limit <= capacity
 * 
 * 四、直接缓冲区与非直接缓冲区
   * 非直接缓冲区：通过allocate() 方法分配缓冲区，将缓冲区建立在JVM的内存中
  * 直接缓冲区：通过allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率（大量数据使用）。
 */
public class BufferTests {
	
	
	@Test
	public void test2() {
		String str = "abcde";
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
		buffer.put(str.getBytes());
		
		buffer.flip();
		
		byte[] dst = new byte[buffer.limit()];
		buffer.get(dst, 0, 2);
		System.out.println(new String(dst,0,2));
		System.out.println(buffer.position());
		
		// mark()
		buffer.mark();
		
		buffer.get(dst, 2, 2);
		System.out.println(new String(dst,2,2));
		System.out.println(buffer.position());
		
		// reset() 恢复到标记位置
		buffer.reset();
		System.out.println(buffer.position());
		
	}
	
	@Test
	public void test1() {
		// 1、allocate(int) 初始化容量为10字节
		ByteBuffer buffer = ByteBuffer.allocate(10);
		System.out.println("1-capacity--"+buffer.capacity());
		System.out.println("1-limit--"+buffer.limit());
		System.out.println("1-position--"+buffer.position());
		
		// 2、put() 存入数据
		String str = "abcde";
		buffer.put(str.getBytes());
		System.out.println("2-capacity--"+buffer.capacity());
		System.out.println("2-limit--"+buffer.limit());
		System.out.println("2-position--"+buffer.position());
		
		// 3、flip() 切换读取数据模式
		buffer.flip();
		System.out.println("3-capacity--"+buffer.capacity());
		System.out.println("3-limit--"+buffer.limit());
		System.out.println("3-position--"+buffer.position());
		
		// 4、get() 取数据
		byte[] dst = new byte[buffer.limit()];
		buffer.get(dst);
		System.out.println("4-capacity--"+buffer.capacity());
		System.out.println("4-limit--"+buffer.limit());
		System.out.println("4-position--"+buffer.position());
		System.out.println(new String(dst, 0, dst.length));
		
		// 5、rewind() 可重复读数据
		buffer.rewind();
		System.out.println("5-capacity--"+buffer.capacity());
		System.out.println("5-limit--"+buffer.limit());
		System.out.println("5-position--"+buffer.position());
		
		// 6、clear() 清空缓冲区，但是数据还在
		buffer.clear();
		System.out.println("6-capacity--"+buffer.capacity());
		System.out.println("6-limit--"+buffer.limit());
		System.out.println("6-position--"+buffer.position());
		System.out.println((char)buffer.get());
	}



}
