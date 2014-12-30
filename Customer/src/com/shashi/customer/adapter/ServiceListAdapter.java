package com.shashi.customer.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shashi.customer.R;
import com.shashi.customer.db.CustomerDatabase;
import com.shashi.customer.db.DataBaseHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceListAdapter extends BaseAdapter {

	Context context;
	Helper helper;
	List<CustomerDatabase> list;
	DataBaseHelper dataBaseHelper;

	public ServiceListAdapter(Context context, DataBaseHelper dataBaseHelper,
			List<CustomerDatabase> list) {
		this.context = context;
		this.dataBaseHelper = dataBaseHelper;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.customer_service, arg2, false);
			helper = new Helper();
			helper.customer = (TextView) view.findViewById(R.id.servicename);
			helper.time = (TextView) view.findViewById(R.id.time_to);
			helper.location = (TextView) view.findViewById(R.id.location_to);
			view.setTag(helper);
		} else {
			helper = (Helper) view.getTag();
		}
		helper.customer.setText(list.get(position).getServiceList());
		String json = list.get(position).getLocationToService();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			helper.location.setText(jsonObject.getString("address"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		helper.time.setText(list.get(position).getTimeToService());
		return view;
	}

	private static class Helper {
		TextView time, customer, location;
	}

}
