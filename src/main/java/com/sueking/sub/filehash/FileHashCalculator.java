package com.sueking.sub.filehash;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Arrays;

public class FileHashCalculator {
	
	private final static int BUFFER_SIZE = 4096;
	
	public static String getHash(String filePath) throws Exception{
		
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		long fileLength = file.length();
		
		long[] positions = new long[]{4096, fileLength / 3 * 2, fileLength / 3, fileLength - 8192};
		StringBuilder stringBuilder = new StringBuilder();
		for (long position : positions) {
			byte[] buffer = new byte[BUFFER_SIZE];
			file.seek(position);
			int realBufferSize = file.read(buffer);
			buffer = Arrays.copyOfRange(buffer, 0, realBufferSize);
			stringBuilder.append(bytesToMD5(buffer));
			stringBuilder.append(";");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}
	
	private static String bytesToString(byte[] bytes){
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : bytes) {
			int bias = (b & 0xf0) >>> 4;
			stringBuilder.append(Integer.toHexString(bias));
			bias = b & 0xf;
			stringBuilder.append(Integer.toHexString(bias));
		}
		return stringBuilder.toString();
	}
	
	
	private static String bytesToMD5(byte[] bytes) throws Exception{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] buffer = messageDigest.digest(bytes);
		return bytesToString(buffer);
	}
	
}
