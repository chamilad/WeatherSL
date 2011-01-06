package lk.ucsc.wsl2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class main extends TabActivity {
	ProgressDialog progressDialog;

	AlertDialog.Builder change_location_alert;
	String[] LOCATIONS;
	AutoCompleteTextView textView;
	ArrayAdapter<String> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// change location alert creation

		LOCATIONS = getResources().getStringArray(R.array.locations_array);

		// tabs creation
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		spec = tabHost.newTabSpec("summary").setIndicator("Summary",
				res.getDrawable(R.drawable.summary_icon)).setContent(
				R.id.summary_view);
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("details").setIndicator("Details",
				res.getDrawable(R.drawable.detailed_icon)).setContent(
				R.id.details_view);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ForecastActivity.class);
		spec = tabHost.newTabSpec("forecast").setIndicator("Forecast",
				res.getDrawable(R.drawable.generic_icon)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, AlertsActivity.class);
		spec = tabHost.newTabSpec("alerts").setIndicator("Alerts",
				res.getDrawable(R.drawable.generic_icon)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

		// progress diaload creation
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Fetching Weather Info...");

		SharedPreferences settings = getSharedPreferences("weathersl_settings",
				0);
		String location = settings.getString("location", null);

		if (location == null) {
			// change_location_alert.show();
			updateLocation();
		} else {
			progressDialog.show();

			GetDetailsTask get_details_task = new GetDetailsTask();
			get_details_task.execute(location, null, null);
		}

	}

	public boolean validLocation(String location) {
		for (String l : LOCATIONS) {
			if (l.equalsIgnoreCase(location)) {
				return true;
			}
		}
		return false;
	}

	// show a location input and update the screen
	public void updateLocation() {
		change_location_alert = new AlertDialog.Builder(this);
		textView = new AutoCompleteTextView(this);

		adapter = new ArrayAdapter<String>(this, R.layout.location_list_item,
				LOCATIONS);

		change_location_alert.setTitle("WeatherSL");
		change_location_alert.setMessage("Change Location");

		textView.setAdapter(adapter);
		change_location_alert.setView(textView);

		change_location_alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = textView.getText().toString();

						// ...validate location
						if (validLocation(value)) {

							progressDialog.show();

							GetDetailsTask get_details_task = new GetDetailsTask();
							get_details_task.execute(value, null, null);
							// System.out.println(value);
						} else {
							// ...show alert
							showErrorToast("The location \"" + value
									+ "\" is invalid.");
							updateLocation();
						}
					}
				});

		change_location_alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		change_location_alert.show();
	}

	public void showErrorToast(String message) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.error_toast,
				(ViewGroup) findViewById(R.id.error_toast));

		TextView text = (TextView) layout.findViewById(R.id.error_msg);
		text.setText(message);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.change_loc:
			updateLocation();
			return true;
		case R.id.save:
			// System.out.println("settings");

			SharedPreferences settings = getSharedPreferences(
					"weathersl_settings", 0);
			SharedPreferences.Editor editor = settings.edit();
			TextView location_txtview = (TextView) findViewById(R.id.display_location);
			String location_city = location_txtview.getText().toString();
			location_city = location_city.substring(0, location_city
					.indexOf(","));
			System.out.println("Location City : " + location_city);
			editor.putString("location", location_city);

			// Commit the edits!
			editor.commit();

			Toast.makeText(this, location_city + " was saved to be autoloaded",
					Toast.LENGTH_LONG).show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	final Handler service_handler = new Handler() {
		public void handleMessage(Message message) {
			@SuppressWarnings("unchecked")
			// String display_location_full = (String)
			// message.getData().getString("display_location_full");
			// System.out.println(display_location_full);
			TextView textview = (TextView) findViewById(R.id.display_location);
			textview.setText((String) message.getData().getString(
					"display_location_full"));

			textview = (TextView) findViewById(R.id.temperature);
			textview.setText((String) message.getData()
					.getString("temp_string"));

			textview = (TextView) findViewById(R.id.weather);
			textview.setText((String) message.getData().getString("weather"));

			textview = (TextView) findViewById(R.id.last_update);
			textview.setText((String) message.getData().getString("ob_time"));

			textview = (TextView) findViewById(R.id.observed);
			textview.setText("Observation from "
					+ (String) message.getData().getString("ob_location"));

			textview = (TextView) findViewById(R.id.courtesy);
			textview.setText((String) message.getData().getString("credit"));

			textview = (TextView) findViewById(R.id.loca);
			textview.setText((String) message.getData().getString(
					"display_location_full"));

			textview = (TextView) findViewById(R.id.loc_lat);
			textview.setText((String) message.getData().getString("loc_lat"));

			textview = (TextView) findViewById(R.id.loc_long);
			textview.setText((String) message.getData().getString("loc_long"));

			textview = (TextView) findViewById(R.id.loc_ele);
			textview.setText((String) message.getData().getString("loc_ele"));

			textview = (TextView) findViewById(R.id.obs_point);
			textview.setText((String) message.getData()
					.getString("ob_location"));

			textview = (TextView) findViewById(R.id.ob_lat);
			textview.setText((String) message.getData().getString("ob_lat"));

			textview = (TextView) findViewById(R.id.ob_long);
			textview.setText((String) message.getData().getString("ob_long"));

			textview = (TextView) findViewById(R.id.ob_ele);
			textview.setText((String) message.getData().getString("ob_ele"));

			textview = (TextView) findViewById(R.id.station_id);
			textview
					.setText((String) message.getData().getString("station_id"));

			textview = (TextView) findViewById(R.id.ob_time_rfc);
			textview.setText((String) message.getData()
					.getString("ob_time_rfc"));

			textview = (TextView) findViewById(R.id.temp_detail);
			textview.setText((String) message.getData()
					.getString("temp_string"));

			textview = (TextView) findViewById(R.id.weather_detail);
			textview.setText((String) message.getData().getString("weather"));

			textview = (TextView) findViewById(R.id.rel_humidity);
			textview.setText((String) message.getData().getString(
					"rel_humidity"));

			textview = (TextView) findViewById(R.id.wind);
			textview.setText((String) message.getData().getString("wind"));

			textview = (TextView) findViewById(R.id.pressure);
			textview.setText((String) message.getData().getString("pressure"));

			textview = (TextView) findViewById(R.id.dew_point);
			textview.setText((String) message.getData().getString("dew_point"));

			textview = (TextView) findViewById(R.id.heat_index);
			textview
					.setText((String) message.getData().getString("heat_index"));

			textview = (TextView) findViewById(R.id.visibility);
			textview
					.setText((String) message.getData().getString("visibility"));

			/*
			 * textview = (TextView) findViewById(R.id.);
			 * textview.setText((String) message.getData().getString(
			 * "Info Courtesy"));
			 */

			/*
			 * GetWeatherIcon get_weather_icon = new GetWeatherIcon();
			 * get_weather_icon
			 * .execute((String)message.getData().getString("icon_url"));
			 */

			// getting the images from the urls
			try {
				// weather condition icon
				URL image_url = new URL((String) message.getData().getString(
						"icon_url"));
				InputStream image_is = image_url.openStream();

				Drawable d = Drawable.createFromStream(image_is, "src");
				ImageView img_view = (ImageView) findViewById(R.id.weather_icon);
				img_view.setImageDrawable(d);

				// wunderground icon
				image_url = new URL((String) message.getData().getString(
						"wg_icon_url"));
				image_is = image_url.openStream();

				d = Drawable.createFromStream(image_is, "src");
				img_view = (ImageView) findViewById(R.id.wg_icon);
				img_view.setImageDrawable(d);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			}

			progressDialog.dismiss();
		}
	};

	class GetDetailsTask extends AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... arg0) {
			try {
				arg0[0] = arg0[0].replaceAll(" ", "%20");
				URL url = new URL(
						"http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query="
								+ arg0[0] + ",LK");
				System.out.println(url.toString());

				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();

				XMLReader xml_reader = sp.getXMLReader();
				XMLHandler xml_handler = new XMLHandler();
				xml_reader.setContentHandler(xml_handler);

				xml_reader.parse(new InputSource(url.openStream()));

				Message msg = service_handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("display_location_full",
						xml_handler.display_location_full);
				bundle.putString("icon_url", xml_handler.icon_url);
				bundle.putString("weather", xml_handler.weather);
				bundle.putString("temp_string", xml_handler.temp_string);
				bundle.putString("ob_time", xml_handler.ob_time);
				bundle.putString("ob_location", xml_handler.ob_location);
				bundle.putString("wg_icon_url", xml_handler.wg_icon_url);
				bundle.putString("credit", xml_handler.credit);
				bundle.putString("loc_long", xml_handler.loc_long);
				bundle.putString("loc_lat", xml_handler.loc_lat);
				bundle.putString("loc_ele", xml_handler.loc_ele);
				bundle.putString("ob_long", xml_handler.ob_long);
				bundle.putString("ob_lat", xml_handler.ob_lat);
				bundle.putString("ob_ele", xml_handler.ob_ele);
				bundle.putString("station_id", xml_handler.station_id);
				bundle.putString("ob_time_rfc", xml_handler.ob_time_rfc);
				bundle.putString("rel_humidity", xml_handler.rel_humidity);
				bundle.putString("wind", xml_handler.wind);
				bundle.putString("pressure", xml_handler.pressure);
				bundle.putString("dew_point", xml_handler.dew_point);
				bundle.putString("heat_index", xml_handler.heat_index);
				bundle.putString("visibility", xml_handler.visibility);

				msg.setData(bundle);
				service_handler.sendMessage(msg);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Woops! An error was encountered.",Toast.LENGTH_LONG).show();
				System.exit(0);
			}

			return null;
		}

	}

}
