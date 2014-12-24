package com.shashi.provider.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import com.shashi.provider.CustomerAccept;
import com.shashi.provider.MainActivity;
import com.shashi.provider.R;
import com.shashi.provider.db.DataBaseHelper;
import com.shashi.provider.db.ProviderDatabase;

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

	Context context;
	Intent emptyIntent = null;
	JSONObject jsonObject = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Toast.makeText(context, "Recived", Toast.LENGTH_LONG).show();
		System.out.println("Channel  "
				+ intent.getExtras().getString("com.parse.Channel"));
		if (intent.getAction().equals("com.parse.push.intent.RECEIVE")) {
			try {
				JSONObject jsonObject = new JSONObject(intent.getExtras()
						.getString("com.parse.Data"));
				String data = jsonObject.getString("alert");
				System.out.println("Data  " + data);
				decodeMessage(data);
				if (MainActivity.isAppOpend) {
					MainActivity.updateListView();
				} else if (CustomerAccept.isAcceptAppOpend) {
					CustomerAccept.updateListView();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	private void decodeMessage(String message) {

		try {
			jsonObject = new JSONObject(message);
			DataBaseHelper baseHelper = new DataBaseHelper(context);
			JSONObject json = null;
			String title = null;
			if (jsonObject.getString("messagetype").equalsIgnoreCase("request")) {
				ProviderDatabase database = new ProviderDatabase();
				database.setCustomerAcceptedStatus("false");
				database.setRequestId(jsonObject.getString("requestid"));
				database.setCustomerName(jsonObject.getString("customername"));
				database.setTimeToService(jsonObject.getString("timetoservice"));
				database.setLocationToService(jsonObject
						.getString("locationtoservice"));
				database.setProviderAcceptedStatus("false");
				database.setInstallationId(jsonObject
						.getString("installationid"));
				baseHelper.insert(database);
				json = new JSONObject();
				json.put("id", database.getId());
				json.put("customername", database.getCustomerName());
				json.put("timetoservice", database.getTimeToService());
				json.put("locationtoservice", database.getLocationToService());
				json.put("provideracceptedstatus",
						database.getProviderAcceptedStatus());
				json.put("customeracceptedstatus",
						database.getCustomerAcceptedStatus());
				json.put("requestid", database.getRequestId());
				json.put("installationid", database.getInstallationId());
				emptyIntent = new Intent(context, MainActivity.class);
				title = "Customer Service Request";
			} else {
				ProviderDatabase database = new ProviderDatabase();
				database.setCustomerAcceptedStatus("true");
				database.setRequestId(jsonObject.getString("requestid"));
				database.setCustomerName(jsonObject.getString("customername"));
				database.setTimeToService(jsonObject.getString("timetoservice"));
				database.setLocationToService(jsonObject
						.getString("locationtoservice"));
				database.setInstallationId(jsonObject
						.getString("installationid"));
				baseHelper.updateCustomerStatus(database);
				json = new JSONObject();
				json.put("id", database.getId());
				json.put("customername", database.getCustomerName());
				json.put("timetoservice", database.getTimeToService());
				json.put("locationtoservice", database.getLocationToService());
				json.put("acceptedstatus", database.getProviderAcceptedStatus());
				json.put("customeracceptedstatus",
						database.getCustomerAcceptedStatus());
				json.put("requestid", database.getRequestId());
				json.put("installationid", database.getInstallationId());
				emptyIntent = new Intent(context, CustomerAccept.class);
				title = "Customer Accepted Service";
			}
			emptyIntent.putExtra("data", json.toString());
			showNotification(title);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void showNotification(String title) throws JSONException {

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 9,
				emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(
						jsonObject.getString("customername") + ".\n"
								+ jsonObject.getString("timetoservice") + ".")
				.setContentIntent(pendingIntent);
		mBuilder.setSound(RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(99, mBuilder.build());
	}
}
