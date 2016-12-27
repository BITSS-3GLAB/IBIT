package com.bitss.Digital_BIT.News;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

/*
 * 新闻文件读写类
 */

public class NewsFileAsker {
	private Context context;
	private String str = "newslistdata";

	public NewsFileAsker(Context _context) {
		context = _context;
	}

	/**
	 * 写文件，传入新闻列表数据，和新闻类型id
	 * */
	public void writeFile(LinkedList<NewsData> message, long id) {
		String fileName = str + id;
		try {
			// 打开程序自己私有的文件和数据，必须使用Activity提供openFileOutput和openFileInput方法
			// openFileOutput为写入操作
			FileOutputStream fout = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream outWriter = new ObjectOutputStream(fout);
			outWriter.writeObject(message);

			outWriter.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写文件，传入订阅的顺序
	 * */
	public void writeOrderStrToFile(String orderString, String fileName) {
		FileOutputStream fout;
		try {
			fout = context.openFileOutput(fileName, 0);
			byte[] bytes = orderString.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileName
	 *            :需要读取订阅顺序的文件名 从指定的文件中读取订阅顺序的字符串
	 * */
	public String readOrderFromFile(String fileName) {
		String res = null;
		FileInputStream fin;
		try {
			fin = context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			res.replace(",", "");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 读文件，传入新闻id，返回读取到的新闻列表
	 * */
	@SuppressWarnings("unchecked")
	public LinkedList<NewsData> readFile(long id) {
		LinkedList<NewsData> res = new LinkedList<NewsData>();
		String fileName = str + id;
		try {
			FileInputStream fin = context.openFileInput(fileName);
			ObjectInputStream inReader = new ObjectInputStream(fin);
			res = (LinkedList<NewsData>) inReader.readObject();
			inReader.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// 读取新闻具体内容，传入新闻类型和新闻id，返回新闻具体
	@SuppressWarnings("unchecked")
	public NewsContentData readContent(int type, long id) {
		String fileName = "" + id + "." + type;
		NewsContentData res = null;
		try {
			FileInputStream fin = context.openFileInput(fileName);
			ObjectInputStream inReader = new ObjectInputStream(fin);
			res = (NewsContentData) inReader.readObject();
			inReader.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 写新闻具体内容
	 * 
	 * @param 传入内容
	 *            ，类型，id
	 * 
	 *            储存名为类型+id
	 * */
	public void writeContent(NewsContentData message, int type, long id) {
		String fileName = String.valueOf(id) + "." + type;
		try {
			FileOutputStream fout = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream outWriter = new ObjectOutputStream(fout);
			outWriter.writeObject(message);

			outWriter.close();
			fout.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkIntOnly(String str) {
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) > '9' || str.charAt(i) < '0')
				return false;
		return true;
	}

	// 清理具体内容缓存，将
	public void cleanContent(int type, long newsId) {
		File f = new File("/data/data/com.bitss.Digital_BIT/files");
		if (f.exists()) {
			File[] file = f.listFiles();
			for (int i = 0; i < file.length; i++) {
				String str = file[i].getName();
				int div = str.indexOf(".");
				if (div == -1)
					continue;
				String fileType = str.substring(div + 1, str.length());
				if (checkIntOnly(fileType) == false)
					continue;

				int thisNewsType = Integer.parseInt(fileType);
				if (thisNewsType == type) {
					String name = str.substring(0, div);
					if (checkIntOnly(name) == false)
						continue;
					int id = Integer.parseInt(name);
					if (newsId - id > 10)
						file[i].delete();
				}
			}
		}
		File[] newfile = f.listFiles();
	}

}
