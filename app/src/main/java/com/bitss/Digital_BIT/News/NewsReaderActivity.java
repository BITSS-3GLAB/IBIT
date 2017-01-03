package com.bitss.Digital_BIT.News;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.NewsContentData.NewsContent;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Tools.HttpPictureAsker;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.TextHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.DefaultFormBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.Parameter;

public class NewsReaderActivity extends CustomBaseActivity {

	private BaseApplication mApp;

	// --------评论相关------ß
	private RelativeLayout bottomLayout; // 控制评论和分享的显示,新闻没加载出来不显示ßß
	private ImageView ibtnShare, ibtnSave, ibtnComment, imgOK, imgNO;
	private TextView etxt;
	private EditText etxtName, etxtComment;
	private HttpCommentAsker httpAsker;
	private NewsCommentActivity nActivity;
	private Dialog dialog;

	private WebView newsWebview;
	private String htmlString;
	private NewsContentData newsContentData; // 新闻内容
	private Bundle bundle;
	private Context context;
	private Map<String, Bitmap> pictureMap = new HashMap<String, Bitmap>();

	private boolean isAlumni; // 是否来自校友网
	private String newsName; // 新闻类型的名字
	private int newsType = 1;
	private long newsID = 1;
	private String newsUrl;
	private int commNum = 3;
	private String newsTitle;
	private String newsTime;
	private String shareString = "";
	SharedPreferences settings;
	// ------------------------------附件下载--------------------------------
	private boolean isOpen = false; // 下载后是否打开

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backAction();
			return true;
		}
		super.onKeyDown(keyCode, event);
		return true;
	}

	@Override
	public void onDestroy() {
		if (newsContentData != null
				&& newsContentData.newsContentDataList.size() != 0) {
			new NewsFileAsker(context).writeContent(newsContentData, newsType,
					newsID);
		}
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsreader);

		mTvNaviTitle.setText(getResources().getString(R.string.news_school));

		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		context = this;
		mApp = (BaseApplication) context.getApplicationContext();
		settings = mApp.getPreferences();
		httpAsker = new HttpCommentAsker();
		// 新闻中分享和评论的按钮
		bottomLayout = (RelativeLayout) findViewById(R.id.linBottom);
		bottomLayout.setVisibility(View.INVISIBLE);
		ibtnComment = (ImageView) findViewById(R.id.ibtnComment);
		ibtnShare = (ImageView) findViewById(R.id.ibtnShare);
		etxt = (TextView) findViewById(R.id.etxtComment);

		// 获取从listview点击项传递过来的参数
		Intent intent = this.getIntent();
		bundle = intent.getExtras();
		isAlumni = bundle.getBoolean("isAlumni");
		newsName = bundle.getString("NewsName");
		newsType = bundle.getInt("NewsType");
		newsUrl = bundle.getString("Url");
		newsID = bundle.getLong("NewsID");
		pictureMap = (Map<String, Bitmap>) bundle.get("NewsPictureList");
		newsTitle = bundle.getString("NewsTitle");
		newsTime = bundle.getString("NewsTime");

		mTvNaviTitle.setText(newsName);

		// newsContentData = new NewsFileAsker(this).readContent(newsType,
		// newsID);
		// if (newsContentData == null
		// || newsContentData.newsContentDataList.size() == 0) {
		// Log.e("news reader", "get content from task");
		// new GetDataTask().execute();
		// } else {
		//
		// Log.e("news reader", "get content from cache");
		// setWebView();
		// bottomLayout.setVisibility(View.VISIBLE);
		// }
		setWebView();
		bottomLayout.setVisibility(View.VISIBLE);
		// 以下是评论中的按钮
		// 点击评论，跳出对话框让你输入评论内容
		etxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (settings.getString(Constants.KEY_EMAIL, "").equals("")) {
					Utils.haveNotLogin(NewsReaderActivity.this);
				} else {

					myselfDialog();
				}
			}
		});

		// 显示别人评论的内容
		ibtnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt("NewsType", newsType);
				bundle.putLong("NewsID", newsID);
				bundle.putString("NewsTitle", newsTitle);
				bundle.putString("NewsTime", newsTime);

				Intent intent = new Intent();
				intent = new Intent(NewsReaderActivity.this,
						NewsCommentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});

		ibtnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, newsTitle);
