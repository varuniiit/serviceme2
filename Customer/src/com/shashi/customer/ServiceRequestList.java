package com.shashi.customer;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shashi.customer.adapter.ServiceListAdapter;
import com.shashi.customer.db.CustomerDatabase;
import com.shashi.customer.db.DataBaseHelper;

public class ServiceRequestList extends ActionBarActivity implements
		OnItemClickListener {

	ListView listView;
	DataBaseHelper dataBaseHelper;
	List<CustomerDatabase> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_request_list);
		listView = (ListView) findViewById(R.id.servicelist);
		listView.setOnItemClickListener(this);
		dataBaseHelper = new DataBaseHelper(this);
		list = dataBaseHelper.getAllEntries();
		listView.setAdapter(new ServiceListAdapter(this, dataBaseHelper, list));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(this, ProviderList.class);
		intent.putExtra("requestid", list.get(position).getRequestId());
		startActivity(intent);
	}
}
