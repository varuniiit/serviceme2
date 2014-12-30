package com.shashi.customer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.shashi.customer.db.CustomerDatabase;
import com.shashi.customer.db.DataBaseHelper;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		OnItemSelectedListener, OnTimeSetListener, OnDateSetListener {

	Spinner spinner;
	Button map, dateTime, submit;
	TextView mapText, dateTimeText;
	String[] items = { "Shop", "Hotel", "Rest", "Food" };
	LatLng location;
	String serviceType = items[0];
	DatePickerDialog datePickerDialog;
	TimePickerDialog timePickerDialog;
	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	String date, time;
	Calendar dateTimeCalender = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner = (Spinner) findViewById(R.id.dropdown);
		map = (Button) findViewById(R.id.map);
		dateTime = (Button) findViewById(R.id.datetimepicker);
		mapText = (TextView) findViewById(R.id.locationtext);
		dateTimeText = (TextView) findViewById(R.id.datetext);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		spinner.setOnItemSelectedListener(this);
		map.setOnClickListener(this);
		dateTime.setOnClickListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, items);
		spinner.setAdapter(adapter);
		String name = getSharedPreferences("name", Context.MODE_PRIVATE)
				.getString("customername", null);
		if (name == null)
			showDialog();
	}

	private void showDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final View view = layoutInflater.inflate(R.layout.customer_name, null);
		final EditText editText = (EditText) view.findViewById(R.id.editText1);
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("Customer Name")
				.setIcon(R.drawable.ic_launche)
				.setView(view)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (editText.getText().toString().trim()
										.isEmpty()) {
									showDialog();
								}
								getSharedPreferences("name",
										Context.MODE_PRIVATE)
										.edit()
										.putString("customername",
												editText.getText().toString())
										.commit();
							}
						})
				.setNegativeButton("Exit",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								MainActivity.this.finish();
							}
						});
		builder.create();
		builder.setCancelable(false);
		builder.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (MapActivity.address != null && !MapActivity.address.isEmpty()) {
			mapText.setText("Address: " + MapActivity.address);
		}
		if (MapActivity.finalLoc != null) {
			location = MapActivity.finalLoc;
		}

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.map) {
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
		} else if (view.getId() == R.id.datetimepicker) {
			setupDateTimePicker();
			datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
		} else if (view.getId() == R.id.submit) {
			/*
			 * if (MapActivity.address == null || MapActivity.address.isEmpty())
			 * { Toast.makeText(this, "Please select address first.",
			 * Toast.LENGTH_LONG).show(); return; }
			 */
			if (this.date == null || this.date.isEmpty() || this.time == null
					|| this.time.isEmpty()) {
				Toast.makeText(this, "Please select date time first.",
						Toast.LENGTH_LONG).show();
				return;
			}
			try {
				DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
				ParseObject parseObject = new ParseObject("CustomerRequest");
				parseObject.add("messagetype", "request");
				int requestid = dataBaseHelper.getRequestId().get(0)
						.getRequestId();
				parseObject.add("requestid", GlobalApplication.installationId
						+ "@" + requestid);
				dataBaseHelper.updateRequestIdRecent();
				parseObject.add("customername",
						getSharedPreferences("name", Context.MODE_PRIVATE)
								.getString("customername", null));
				parseObject.add("servicetype", serviceType);
				parseObject.add("timetoservice", new SimpleDateFormat(
						"dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
						.format(dateTimeCalender.getTime()));
				JSONObject object = new JSONObject();
				object.put("address", /* MapActivity.address */"Bangalore");
				object.put("long", /* MapActivity.finalLoc.longitude */123456);
				object.put("lat", /* MapActivity.finalLoc.latitude */123456);
				parseObject.add("locationtoservice", object.toString());
				parseObject.add("installationid",
						GlobalApplication.installationId);
				CustomerDatabase customerDatabase = new CustomerDatabase();
				customerDatabase
						.setInstallationId(GlobalApplication.installationId);
				customerDatabase.setLocationToService(object.toString());
				customerDatabase.setRequestId(GlobalApplication.installationId
						+ "@" + requestid);
				customerDatabase.setServiceList(serviceType);
				customerDatabase.setTimeToService(new SimpleDateFormat(
						"dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
						.format(dateTimeCalender.getTime()));
				dataBaseHelper.insert(customerDatabase);
				parseObject.saveInBackground();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		serviceType = items[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void setupDateTimePicker() {
		final Calendar calendar = Calendar.getInstance();
		datePickerDialog = DatePickerDialog.newInstance(this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), false);
		datePickerDialog.setYearRange(1985, 2028);
		datePickerDialog.setCloseOnSingleTapDay(false);
		timePickerDialog = TimePickerDialog.newInstance(this,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), false, false);
		timePickerDialog.setCloseOnSingleTapMinute(false);
	}

	@Override
	public void onTimeSet(RadialPickerLayout arg0, int hourOfday, int minute) {

		dateTimeCalender.set(Calendar.HOUR_OF_DAY, hourOfday);
		dateTimeCalender.set(Calendar.MINUTE, minute);
		this.time = hourOfday + ":" + minute;
		this.dateTimeText.setText(new SimpleDateFormat("dd-MMM-yyyy hh:mm a",
				Locale.ENGLISH).format(dateTimeCalender.getTime()));
	}

	@Override
	public void onDateSet(DatePickerDialog arg0, int year, int month, int date) {
		this.date = date + "-" + month + "-" + year;
		dateTimeCalender.set(Calendar.YEAR, year);
		dateTimeCalender.set(Calendar.MONTH, month);
		dateTimeCalender.set(Calendar.DATE, date);
		timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, ServiceRequestList.class);
		startActivity(intent);
		return true;
	}
}