//				intent.putExtra(Intent.EXTRA_TEXT, newsTitle + " " + newsTime
//						+ "    " + newsContentData.newsUrl);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));

			}
		});
	}

	public void myselfDialog() {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.outputcomment);

		Window diaWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = diaWindow.getAttributes();
		diaWindow.setGravity(Gravity.BOTTOM);
		lp.width = LayoutParams.FILL_PARENT;
		diaWindow.setAttributes(lp);
		imgOK = (ImageView) dialog.findViewById(R.id.imgOK);
		imgNO = (ImageView) dialog.findViewById(R.id.imgNo);

		imgNO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		imgOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				etxtName = (EditText) dialog.findViewById(R.id.etxtName);
				etxtComment = (EditText) dialog
						.findViewById(R.id.etxtCommentDetail);
				String mTitle = settings.getString(Constants.NICK_NAME, "");
				String mBody = etxtComment.getText().toString().trim();

				if (mBody.length() == 0) {
					Toast.makeText(context,
							context.getString(R.string.warning_comment_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}

				putComment(mBody);

				// CommentData toMobileData;
				// toMobileData = new CommentData(mTitle, mBody, System
				// .currentTimeMillis() + "", "", newsID, newsType, 0);
				// // 将评论的信息发送到数据库进行存储
				// try {
				// int result = httpAsker.putCommentToMobile(toMobileData);
				//
				// if (result < 0) {
				// HttpErrorToast.Show(NewsReaderActivity.this);
				//
				// }
				// if (result == 0) {
				// Toast toast = Toast.makeText(context, context
				// .getString(R.string.insert_comment_success),
				// Toast.LENGTH_SHORT);
				// toast.setGravity(Gravity.BOTTOM, 0, 50);
				// toast.show();
				// }
				//
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				//
				// // 发表评论之后需要将最新的评论消息存储到文件当中
				// HttpCommentAsker httpAsker = new HttpCommentAsker();
				// LinkedList<CommentData> list = new LinkedList<CommentData>();
				// CommentFileAsker commentFileAsker = new CommentFileAsker(
				// context);
				// list = commentFileAsker.readFile(newsType, newsID);
				//
				// httpAsker.askForMobileList(list, false, commNum, newsType,
				// newsID, true);
				//
				// int end = 3;
				// if (list.size() < commNum)
				// end = list.size();
				// commentFileAsker.writeFile(
				// new LinkedList<CommentData>(list.subList(0, end)),
				// newsType, newsID);
				// // 每次将评论发表之后都要刷新一次得到最新的评论信息
				dialog.dismiss();

			}
		});
		dialog.setTitle("评论");

		dialog.show();

	}

	private void putComment(String content) {
		List<Parameter> parameters = new ArrayList<Parameter>(2);
		parameters.add(new Parameter("newsId", newsID));
		parameters.add(new Parameter("content", content));
		HttpRequest request = new HttpRequest.Builder()
				.url("/news/front/newsComment")
				.bodyBuilder(new DefaultFormBodyBuilder()).addList(parameters)
				.build();
		HttpClient.getInstance(context).put(request.getUrl(),
				request.getBody(), new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 201:
							Utils.showToast(mApp, "发表评论成功");
							break;
						case 401:
							new ReLogin(context).showDialog();
							break;
						default:
							Utils.showToast(mApp, "发表评论失败");
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						e.printStackTrace();
						Utils.showToast(mApp, "发表评论失败");
					}
				});
	}
	
	// private class GetDataTask extends AsyncTask<Void, Void, Void> {
	// @Override
	// protected Void doInBackground(Void... params) {
	// newsContentData = new HttpNewsAsker().askForContent(isAlumni,
	// newsType, newsID);
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// if (newsContentData != null) {
	// // 新闻内容不空才进行数据显示
	// setWebView();
	//
	// bottomLayout.setVisibility(View.VISIBLE);
	// } else {
	// // 弹出提示框
	// HttpErrorToast.Show(context);
	// }
	// }
	// }

	private void setWebView() {
		newsWebview = (WebView) findViewById(R.id.newsdetailwebview);
		newsWebview.getSettings().setJavaScriptEnabled(true);
		// newsWebview.addJavascriptInterface(this, htmlString);
		final String mimeType = "text/html";
		final String encoding = "utf-8";

		// 页面html布局
		// SetHtmlView();

		// 点击事件，通过传入的url来判断
		newsWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// 电话或者邮件
				if (url.startsWith("tel:") || url.startsWith("mailto:")) {
					return false;
				}

				// 获取文件后缀名
				String fileEnding = url.substring(url.lastIndexOf(".") + 1);

				if (fileEnding.equals("jpg") || fileEnding.equals("png")) {
					MyDialog dialog = new MyDialog(context, R.style.dialog, url);
					dialog.show();
				} else {
					showDownLoadDialog(url);
				}
				return true;
			}
		});
		// newsWebview.loadDataWithBaseURL(null, htmlString, mimeType, encoding,
		// null);
		newsWebview.loadUrl(newsUrl);
	}

	/**
	 * 下载附加时弹出的对话框
	 * */
	public void showDownLoadDialog(final String url) {
		// 获取文件名(包括后缀)
		String fileName = url.substring(url.lastIndexOf("/") + 1);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				NewsReaderActivity.this);

		alertDialog.setMessage(getString(R.string.downLoad_ask) + fileName);
		// 确定要下载
		alertDialog.setPositiveButton(getString(R.string.downLoad_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downLoadFile(url);
						dialog.dismiss();
					}
				});
		// 下载并打开
		alertDialog.setNeutralButton(getString(R.string.downLoad_and_open),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isOpen = true;
						downLoadFile(url);
						dialog.dismiss();
					}
				});
		// 取消
		alertDialog.setNegativeButton(getString(R.string.downLoad_cancle),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	/**
	 * 检测sd是否存在以及是否有写权限
	 * 
	 * @param url
	 *            :需要下载的文件的地址
	 * */
	public void downLoadFile(String url) {
		// 先判断sd是否有读写权限
		String sdState = Environment.getExternalStorageState();

		if (sdState.equals(Environment.MEDIA_MOUNTED)) { // 此时sd卡存在且拥有写权限
			String sdPath = null;
			sdPath = Environment.getExternalStorageDirectory()
					+ "/iBIT_DownLoad/";

			File f = new File(sdPath);
			if (!f.exists()) {
				f.mkdir();
			}
			// 下载url对应的附件
			new DownLoadFile().execute(url);

		} else { // warn not sd's right
			Toast.makeText(NewsReaderActivity.this,
					getString(R.string.downLoad_no_sd), Toast.LENGTH_LONG)
					.show();
		}

	}

	/**
	 * 开新的线程去下载附件
	 * */
	class DownLoadFile extends AsyncTask<String, Void, Integer> {

		ProgressDialog mypDialog; // 下载进度条
		boolean startTask = true; // 是否取消文件下载
		/**
		 * 文件下载后在sd卡的路径
		 * */
		String filePath;

		// 设置下载进度条
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mypDialog = new ProgressDialog(NewsReaderActivity.this);
			// 设置进度条风格，风格为圆形，旋转的
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setTitle(getString(R.string.downLoad_wait));
			mypDialog.setMessage(getString(R.string.downLoad_now));
			mypDialog.setIndeterminate(false);
			// 设置ProgressDialog 的进度条是否不明确
			mypDialog.setCancelable(true);
			// 设置ProgressDialog 是否可以按退回按键取消
			mypDialog.setButton(getString(R.string.downLoad_cancle),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							startTask = false;
						}
					});
			mypDialog.show();
		}

		// 访问服务器下载附件
		@Override
		protected Integer doInBackground(String... params) {

			String _urlStr = params[0].toString();
			String _dirName = Environment.getExternalStorageDirectory()
					+ "/iBIT_Download/";

			// 获取文件名
			String filename = _urlStr.substring(_urlStr.lastIndexOf("/") + 1);
			filename = _dirName + filename;

			filePath = filename;

			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}

			try {
				URL url = new URL(_urlStr);
				URLConnection con = url.openConnection();
				// 设置连接超时
				con.setConnectTimeout(30000);

				InputStream is = con.getInputStream();
				byte[] bs = new byte[1024];
				int len;
				OutputStream os = new FileOutputStream(filename);
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				os.close();
				is.close();
			} catch (Exception e) {
				return 0;
			}

			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			mypDialog.dismiss();
			if (result == 1) { // 下载成功
				if (startTask) {
					String dirName = Environment.getExternalStorageDirectory()
							+ "/iBIT_Download/";
					Toast.makeText(NewsReaderActivity.this,
							getString(R.string.downLoad_finish) + dirName,
							Toast.LENGTH_LONG).show();

					// 下载后要求打开附件
					if (isOpen) {
						isOpen = false;
						Intent intent = openFile(filePath);
						if (intent != null) {
							try {
								startActivity(intent);
							} catch (Exception e) {
								Toast.makeText(NewsReaderActivity.this,
										getString(R.string.downLoad_not_open),
										Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(NewsReaderActivity.this,
									getString(R.string.downLoad_not_open),
									Toast.LENGTH_LONG).show();
						}
					}
				}
			} else {
				Toast.makeText(NewsReaderActivity.this,
						getString(R.string.downLoad_timeout), Toast.LENGTH_LONG)
						.show();
			}

		}
	}

	/**
	 * 打开指定路径下的文件
	 * 
	 * @param filePath
	 *            :附件在sd中的路径
	 * */
	public Intent openFile(String filePath) {
		Intent intent = null;

		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

		if (checkEndsWithInStringArray(fileName,
				getResources().getStringArray(R.array.fileEndingWord))) {
			// 用word打开
			intent = DownLoadingFile.getWordFileIntent(filePath);
			return intent;

		} else if (checkEndsWithInStringArray(fileName, getResources()
				.getStringArray(R.array.fileEndingExcel))) {
			// excle打开
			intent = DownLoadingFile.getExcelFileIntent(filePath);
			return intent;

		} else if (checkEndsWithInStringArray(fileName, getResources()
				.getStringArray(R.array.fileEndingPdf))) {
			// pdf打开
			intent = DownLoadingFile.getPdfFileIntent(filePath);
			return intent;

		} else {
			// 此时intent为空，表明不能打开此附件;
			return intent;
		}
	}

	/**
	 * @param checkItsEnd
	 *            :文件名(包括后缀)
	 * @param fileEndings
	 *            ：文件名后缀数组
	 * 
	 * */
	public boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	class MyDialog extends Dialog {
		Dialog dialog;
		ImageView pictureImage;
		String url;
		Button back;

		public MyDialog(Context context, int theme, String _url) {
			super(context, theme);
			url = _url;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			this.setContentView(R.layout.newspicturedialog);
			dialog = this;
			back = (Button) findViewById(R.id.newspicturecancelbutton);
			pictureImage = (ImageView) findViewById(R.id.newspicturedialogview);
			Bitmap tmpMap = pictureMap.get(url);
			if (tmpMap != null) {
				pictureImage.setImageBitmap(tmpMap);
			} else
				new GetPictureTask().execute(url);
			back.setOnClickListener(new android.view.View.OnClickListener() {
				public void onClick(View v) {
					dialog.cancel();
				}
			});

		}

		private class GetPictureTask extends AsyncTask<String, Void, Bitmap> {
			@Override
			protected Bitmap doInBackground(String... params) {
				HttpPictureAsker httpasker = new HttpPictureAsker();
				Bitmap ans = httpasker.getPicture(params[0].toString());
				return ans;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					if (pictureImage != null) {
						pictureImage.setImageBitmap(result);
						pictureMap.put(url, result);
					}
				} else {
					HttpErrorToast.Show(context);
				}
				super.onPostExecute(result);
			}
		}
	}

	private void SetHtmlView() {

		// 头部固定html
		String headString = "<html><head>" + "<style type='text/css'>"
				+ "div.content{padding:15px;word-break:break-all;}"
				+ "p{line-height:25px;}" + "img{padding:10px;}"
				+ "h1{text-align:center;font-size:20px;}"
				+ "p.releasetime{text-align:center;color:#31C5AB;}";

		// 图片正常显示右边
		String normalString = "img{"
				+ "float:right;"
				+ "max-width: 130px; max-height: 130px; zoom: expression( function(elm) { if (elm.width > 100 ||elm.height > 100 ){ if (elm.width>elm.height) { elm.width=100; } else{ elm.height=100; } } elm.style.zoom = '1'; }(this) ); }"
				+ "</style>" + "</head>" + "<body>" + "<div class='content'>";

		// 图片特殊显示在中间
		String specialString = "img{"
				+ "max-width: 200px; max-height: 200px; zoom: expression( function(elm) { if (elm.width > 100 ||elm.height > 100 ){ if (elm.width>elm.height) { elm.width=100; } else{ elm.height=100; } } elm.style.zoom = '1'; }(this) ); }"
				+ "</style>" + "</head>" + "<body>"
				+ "<div style='text-align:center' class='content'>";

		// 除了head之外的html部分，把后续部分拼接
		String exceptHeadString = "";

		String h1String = "<h1 class=\"newstitle\">";
		h1String += bundle.getString("NewsTitle");
		h1String += "</h1>";
		exceptHeadString += h1String;

		String releaseTimeString = "<p class=\"releasetime\">";
		releaseTimeString += "发布时间：" + bundle.getString("NewsTime");
		releaseTimeString += "</p><hr width=100% size=1 color=#31C5AB style='FILTER': alpha(opacity=100,finishopacity=0,style=3)/>";
		exceptHeadString += releaseTimeString;

		// 统计文字段落、图片，用于最后的布局
		int count_content = 0, count_img = 0;
		// 拼接所有附件的字符串
		String downLoadFileHtml = "";

		List<NewsContent> newsContentDataList = newsContentData.newsContentDataList;
		for (int i = 0; i < newsContentDataList.size(); ++i) {
			if (newsContentDataList.get(i).type == 0) {
				// 文章
				count_content++;

				String txtContentWithPTag = "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				txtContentWithPTag += newsContentDataList.get(i).detail;
				txtContentWithPTag += "</p>";
				exceptHeadString += txtContentWithPTag;
				shareString += newsContentDataList.get(i).detail;

			} else if (newsContentDataList.get(i).type == 1) {
				// 图片
				count_img++;

				String imgUrlWithImgTag = "<A href=\"";
				imgUrlWithImgTag += newsContentDataList.get(i).detail;
				imgUrlWithImgTag += "\"><img src=\"";
				imgUrlWithImgTag += newsContentDataList.get(i).detail;
				imgUrlWithImgTag += "\"/> </a>";
				exceptHeadString += imgUrlWithImgTag;

			} else if (newsContentDataList.get(i).type == 2) {
				// 附件URL
				String downLaodFile = "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				downLaodFile += "<A href=\""
						+ newsContentDataList.get(i).detail + "\">";
				downLoadFileHtml += downLaodFile;
			} else if (newsContentDataList.get(i).type == 3) {
				// 附件名称
				String filename = newsContentDataList.get(i).detail;
				downLoadFileHtml += filename + "</A>";
			}

		}

		// 确定头部html的布局
		if (count_content == 0) { // 中间布局
			htmlString = headString + specialString + exceptHeadString;
		} else if (count_content == 1) { // 分情况
			if (count_img == 0) {
				htmlString = headString + normalString + exceptHeadString;
			} else {
				htmlString = headString + specialString + exceptHeadString;
			}
		} else {
			htmlString = headString + normalString + exceptHeadString;
		}

		// 确定附件下载的布局
		if (!downLoadFileHtml.equals("")) {
			// 判断附件上方是否需要添加分割线
			if (count_content == 0 && count_img == 0) { // 此时不需要添加分割线
				htmlString += downLoadFileHtml;
			} else {
				htmlString += "</p><hr width=100% size=1 color=#cccccc style='FILTER': alpha(opacity=100,finishopacity=0,style=3)/>";
				htmlString += downLoadFileHtml;
			}
		}
	}
}