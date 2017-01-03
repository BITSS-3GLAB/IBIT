package com.bitss.Digital_BIT.BitKnow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.R.drawable;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.FileUtils;

@SuppressWarnings("deprecation")
public class KnowAddTopicActivity extends CustomBaseActivity {

	private GridView gridView;
	private TextView addBtn;
	private EditText topicDiscribe;
	private TopicAdapter topicGrid;
	private ArrayList<String> picUri = new ArrayList<String>();
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private String qDiscribe;
	private String sentTopic = "";
	private static final int POST_SUCCESS = 10024;
	private static final int POST_FAILURE = 10025;
	private HttpPost post;
	private HttpPost cloudpost;
	private static final String TARGETURL = "http://10.1.112.218:8080/Digital_BIT_Server/GetNewQuestionInfo";
	private static final String CLOUDTARGETURL = Constants.BITKNOWTEST_CLOUDSERVER_STRING+"GetNewQuestionInfo";
	private SharedPreferences settings;
	private String phone;
	public static final int RESULT_OVER = 100;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_know_add_topic);

		intiActionBar();
		addGridView();
		getMyIntent();// 得到传来的问题描述和图片uri
		listens();

	}

	/**
	 * 设置actionBar
	 */
	private void intiActionBar() {
		mTvNaviTitle.setText(R.string.bitknow_add_topic);

		mTvRight.setText(R.string.submit);

		mTvRight.setVisibility(View.VISIBLE);
	}

	/**
	 * 得到intent传过来的东西，包括问题描述和图片的uri
	 */
	private void getMyIntent() {
		Bundle bundle = getIntent().getExtras();
		qDiscribe = bundle.getString(KnowDiscribeQActivity.INTENT_DISCRIBEQ);
		picUri = bundle.getStringArrayList(KnowDiscribeQActivity.INTENT_BITMAP);
		getSentBitmap();
	}

	/**
	 * 设置所有监听
	 */
	@SuppressLint("NewApi")
	private void listens() {

		addBtn = (TextView) findViewById(R.id.add);
		topicDiscribe = (EditText) findViewById(R.id.add_topic);

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (topicDiscribe.getText().toString().trim().equals(""))
					return;

				String topic = topicDiscribe.getText().toString().trim();
				topicGrid.addTopic(topic);
				topicDiscribe.setText("");
			}
		});

		// 发送问题的监听
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ArrayList<String> allTopic = topicGrid.getAllTopic();
				if (allTopic.size() < 1) {
					Toast.makeText(KnowAddTopicActivity.this, "请选择至少一个标签",
							Toast.LENGTH_SHORT).show();
					;
					return;
				}

				getSentTopic(allTopic);// 得到标签
				// 得到电话
				settings = getSharedPreferences("User", 0);
				phone = settings.getString(Constants.USER_PHONE, "");
				Log.e("net_data", "url = " + TARGETURL);
				if (post == null) {
					post = new HttpPost(TARGETURL);
				}
				if (cloudpost == null) {
					cloudpost = new HttpPost(CLOUDTARGETURL);
				}
				new Thread(new postLostFound()).start();
			}
		});

	}

	/**
	 * 添加gridview
	 */
	private void addGridView() {
		gridView = (GridView) findViewById(R.id.gridview);

		topicGrid = new TopicAdapter(this);
		gridView.setAdapter(topicGrid);
	}

	/**
	 * 把uri转化成bitmap
	 */
	private void getSentBitmap() {
		for (String p : picUri) {
			Uri uri = Uri.parse(p);
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bitmaps.add(bitmap);
		}
	}

	/**
	 * 得到标签的格式
	 */
	void getSentTopic(ArrayList<String> allTopic) {

		for (String p : allTopic) {
			sentTopic += p + "|";
		}
		sentTopic = sentTopic.substring(0, sentTopic.length() - 1);
	}

	/**
	 * 
	 * @author 林蔚澜 
	 * 这是网络连接类，来自失物招领的代码，做了少量更改
	 */
	private class postLostFound implements Runnable {

		@Override
		public void run() {

			Message message = new Message();
			MultipartEntity entity = new MultipartEntity();
			try {
				// 构建参数
				for (int i = 0; i < bitmaps.size(); i++) {

					File file = FileUtils.bitmap2file(bitmaps.get(i), 80, i);
					entity.addPart("file" + i + ".jpg", new FileBody(file));

				}

				entity.addPart("phone",
						new StringBody(phone, Charset.forName(HTTP.UTF_8)));
				entity.addPart("text",
						new StringBody(qDiscribe, Charset.forName(HTTP.UTF_8)));
				entity.addPart("tag",
						new StringBody(sentTopic, Charset.forName(HTTP.UTF_8)));

				post.setEntity(entity);
				cloudpost.setEntity(entity);

				// 请求服务器
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
				HttpConnectionParams.setSoTimeout(httpParams, 30000);

				HttpResponse cloudresponse = new DefaultHttpClient(httpParams)
						.execute(cloudpost);
				if (cloudresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					message.what = POST_SUCCESS;
					String strResult = EntityUtils.toString(cloudresponse
							.getEntity());
					message.obj = strResult;

				} else {
					HttpResponse response = new DefaultHttpClient(httpParams)
					.execute(post);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						message.what = POST_SUCCESS;
						String strResult = EntityUtils.toString(response
								.getEntity());
						message.obj = strResult;

					} else {
						message.what = POST_FAILURE;
					}
				}

			} catch (Exception e) {
				message.what = POST_FAILURE;
			} finally {
				handler.sendMessage(message);
			}
		}
	}

	/**
	 * 网络出错处理
	 */

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case POST_FAILURE: {
				Toast.makeText(KnowAddTopicActivity.this, "网络环境不好╯﹏╰",
						Toast.LENGTH_SHORT).show();
				break;
			}
			case POST_SUCCESS: {
				Intent intent = getIntent();
				setResult(RESULT_OVER, intent);
				KnowAddTopicActivity.this.finish();
			}

			}
		}
	};
}

