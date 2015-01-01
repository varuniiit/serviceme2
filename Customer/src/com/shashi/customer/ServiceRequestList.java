package com.shashi.customer;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.shashi.customer.adapter.ServiceListAdapter;
import com.shashi.customer.db.CustomerDatabase;
import com.shashi.customer.db.DataBaseHelper;

public class ServiceRequestList extends ActionBarActivity implements
		OnItemClickListener {

	ListView listView;
	DataBaseHelper dataBaseHelper;
	List<CustomerDatabase> list;
	List<Integer> selectedItems = new ArrayList<Integer>();
	boolean isClearClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_request_list);
		listView = (ListView) findViewById(android.R.id.list);
		TextView textView = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(textView);
		listView.setOnItemClickListener(this);
		dataBaseHelper = new DataBaseHelper(this);
		updateListView();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (!isClearClicked) {
			Intent intent = new Intent(this, ProviderList.class);
			intent.putExtra("requestid", list.get(position).getRequestId());
			startActivity(intent);
		} else {
			CheckedTextView checkedTextView = (CheckedTextView) arg1;
			if (checkedTextView.isChecked()) {
				checkedTextView.setChecked(true);
				selectedItems.add((Integer) position);
			} else {
				checkedTextView.setChecked(false);
				selectedItems.remove((Integer) position);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.service_request_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getTitle().toString().equals("Clear")) {
			selectedItems.clear();
			isClearClicked = true;
			item.setTitle("Delete");
			listView.setOnItemClickListener(this);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_checked);
			for (CustomerDatabase provider : list) {
				adapter.add(provider.getServiceList() + "\n"
						+ provider.getTimeToService());
			}
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listView.setAdapter(adapter);
			return true;
		} else if (item.getTitle().toString().equals("Delete")) {
			isClearClicked = false;
			for (Integer i : selectedItems) {
				dataBaseHelper.delete(list.get(i));
			}
			updateListView();
			item.setTitle("Clear");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateListView() {
		// TODO Auto-generated method stub
		list = dataBaseHelper.getAllEntries();
		listView.setAdapter(new ServiceListAdapter(this, dataBaseHelper, list));
	}
}
