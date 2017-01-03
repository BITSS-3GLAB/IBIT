package com.bitss.Digital_BIT.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Guide.GuideActivity;

/**
 * @author huangqian 2013-8-10
 * 
 *         程序第一次启动要把数据写入数据库
 * */
public class SetupDBUtil {

	private Context context;

	public SetupDBUtil(Context context) {
		this.context = context;

		if (isNeedToInsert()) {
			new InsertDataTask().execute();
		} else {
			setupComplete();
		}
	}

	/**
	 * 每次版本升级都要对清理原来数据
	 * */
	public boolean isNeedToInsert() {
		boolean needInsert = false;

		SharedPreferences uiState = context.getSharedPreferences("cacheVersion",
		        context.MODE_PRIVATE);
		int version = uiState.getInt("cacheVersion", 1);

		if (version < 5) {
			needInsert = true;

			// 缓存删除成功
			File f = new File("/data/data/com.bitss.Digital_BIT/files");
			if (f.exists()) {

				File[] file = f.listFiles();
				for (int i = 0; i < file.length; i++) {
					file[i].delete();
				}
			}

			// 数据库更新
			String dbDirPath = "/data/data/com.bitss.Digital_BIT/databases";
			File dbDir = new File(dbDirPath);
			if (dbDir.exists())
				dbDir.delete();

			SharedPreferences.Editor editor = uiState.edit();
			editor.putInt("cacheVersion", 5);
			editor.commit();
		}
		return needInsert;
	}

	private void addDatabase(int id, String name) {
		try {
			String dbDirPath = "/data/data/com.bitss.Digital_BIT/databases";
			File dbDir = new File(dbDirPath);
			if (!dbDir.exists())
				dbDir.mkdir();
			// 打开静态数据库文件的输入流
			InputStream is = context.getResources().openRawResource(id);
			// 打开目标数据库文件的输出流
			FileOutputStream os = new FileOutputStream(dbDirPath + "/" + name);
			byte[] buffer = new byte[1024];
			int count = 0;
			// 将静态数据库文件拷贝到目的地
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			is.close();
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("SetupDBUtil", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("SetupDBUtil", e.getMessage());
		}
	}

	// 后台执行加载函数
	private class InsertDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			addDatabase(R.raw.phone, "phone.db");
			addDatabase(R.raw.bus, "bus.db");
			addDatabase(R.raw.admission, "admission.db");

			Log.e("SetupDBUtil", "insert finish");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setupComplete();
		}

	}

	public void setupComplete() {
		if (context != null) {
			((GuideActivity) context).setScrollStatus(true);
		}
	}
}
