package com.bitss.Digital_BIT.News;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import android.content.Context;

/*
 * 评论读写类
 */
public class CommentFileAsker {
	private Context  context;
	private String str = "commentlistdata";

	public CommentFileAsker(Context _context)
	{
		context = _context;
	}

	/**
	 * 写文件，要包括评论的内容和该条新闻所处的新闻类别，以及该条新闻的id
	 */
	public void writeFile(LinkedList<CommentData> commentDatas , int newsType,long newsId){
		String filename = str + newsType + newsId;
		try{
			//打开自己私有的文件和数据
			FileOutputStream fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream outWriter  = new ObjectOutputStream(fout);
			outWriter.writeObject(commentDatas);

			outWriter.close();
			fout.close();

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 读文件，传入新闻的类型和新闻的id，返回所有的评论列表
	 */
	public LinkedList<CommentData> readFile(int typeId , long newsId){
		LinkedList<CommentData> res = new LinkedList<CommentData>();
		String fileName = str + typeId + newsId;
		try{
			FileInputStream fin = context.openFileInput(fileName);
			ObjectInputStream inReader = new ObjectInputStream(fin);
			res = (LinkedList<CommentData>)inReader.readObject();
			inReader.close();
			fin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}

}
