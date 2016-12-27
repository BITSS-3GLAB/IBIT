package com.bitss.Digital_BIT.Meeting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;

class MeetingFileAsker{
	private Context context;
	private String fileName = "meetingdata";
	public MeetingFileAsker(Context _context){
		context = _context;
	}
	public void writeFile(ArrayList<LinkedList<MeetingData>> message){
		try{
			FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream outWriter = new ObjectOutputStream(fout);
			outWriter.writeObject(message);
			
			outWriter.close();
			fout.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public ArrayList<LinkedList<MeetingData>> readFile(){
		ArrayList<LinkedList<MeetingData>> res = null;
		try{
			FileInputStream fin = context.openFileInput(fileName);
			ObjectInputStream inReader = new ObjectInputStream(fin);
			res = (ArrayList<LinkedList<MeetingData>>)inReader.readObject();
			inReader.close();
			fin.close();	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
}
