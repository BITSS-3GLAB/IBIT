package com.bitss.Digital_BIT.Admission;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;

public class CollegeMenuFragment extends Fragment implements
		OnItemClickListener {

	private Context context;

	private ListView mListView;
	private AdmissionMenuAdapter adapter;

	private String[] collegeNameArray;
	private String[] collegeFileArray;
	private List<CollegeModel> collegeInfoList = new ArrayList<CollegeModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.context = getActivity();

		View view = inflater.inflate(R.layout.admission_menu_fragment, null);
		mListView = (ListView) view.findViewById(R.id.college_listview);
		mListView.setOnItemClickListener(this);

		// set up menu data
		collegeNameArray = getResources().getStringArray(
				R.array.array_college_name);
		collegeFileArray = getResources().getStringArray(
				R.array.array_college_file);
		for (int i = 0; i < collegeFileArray.length; i++) {
			CollegeModel model = new CollegeModel(collegeNameArray[i],
					collegeFileArray[i]);
			collegeInfoList.add(model);
		}
		adapter = new AdmissionMenuAdapter();
		mListView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switchFragment(collegeNameArray[(int) id], collegeFileArray[(int) id]);
	}

	private void switchFragment(String name, String file) {
		if (context == null)
			return;

		if (context instanceof CollegeSlidingActivity) {
			((CollegeSlidingActivity) context).switchContent(name, file);
		}
	}

	public class AdmissionMenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return collegeInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_menu, parent, false);
			TextView name = (TextView) convertView.findViewById(R.id.tv_menu);
			name.setText(collegeInfoList.get(position).collegeName);
			return convertView;
		}

	}
}
