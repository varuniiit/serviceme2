package com.shashi.customer;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shashi.customer.db.DataBaseHelper;
import com.shashi.customer.db.ProviderAcceptedList;

public class ProviderList extends ActionBarActivity implements
		OnItemClickListener {

	ListView listView;
	String requestId;
	DataBaseHelper dataBaseHelper;
	List<ProviderAcceptedList> list;
	boolean isAccepted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provider_list);
		listView = (ListView) findViewById(android.R.id.list);
		TextView textView = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(textView);
		requestId = getIntent().getStringExtra("requestid");
		listView.setOnItemClickListener(this);
		dataBaseHelper = new DataBaseHelper(this);
		list = dataBaseHelper.getProviderList(requestId);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		String data = "";
		for (ProviderAcceptedList providerAcceptedList : list) {
			data = providerAcceptedList.getProviderName();
			data += "\n\n";
			if (providerAcceptedList.getCustomerAcceptedStatus()
					.equalsIgnoreCase("true")) {
				isAccepted = true;
				data += "Status: Accepted\n";
			} else {
				data += "Status: Pending\n";
			}
			adapter.add(data);
		}
		listView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		if (!isAccepted) {
			try {
				Intent intent = new Intent(this, ProviderDetails.class);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("providername", list.get(position)
						.getProviderName());
				jsonObject.put("requestid", list.get(position).getRequestId());
				jsonObject.put("installationid", list.get(position)
						.getInstallationId());
				jsonObject.put("id", list.get(position).getId());
				intent.putExtra("data", jsonObject.toString());
				startActivity(intent);
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(this, "This Service is Already Accepted",
					Toast.LENGTH_LONG).show();
		}

	}
}
