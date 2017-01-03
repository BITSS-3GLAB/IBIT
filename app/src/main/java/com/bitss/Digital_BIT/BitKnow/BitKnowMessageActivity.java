package com.bitss.Digital_BIT.BitKnow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.BitKnow.BitKnowNetworkHandler;
import com.bitss.Digital_BIT.Phone.InitialDatabase;
import com.bitss.Digital_BIT.Util.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class BitKnowMessageActivity extends Activity implements OnClickListener {
	private SharedPreferences settings;
	private String phone;
	private String phone1 = "15201614601";
	private Context myActivity;
	
	private PullToRefreshListView pullToRefreshListView;
	private BitKnowMessageAdapter bitKnowMessageAdapter;
	private BitKnowHttpConnect mConnection;
	
	private static List<BitKnowMessageData> mDatas = new ArrayList<BitKnowMessageData>();
	private static final int DoRefresh = 1;
	private static final int OnMore = 0;
	
	private int number = 0;
	private boolean mIsRefresh = false;
	
	private ImageView backToMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitknow_message);
		
		init();
		
		try{
			getData(DoRefresh);
		}catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		listeners();
	}

	private void init()
	{
		settings = getSharedPreferences("User", 0);
		phone = settings.getString(Constants.USER_PHONE, "");
		 
		backToMain = (ImageView)findViewById(R.id.message_back);
		
		mConnection = new BitKnowHttpConnect(this);
		mConnection.setTimeOut(11000);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.bitknow_message_pull_to_refresh_listview);
		pullToRefreshListView.setShowIndicator(false);
		pullToRefreshListView.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				try{
					doRefresh();
				}catch(UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				try{
					onMore();
				}catch(UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
		});
		
		myActivity = this;
	}
	
	private void doRefresh() throws UnsupportedEncodingException
	{
		if(!mIsRefresh)
		{
			mIsRefresh = true;
			getData(DoRefresh);
		}else{
			return ;
		}
	}
	
	private void onMore() throws UnsupportedEncodingException
	{
		if(!mIsRefresh)
		{
			mIsRefresh = true;
			getData(OnMore);
		}else{
			return ;
		}
	}
	
	private void getData(int refresh) throws UnsupportedEncodingException
	{
		if(refresh == DoRefresh)
		{
			JSONObject json = new JSONObject();
			try{
				json.put("phone",phone1);
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING + "SendAnswerByPhone", Constants.BITKNOWTEST_CLOUDSERVER_STRING+"SendAnswerByPhone"
					,json, new BitKnowNetworkHandler() {
				
				@Override
				public void onSuccess(String str) {
					try{
						JSONObject json = new JSONObject(str);
					    System.out.println(str);
					    Log.v("huiqin", str);
					    boolean isSuccess = json.getBoolean("success");
					    if(isSuccess)
					    {
					    	mDatas.clear();
					    	JSONObject result = json.getJSONObject("result");
					    	JSONArray records = result.getJSONArray("answerList");
					    	
					    	for(int i = 0;i < records.length();i ++)
					    	{
					    		JSONObject item = records.getJSONObject(i);
					    		BitKnowMessageData data = new BitKnowMessageData(item);
					    		mDatas.add(data);
					    	}
					    	
					    	if(bitKnowMessageAdapter == null)
					    	{
					    		bitKnowMessageAdapter = new BitKnowMessageAdapter(myActivity, mDatas);
					    		pullToRefreshListView.setAdapter(bitKnowMessageAdapter);
					    	}else 
					    	{
								bitKnowMessageAdapter.notifyDataSetChanged();
							}
					    }
					}catch(JSONException e)
					{
						e.printStackTrace();
					}finally
					{
						number = 0;
						pullToRefreshListView.onRefreshComplete();
						mIsRefresh = false;
					}
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					pullToRefreshListView.onRefreshComplete();
					Toast.makeText(myActivity, "网络异常", Toast.LENGTH_SHORT).show();;
				}
			});
		}else 
		{
			JSONObject json = new JSONObject();
			try {
				json.put("phone", phone1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING + "SendAnswerByPhone", Constants.BITKNOWTEST_CLOUDSERVER_STRING+"SendAnswerByPhone",
					json, new BitKnowNetworkHandler() {
				
				@Override
				public void onSuccess(String str) {
					try{
						JSONObject json = new JSONObject(str);
						System.out.print(str);
						boolean isSuccess = json.getBoolean("success");
						if(isSuccess)
						{
							JSONObject result = json.getJSONObject("result");
							JSONArray records = result.getJSONArray("answerList");
							for(int i = 0;i < records.length();i ++)
							{
								JSONObject item = records.getJSONObject(i);
								BitKnowMessageData data = new BitKnowMessageData(item);
								mDatas.add(data);
							}
						}
						bitKnowMessageAdapter.notifyDataSetChanged();
						Toast.makeText(myActivity, "成功加载更多数据", Toast.LENGTH_SHORT).show();;
					}catch(JSONException e){
						e.printStackTrace();
					}finally{
						pullToRefreshListView.onRefreshComplete();
						mIsRefresh = false;
					}
				}
				
				@Override
				public void onFailure() {
					pullToRefreshListView.onRefreshComplete();
					Toast.makeText(myActivity, "网络异常", Toast.LENGTH_SHORT).show();
					mIsRefresh = false;
				}
			});
		}
	}
	
	private void listeners()
	{
		backToMain.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.message_back:
//			Intent bitknowMianIntent = new Intent();
//			bitknowMianIntent.setClass(this, BitKnowMainActivity.class);
//			startActivity(bitknowMianIntent);
			finish();
			break;
		default:
			break;
		}
	} 

}
