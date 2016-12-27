package com.bitss.Digital_BIT.BitKnow;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.BitKnow.BitKnowNetworkHandler;
import com.bitss.Digital_BIT.Util.Constants;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BitKnowAnswerAdapter extends BaseAdapter {
	private Context mcontext;
	private static List<BitKnowAnswerData> mData;
	private BitKonwDetailActivity activity;
	private BitKnowHttpConnect myConnect;
	private int questionid;
	private String phone;
	private String myphone;
	private AnswerDeleteCallBack mCallback;
	SharedPreferences settings;

	public BitKnowAnswerAdapter(Context context, List<BitKnowAnswerData> data,
			int questionid, String phone, String myphone,
			AnswerDeleteCallBack callback) {
		this.mData = data;
		this.mcontext = context;
		this.questionid = questionid;
		this.phone = phone;
		this.myphone = myphone;
		this.mCallback = callback;
		this.myConnect = new BitKnowHttpConnect(mcontext);
	}

	@Override
	public int getCount() {
		if (mData == null)
			return 0;
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;

		// if (convertView == null) {

		holder = new ViewHolder();
		LayoutInflater flater = LayoutInflater.from(mcontext);
		convertView = flater.inflate(R.layout.listview_bitkonw_detail, null);
		holder.userimage = (RoundedImageView) convertView
				.findViewById(R.id.bit_know_user_imageView);

		holder.username = (TextView) convertView
				.findViewById(R.id.bit_know_user_name);
		holder.answertime = (TextView) convertView
				.findViewById(R.id.bit_konw_answer_time);
		holder.answerzannumber = (TextView) convertView
				.findViewById(R.id.bitknow_listview_answer_zan_number);
		holder.answer = (TextView) convertView
				.findViewById(R.id.bit_konw_answer);
		holder.answerzan = (ImageView) convertView
				.findViewById(R.id.bitknow_listview_answernumber_drawable);
		holder.answeruse = (TextView) convertView
				.findViewById(R.id.bit_know_answeruse);
		holder.answerdelete = (TextView) convertView
				.findViewById(R.id.bit_know_answerdelete);
		holder.useicon = (ImageView) convertView
				.findViewById(R.id.bitknow_listview_use_drawable);
		holder.answerusedelete = (LinearLayout) convertView
				.findViewById(R.id.bit_know_answerusedeletelinearlayout);

		convertView.setTag(holder);

		// } else {
		//
		// holder = (ViewHolder) convertView.getTag();
		// }

		if (((String) (mData.get(position).getphotoUrl())).equals("无")) {

		} else {
			ImageLoader.getInstance().displayImage(
					Constants.PHTOT_CLOUDSERVER_STRING
							+ (String) mData.get(position).getphotoUrl(),
					holder.userimage);
		}
		if (position == 0) {
			if (mData.get(position).getisuse() == true) {
				holder.useicon.setVisibility(View.VISIBLE);
			}

		}
		if (myphone.contains(phone)) {
			holder.answerusedelete.setVisibility(View.VISIBLE);
		}
		if (((String) mData.get(position).getZan()).equals("yes")) {
			holder.answerzan.setImageResource(R.drawable.bit_know_zan_red);
		}

		String url = Constants.PHTOT_CLOUDSERVER_STRING
				+ (String) mData.get(position).getphotoUrl();
		ImageLoader.getInstance().displayImage(url, holder.userimage);
		holder.username.setText((String) mData.get(position).getusername());
		holder.answertime.setText((String) mData.get(position).gettime());
		holder.answerzannumber.setText(mData.get(position).getzan().toString()
				+ "个赞");
		holder.answer.setText(mData.get(position).gettext());
		holder.answerzan.setOnClickListener(new AnswerListener(
				holder.answerzan, holder.answerzannumber, position));
		holder.answerdelete.setOnClickListener(new AnswerDeleteListener(
				position, holder.answerdelete));
		holder.answeruse.setOnClickListener(new AnswerUseListener(position,
				holder.useicon));

		return convertView;

	}

	public String getMyphone() {
		return myphone;
	}

	public void setMyphone(String myphone) {
		this.myphone = myphone;
	}

	public void setphone(String phone) {
		this.phone = phone;
	}

	private class ViewHolder {
		RoundedImageView userimage;// 用户头像
		TextView username;
		TextView answertime;
		TextView answerzannumber;
		TextView answer;
		ImageView answerzan;
		TextView answeruse;
		TextView answerdelete;
		ImageView useicon;
		LinearLayout answerusedelete;
	}

	private class AnswerListener implements OnClickListener {
		private ImageView answerzan;
		private TextView answerzannumber;
		private int position;

		public AnswerListener(ImageView answerzan, TextView answerzannumber,
				int position) {
			this.answerzan = answerzan;
			this.answerzannumber = answerzannumber;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			JSONObject json = new JSONObject();
			int id = mData.get(position).getid();
			try {
				json.put("id", id);
				if (myphone != "") {
					json.put("phone", myphone);
				} else {
					json.put("phone", "-1");
				}
			} catch (JSONException e) {

			}
			myConnect.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "GetAnswerZan", Constants.BITKNOWTEST_CLOUDSERVER_STRING
					+ "GetAnswerZan", json, new BitKnowNetworkHandler() {

				@Override
				public void onSuccess(String str) {
					JSONObject json;
					try {
						System.out.println(str);
						json = new JSONObject(str);
						boolean isSuccess = json.getBoolean("success");
						if (isSuccess) {
							Toast.makeText(mcontext, "点赞成功", 0).show();
							answerzan
									.setImageResource(R.drawable.bit_know_zan_red);
							mData.get(position).addZan();
							answerzannumber.setText(mData.get(position)
									.getzan().toString()
									+ "个赞");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure() {
					Toast.makeText(mcontext, "点赞失败", 0).show();
				}
			});

		}

	}

	private class AnswerDeleteListener implements OnClickListener {
		private int position;
		private TextView mTvDelete;

		public AnswerDeleteListener(int position, TextView view) {
			this.position = position;
			this.mTvDelete = view;
		}

		@Override
		public void onClick(View v) {
			int id = mData.get(position).getid();
			JSONObject json = new JSONObject();
			try {
				json.put("id", id);
			} catch (JSONException e) {
			}
			mTvDelete.setEnabled(false);
			myConnect.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "DeleteAnswer", Constants.BITKNOWTEST_CLOUDSERVER_STRING
					+ "DeleteAnswer", json, new BitKnowNetworkHandler() {
				
				@Override
				public void onSuccess(String str) {
					JSONObject json;
					try {
						json = new JSONObject(str);
						System.out.println(str);
						boolean isSuccess = json.getBoolean("success");
						if (isSuccess) {
							Toast.makeText(mcontext, "删除成功", 0).show();
							mData.remove(position);
							notifyDataSetChanged();
							mCallback.onDeleteAnswer();
						} else {
							Toast.makeText(mcontext, "删除失败", 0).show();
							mTvDelete.setEnabled(true);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						mTvDelete.setEnabled(true);
					}
				}

				@Override
				public void onFailure() {
					Toast.makeText(mcontext, "删除失败", 0).show();
					mTvDelete.setEnabled(true);
				}
			});
		}

	}

	private class AnswerUseListener implements OnClickListener {
		private ImageView answeruse;
		private int position;

		public AnswerUseListener(int position, ImageView answeruse) {
			this.position = position;
			this.answeruse = answeruse;
		}

		@Override
		public void onClick(View v) {
			int id = mData.get(position).getid();
			JSONObject json = new JSONObject();
			try {
				json.put("question_id", questionid);
				json.put("answer_id", id);
			} catch (JSONException e) {

			}
			myConnect
					.doPost(Constants.BITKNOWTEST_SERVER_STRING
							+ "GetAnswerAdapt",
							Constants.BITKNOWTEST_CLOUDSERVER_STRING
									+ "GetAnswerAdapt", json,
							new BitKnowNetworkHandler() {

								@Override
								public void onSuccess(String str) {
									JSONObject json;
									try {
										json = new JSONObject(str);
										boolean isSuccess = json
												.getBoolean("success");
										if (isSuccess) {
											Toast.makeText(mcontext, "采纳成功", 0)
													.show();
											mData.get(0).setisuse(false);
											mData.get(position).setisuse(true);
											BitKnowAnswerData data = mData
													.get(position);
											mData.remove(position);
											mData.add(0, data);
											notifyDataSetChanged();
										} else {
											Toast.makeText(mcontext, "采纳失败", 0)
											.show();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure() {
									Toast.makeText(mcontext, "采纳失败", 0).show();
								}
							});

		}

	}

	/**
	 * 评论操作回调，删除评论和刷新评论成功后调用
	 * 
	 * @author 周俊皓
	 * @version 下午6:21:16
	 *
	 */
	public interface AnswerDeleteCallBack {
		/**
		 * 删除了一条评论
		 */
		public void onDeleteAnswer();
	}

}
