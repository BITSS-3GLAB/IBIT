package com.bitss.Digital_BIT.Admission;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Utils;
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

/**
 * 表格的adapter
 * */
public class TableAdapter extends BaseTableAdapter {

	private static final int VIEW_TYPE_COUNT = 2;
	private static final int TABLE_HEADER = 0;
	private static final int TABLE_BODY = 1;

	private Context context;
	private LayoutInflater inflater;

	// 每个单元格的宽、高
	private int width;
	private int height;
	private int rowCount;
	private int columnCount;

	private ArrayList<ArrayList<String>> rowDataList = new ArrayList<ArrayList<String>>();

	/**
	 * 
	 * @param rowDataList
	 *            : 行数据，第一行是title的值
	 * */
	public TableAdapter(Context context,
			ArrayList<ArrayList<String>> rowDataList) {

		inflater = LayoutInflater.from(context);
		this.context = context;
		this.rowDataList = rowDataList;
		rowCount = this.rowDataList.size() - 1; // 行数减一
		columnCount = rowDataList.get(0).size() - 1; // 列数减一

		width = Utils.getScreenWidth(context) / 4;
		height = Utils.dp2pixel(context, 40);
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public int getWidth(int column) {
		return width;
	}

	@Override
	public int getHeight(int row) {
		return height;
	}

	@Override
	public int getItemViewType(int row, int column) {
		return (row < 0) ? TABLE_HEADER : TABLE_BODY;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	public int getLayoutResource(int row, int column) {
		final int layoutResource;
		switch (getItemViewType(row, column)) {
		case TABLE_HEADER:
			layoutResource = R.layout.item_table_header;
			break;
		case TABLE_BODY:
			layoutResource = R.layout.item_table_body;
			break;
		default:
			throw new RuntimeException("wtf?");
		}
		return layoutResource;
	}

	@Override
	public View getView(int row, int column, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(getLayoutResource(row, column), parent,
				false);
		TextView bodyView = (TextView) convertView
				.findViewById(R.id.tv_table_body);
		int actuallyRow = row < 0 ? 0 : row + 1;
		int actuallyColumn = column < 0 ? 0 : column + 1;

		bodyView.setText(rowDataList.get(actuallyRow).get(actuallyColumn));
		return convertView;
	}
}
