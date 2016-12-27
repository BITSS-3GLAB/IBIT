package com.bitss.Digital_BIT.LostFound;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import android.content.Context;

/**
 * 失物招领文件读写类
 * */
public class FileHandle {
	private Context context;
	private static final String FILE_NAME = "LostFoundDate";

	public FileHandle(Context _context) {
		context = _context;
	}

	/**
	 * 把失物招领的数据写文件
	 * 
	 * 把原来的清空，然后再写入
	 * */
	public void writeFile(LinkedList<LostFoundModel> dateList) {
		try {
			// 打开程序自己私有的文件和数据，必须使用Activity提供openFileOutput和openFileInput方法
			// openFileOutput为写入操作
			FileOutputStream fout = context.openFileOutput(FILE_NAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream outWriter = new ObjectOutputStream(fout);
			outWriter.writeObject(dateList);

			outWriter.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取失物招领的数据
	 * */
	@SuppressWarnings("unchecked")
	public LinkedList<LostFoundModel> readFile() {
		LinkedList<LostFoundModel> res = new LinkedList<LostFoundModel>();
		try {
			FileInputStream fin = context.openFileInput(FILE_NAME);
			ObjectInputStream inReader = new ObjectInputStream(fin);
			res = (LinkedList<LostFoundModel>) inReader.readObject();
			inReader.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
