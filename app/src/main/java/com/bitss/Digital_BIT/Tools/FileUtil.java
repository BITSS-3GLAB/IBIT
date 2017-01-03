package com.bitss.Digital_BIT.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.provider.ContactsContract.Directory;


/**
 * 文件操作工具
 * 
 * @author zxp
 * 
 */
public class FileUtil {
	/**
	 * 将一个bitmap写入本地文件
	 */
	public static File bitmap2file(Bitmap bitmap, int quality) {
		// 得到外部存储卡的路径
		String path = FileUtil.getImageCachePath().toString();
		// .png是将要存储的图片的名称
		File file = new File(path, "avatar.jpg");
		// 从资源文件中选择一张图片作为将要写入的源文件
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, quality, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static File bitmap2file(Bitmap bitmap)
	{
		return bitmap2file(bitmap, 80);
		
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (!oldfile.exists()) { // 文件不存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
	
	public static File bitmaptofile(Bitmap bitmap, String name)
	{
		return bitmaptofile(bitmap, name, 80);
	}


	public static File bitmaptofile(Bitmap bitmap, String name, int quality) {
		// 得到外部存储卡的路径
		String path = FileUtil.getImageCachePath().toString();
		// .png是将要存储的图片的名称
		File file = new File(path, name + ".jpg");
		// 从资源文件中选择一张图片作为将要写入的源文件
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, quality, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static void deleteWhatuser(String name) {
		String path = FileUtil.getImageCachePath().toString();
		// .png是将要存储的图片的名称
		File file = new File(path, name + ".jpg");
		file.delete();
	}

	/**
	 * 删除文件
	 * 
	 * @param nameStr
	 */

	public static boolean delete(String nameStr) {
		File delFile = new File(nameStr);
		if (delFile.exists()) {
			return delFile.delete();
		} else {
			return false;
		}
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 */
	public static boolean isExistSDCard() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	public static final String MAIN_SOFT_FOLDER_NAME = "Family_1";
	public static final String CACHE_FOLDER_NAME = "Cache";
	
	// 获得图像缓存的目录。
	public static String getImageCachePath()
	{
		if (!isExistSDCard())
		{
			return "";
		}
		
		String sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		String result = sdRoot +
				"/" + MAIN_SOFT_FOLDER_NAME + "/" + CACHE_FOLDER_NAME;
		if (new File(result).exists() && new File(result).isDirectory())
		{
			return result;
		}
		else {
			return sdRoot;
		}
	}
	
	public static void CreateSoftFolder()
	{
		if (!isExistSDCard())
		{
			return;
		}
		
		String sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		File f1 = new File(sdRoot, MAIN_SOFT_FOLDER_NAME);
		
		if (f1.exists() && !f1.isDirectory())
		{
			f1.delete();
		}
		
		if (!f1.exists())
		{
			try {
				f1.mkdir();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		File f2 = new File(f1, CACHE_FOLDER_NAME);
		if (f2.exists() && !f2.isDirectory())
		{
			f2.delete();
		}
		
		
		if (!f2.exists())
		{
			try {
				f2.mkdir();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}
	
	public static void deleteDifferentFile(final String fileDir, final String[] olderList,final String[] newFiles )
	{
		deleteDifferentFile(null, fileDir, olderList, newFiles );
	}
	
	public static void deleteDifferentFile(final Context context, final String fileDir, final String[] olderList,final String[] newFiles ) {
		if (olderList == null || newFiles == null)
		{
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					// 暂停线程，有足够的时候处理图片后再做删除等。这样做不好，需要有一个lock的标识最好，更稳定。
					// 延迟1.5s处理。
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
				
				List<String> list = getDifferentFilePath(olderList, newFiles);
				
				if (list.size()>5 || list.size() == 0)
				{
					// 超过5张图，说明前面的处理出问题了，绝对不能乱删除。
					// 这里相当于是一个安全保护措施。
					return;
				}
				
				ImageTools.deleteLatestPhoto((Activity)context);
				
//				System.out.println(list);
//				for (String string : list) {
//					try {
//						//SDCardFileUtils.forceDelete(new File(fileDir,string));
//						File f = new File(fileDir,string);
//						if (f.exists())
//						{
//							f.delete();
//						}
//					} catch (Exception e) {
//						//LogUtils.e(CameraActivity.this, TAG, "鍒犻櫎鏂囦欢澶辫触"+string);
//					}
//
//				}
//				
//				
//				if (context != null)
//				{
//					//ImageTools.ScanSysPhotos(context);
//					FileUtil.cleanExternalCache(context);
//				}
			}
		}).start();
	}
	

	public static List<String> getDifferentFilePath(String[] olderList, String[] currentList) {
		List<String> list1 = Arrays.asList(olderList);
		List<String> list2 = new ArrayList<String>();
		for (String t : currentList) {
			if (!list1.contains(t)) {
				list2.add(t);
			}
		}
		return list2;
	}
	
	/**      * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)      *       * @param context      */   
	 public static void cleanInternalCache(Context context) {      
	 deleteFilesByDirectory(context.getCacheDir());     
	 }       
	 /**      * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)      *       * @param context      */    
	 public static void cleanDatabases(Context context) {         
	 deleteFilesByDirectory(new File("/data/data/"                 + context.getPackageName() + "/databases"));   
	 }       
	 /**      * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)      *       * @param context      */    
	 public static void cleanSharedPreference(Context context) {       
	 deleteFilesByDirectory(new File("/data/data/"                 + context.getPackageName() + "/shared_prefs"));     
	 }      
	 /**      * 按名字清除本应用数据库      *       * @param context      * @param dbName      */     
	 public static void cleanDatabaseByName(Context context, String dbName) {         context.deleteDatabase(dbName);     
	 }      
	 /**      * 清除/data/data/com.xxx.xxx/files下的内容      *       * @param context      */    
	 public static void cleanFiles(Context context) {        
	 deleteFilesByDirectory(context.getFilesDir());      
	 }       
	 /**      * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)      *       * @param context      */   
	 public static void cleanExternalCache(Context context) {      
	 if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) {        
	 deleteFilesByDirectory(context.getExternalCacheDir());        
	 }      
	 }     
	 /**      * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除      *       * @param filePath      */   
	 public static void cleanCustomCache(String filePath) {      
	 deleteFilesByDirectory(new File(filePath));     
	 }     
	 /**      * 清除本应用所有的数据      *       * @param context      * @param filepath      */   
	 public static void cleanApplicationData(Context context, String... filepath) {         
	 cleanInternalCache(context);        
	 cleanExternalCache(context);        
	 cleanDatabases(context);         
	 cleanSharedPreference(context);        
	 cleanFiles(context);          
	 for (String filePath : filepath) {         
	 cleanCustomCache(filePath);      
	 }     
	 }      
	 /**      * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理      *       * @param directory      */  
	 private static void deleteFilesByDirectory(File directory) {      
	 if (directory != null && directory.exists() && directory.isDirectory()) {       
	 for (File item : directory.listFiles()) {             
	 item.delete();             
	 }         
	 }     
	 }  

}
