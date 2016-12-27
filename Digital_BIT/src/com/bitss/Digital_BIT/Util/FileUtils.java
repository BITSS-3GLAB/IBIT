package com.bitss.Digital_BIT.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




import com.bitss.Digital_BIT.Tools.ImageTools;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class FileUtils {

	public static boolean isFileExists(String dir, String fileName) {
		File file = new File(dir + File.separator + fileName);
		return file.exists();
	}

	/**
	 * 将数据写入目录下的文件，如果文件不存在，会自动创建，如果文件存在，会覆盖。
	 * 
	 * @param bt
	 *            要存放的数据
	 * @param dir
	 *            数据存放到的目录
	 * @param fileName
	 *            数据存放到的文件名称
	 */
	public static boolean writeStreamToFile(byte[] bt, String dir,
			String fileName) {
		if (bt == null || dir == null || fileName == null)
			return false;

		File directory = new File(dir);

		if (!mkdirs(directory))
			return false;

		File target = new File(dir + File.separator + fileName);

		try {
			if (!target.exists()) {
				target.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(target);
			out.write(bt);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				target.delete();
			} catch (Exception ex) {
			}
			return false;
		}
	}

	public static boolean writeStreamToFile(InputStream is, String dir,
			String fileName) {
		try {
			return writeStreamToFile(ByteToInputStream.stream2byte(is), dir,
					fileName);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 如果目录不存在，创建目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean mkdirs(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			return dir.mkdirs();
		}
		return true;
	}

	/**
	 * 使用FileInputStream打开指定的文件
	 * 
	 * @param dir
	 *            文件的目录
	 * @param fileName
	 *            文件的名称
	 * @return
	 */

	public static FileInputStream openInputStream(String dir, String fileName) {
		return openInputStream(dir + File.separator + fileName);
	}

	/**
	 * 使用FileInputStream打开指定的文件
	 * 
	 * @param filePath
	 *            文件的绝对路径
	 * @return
	 */
	public static FileInputStream openInputStream(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获得文件的扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null)
			return null;
		int pos = fileName.lastIndexOf(".");

		if (pos > -1 && pos < fileName.length()) {
			return fileName.substring(pos + 1);
		}

		return "";
	}

	/**
	 * 获得url的文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(String url) {
		if (url == null)
			return null;
		int pos = url.lastIndexOf("/");

		if (pos > -1 && pos < url.length()) {
			return url.substring(pos + 1);
		}

		return "";
	}

	/**
	 * 计算文件夹所占空间的字节数
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirBytes(File dir) {
		if (!dir.exists() || dir.listFiles() == null) {
			return 0;
		}

		long size = 0;
		File fileList[] = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size += getDirBytes(fileList[i]);
			} else {
				size += fileList[i].length();
			}
		}

		return size;
	}

	/**
	 * 将字节数转换成所占空间大小的字符串
	 * 
	 * @param size
	 * @return
	 */
	public static String convertDirByte2String(long size) {
		if (size == 0) {
			return "0B";
		}

		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (size < 1024) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 删除整个目录和子目录下的所有文件
	 * 
	 * @param dir
	 */
	public static void deleteDir(String dir) {
		File root = new File(dir);
		if (root.exists() && root.isDirectory()) {
			if (root.listFiles().length == 0) {
				// 若目录下没有文件则直接删除
				root.delete();
			} else {
				// 若有则把文件放进数组，并判断是否有子目录
				File fileList[] = root.listFiles();
				int i = root.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (fileList[j].isDirectory()) {
						// 递归删除子目录下的所有文件
						deleteDir(fileList[j].getAbsolutePath());
					}
					fileList[j].delete();
				}
			}
		}
	}

	/**
	 * 将一个bitmap写入本地文件,quality为80
	 */
	public static File bitmap2file(Bitmap bitmap, int quality, int number) {
		// 得到外部存储卡的路径
		String path = FileUtils.getImageCachePath().toString();
		// .png是将要存储的图片的名称

		
		// SimpleDateFormat dateFormat = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		// String hehe = dateFormat.format(now);
	
		File file = new File(path, number + "logo.jpg");
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

	public static final String MAIN_SOFT_FOLDER_NAME = "Digital_secondHand";
	public static final String CACHE_FOLDER_NAME = "Cache";

	// 获得图像缓存的目录。
	public static String getImageCachePath() {
		if (!isExistSDCard()) {
			return "";
		}

		String sdRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String result = sdRoot + "/" + MAIN_SOFT_FOLDER_NAME + "/"
				+ CACHE_FOLDER_NAME;
		if (new File(result).exists() && new File(result).isDirectory()) {
			return result;
		} else {
			return sdRoot;
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

}
