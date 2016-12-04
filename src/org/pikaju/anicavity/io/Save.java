package org.pikaju.anicavity.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save {

	private DataInputStream dis;
	private DataOutputStream dos;
	
	public void readFile(String filePath) {
		try {
			File file = new File(filePath);
			if(!file.exists()) {
				return;
			}
			dis = new DataInputStream(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeFile(String filePath) {
		try {
			File file = new File(filePath);
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			dos = new DataOutputStream(new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean active() {
		return dis != null || dos != null;
	}
	
	public void close() {
		try {
			if(dis != null) {
				dis.close();
				dis = null;
			}
			if(dos != null) {
				dos.flush();
				dos.close();
				dos = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DataInput getDataInput() {
		return dis;
	}
	
	public DataOutput getDataOutput() {
		return dos;
	}
	
	public String toString() {
		return "Save[read = " + (dis != null) + ", write = " + (dos != null) + "]";
	}
	
	public int hashCode() {
		return dis.hashCode() | (dos.hashCode() << 16);
	}
}
