package com.bitss.Digital_BIT.Meeting;

import com.bitss.Digital_BIT.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**

 */
class SearchBoxActivity extends Activity {
	private final int SEARCH_MENU = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchboxlayout);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		handleSearchQuery(getIntent());
	}

	private void handleSearchQuery(Intent queryIntent) {
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			onSearch(queryIntent);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleSearchQuery(intent);
	}

	private void onSearch(Intent intent) {
		final String queryString = intent.getStringExtra(SearchManager.QUERY);
		Toast.makeText(this, queryString, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SEARCH_MENU, 0, getText(R.string.searchHint)).setIcon(
				android.R.drawable.ic_menu_search);
		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SEARCH_MENU:
			onSearchRequested();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}