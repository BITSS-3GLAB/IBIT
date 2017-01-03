package com.bitss.Digital_BIT.SecondHandMarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;

public class ChooseClassifyActivity extends CustomBaseActivity{
	
	private ListView lv_all ;
	private String[] aStrings = {"全部","代步工具","书籍资料","服装鞋包","数码专区","体育用品","爱心赠送","音乐乐器","票券","其他"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);
		mTvNaviTitle.setText("所属类别");
		lv_all = (ListView)findViewById(R.id.lv_classify);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,aStrings);
		lv_all.setAdapter(adapter);
		lv_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChooseClassifyActivity.this,
						SecondHandPublishActivity.class);
				intent.putExtra("content", aStrings[arg2]);
				intent.putExtra("position", arg2);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

}
