package com.shashi.customer;

import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity {

	GoogleMap googleMap;
	MarkerOptions markerOptions;
	Marker marker;
	LatLng tempLocation;
	public static LatLng finalLoc;
	public static String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		address = null;
		finalLoc = null;
		initialMap();
	}

	private void initialMap() {
		if (googleMap == null) {
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			googleMap = fragment.getMap();
			if (googleMap == null) {
				Toast.makeText(this,
						"Google Play service is not supported in this device",
						Toast.LENGTH_LONG).show();
				MapActivity.finalLoc = null;
				MapActivity.address = null;
				finish();
			}
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
			googleMap.setOnMapClickListener(mapClickListener);
		}
	}

	private GoogleMap.OnMapClickListener mapClickListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng loc) {
			if (marker != null) {
				marker.remove();
			}
			tempLocation = loc;
			markerOptions = new MarkerOptions().position(loc).title(
					"Current Location");
			marker = googleMap.addMarker(markerOptions);
			if (googleMap != null) {
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,
						16.0f));
			}

		}
	};
	private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
		@Override
		public void onMyLocationChange(Location location) {
			LatLng loc = new LatLng(location.getLatitude(),
					location.getLongitude());
			tempLocation = loc;
			if (marker != null)
				marker.remove();
			MarkerOptions markerOptions = new MarkerOptions().position(loc)
					.title("Current Location");
			marker = googleMap.addMarker(markerOptions);
			if (googleMap != null) {
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,
						16.0f));
			}
			googleMap.setOnMyLocationChangeListener(null);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.map, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.okay) {
			finalLoc = tempLocation;
			Geocoder geocoder = new Geocoder(this);
			List<Address> address = null;
			try {
				address = geocoder.getFromLocation(finalLoc.latitude,
						finalLoc.longitude, 1);

				if (address.size() >= 0) {
					int adrressIndex = address.get(0).getMaxAddressLineIndex();
					String addressString = "";
					for (int i = 0; i < adrressIndex; i++) {
						addressString += " " + address.get(0).getAddressLine(i);
					}
					MapActivity.address = addressString;
					Toast.makeText(this, "Address: " + addressString,
							Toast.LENGTH_LONG).show();
				}
				finish();
			} catch (Exception e) {
				Toast.makeText(this,
						"Network problem. Please check network connection",
						Toast.LENGTH_LONG).show();
				MapActivity.finalLoc = null;
				MapActivity.address = null;
				e.printStackTrace();
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
