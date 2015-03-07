package com.sueking.sub.filehash.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.sueking.sub.filehash.FileHashCalculator;

public class FileHashTest {

	@Test
	public void testHash() throws Exception {
		String filePath = ClassLoader.getSystemResource("testidx.avi").getFile();
		String expectedHash = "84f0e9e5e05f04b58f53e2617cc9c866;b1f0696aec64577228d93eabcc8eb69b;f54d6eb31bef84839c3ce4fc2f57991c;f497c6684c4c6e50d0856b5328a4bedc";
		assertEquals(expectedHash, FileHashCalculator.getHash(filePath));
	}
	
}
