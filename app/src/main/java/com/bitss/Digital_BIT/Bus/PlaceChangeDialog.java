package com.bitss.Digital_BIT.Bus;

import com.bitss.Digital_BIT.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PlaceChangeDialog extends Dialog {

	private RadioGroup startGroup;
	private RadioGroup endGroup;
	private Button setButton;
	private Button cancelButton;

	public int start_position;
	public int end_position;

	int[] startID = new int[4];
	int[] aimID = new int[4];

	private MyListener listener;

	// 自定义的监听器
	public interface MyListener {
        /**
         * 回调函数,用于给BusActivity传递参数
         */
        public void toMainActivity(int[] position);
    }


	public PlaceChangeDialog(Context context, int start_position,
			int end_position, MyListener listener) {
		super(context);

		this.start_position = start_position;
		this.end_position = end_position;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.busplacechoose);

		init();
		setListener();
	}

	public void init() {
		startGroup = (RadioGroup) findViewById(R.id.busstartradiogroup);
		endGroup = (RadioGroup) findViewById(R.id.busaimradiogroup);

		startID[0] = R.id.busstartplace1;
		startID[1] = R.id.busstartplace2;
		startID[2] = R.id.busstartplace3;
		startID[3] = R.id.busstartplace4;
		aimID[0] = R.id.busaimplace1;
		aimID[1] = R.id.busaimplace2;
		aimID[2] = R.id.busaimplace3;
		aimID[3] = R.id.busaimplace4;

		startGroup.check(startID[start_position]);
		endGroup.check(aimID[end_position]);

		setButton = (Button) findViewById(R.id.busplaceset);
		cancelButton = (Button) findViewById(R.id.busplacecancel);
	}

	public void setListener() {
		startGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int id = 0;
				for (int i = 0; i < 4; i++) {
					if (startID[i] == checkedId) {
						id = i;
						break;
					}
				}
				if (id == end_position) {
					end_position = start_position;
					endGroup.check(aimID[end_position]);
				}
				start_position = id;
			}
		});

		endGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int id = 0;
				for (int i = 0; i < 4; i++) {
					if (aimID[i] == checkedId) {
						id = i;
						break;
					}
				}
				if (id == start_position) {
					start_position = end_position;
					startGroup.check(startID[start_position]);
				}
				end_position = id;
			}
		});

		setButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlaceChangeDialog.this.dismiss();
				// 返回修改后的起点--终点位置
				int[] position = new int[2];
				position[0] = start_position;
				position[1] = end_position;
				listener.toMainActivity(position);
			}
		});
		cancelButton
				.setOnClickListener(new android.view.View.OnClickListener() {
					public void onClick(View v) {
						PlaceChangeDialog.this.dismiss();
					}
				});
	}

}