/**
 * 
 * @author 林蔚澜 gridView的适配器
 */
class TopicAdapter extends BaseAdapter {

	private ArrayList<Map<String, String>> value = new ArrayList<Map<String, String>>();
	private final String BLUE = "blue", ORANGE = "orange", PINK = "pink",
			GREEN = "green", YELLOW = "yellow", GREY = "grey";

	private final String[] otherColor = { "blue", "orange", "pink", "green",
			"yellow" };
	private final String CHOSEN = "chosen", UNCHOSEN = "unchosen";
	private LayoutInflater inflater;

	TopicAdapter(Context context) {
		super();
		inflater = LayoutInflater.from(context);
		iniFirstSix();

	}

	/**
	 * 最初的几个默认标签
	 */
	private void iniFirstSix() {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("topic", "学习");
		tmp.put("color", BLUE);
		tmp.put("chose", CHOSEN);
		value.add(tmp);

		tmp = new HashMap<String, String>();
		tmp.put("topic", "生活");
		tmp.put("color", ORANGE);
		tmp.put("chose", UNCHOSEN);
		value.add(tmp);

		tmp = new HashMap<String, String>();
		tmp.put("topic", "情感");
		tmp.put("color", PINK);
		tmp.put("chose", UNCHOSEN);
		value.add(tmp);

		tmp = new HashMap<String, String>();
		tmp.put("topic", "就业");
		tmp.put("color", GREEN);
		tmp.put("chose", UNCHOSEN);
		value.add(tmp);

		tmp = new HashMap<String, String>();
		tmp.put("topic", "娱乐");
		tmp.put("color", YELLOW);
		tmp.put("chose", UNCHOSEN);
		value.add(tmp);

		tmp = new HashMap<String, String>();
		tmp.put("topic", "其他");
		tmp.put("color", GREY);
		tmp.put("chose", UNCHOSEN);
		value.add(tmp);
	}

	@Override
	public int getCount() {
		return value.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Map<String, String> item = value.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.bitknow_top_grid_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.topic);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText((String) item.get("topic"));

		// 得到背景图id
		Class<drawable> cls = R.drawable.class;
		Integer pic = null;
		try {
			pic = cls
					.getDeclaredField(
							"know_topic_" + item.get("color") + "_"
									+ item.get("chose")).getInt(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		// 显示图片
		viewHolder.image.setImageResource(pic);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (item.get("chose").equals("unchosen"))
					item.put("chose", CHOSEN);
				else
					item.put("chose", UNCHOSEN);
				TopicAdapter.this.notifyDataSetChanged();
			}
		});

		return convertView;

	}

	/**
	 * 添加一个标签
	 * 
	 * @param String
	 *            标签内容（颜色和是否可选自动分配）
	 */
	void addTopic(String topic) {

		int id = (value.size() - 6) % 5;

		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("topic", topic);
		tmp.put("color", otherColor[id]);
		tmp.put("chose", CHOSEN);
		value.add(tmp);
		TopicAdapter.this.notifyDataSetChanged();
	}

	/**
	 * 得到话题组成的ArrayList<String>
	 * 
	 * @return
	 */
	ArrayList<String> getAllTopic() {
		ArrayList<String> allTopic = new ArrayList<String>();
		for (Map<String, String> p : value) {
			if (p.get("chose").equals(CHOSEN))
				allTopic.add(p.get("topic"));
		}
		return allTopic;
	}

	class ViewHolder {
		public TextView title;
		public ImageView image;
	}

}
