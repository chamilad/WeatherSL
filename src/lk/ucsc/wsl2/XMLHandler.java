package lk.ucsc.wsl2;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	private StringBuffer buff = null;
	private boolean buffer, in_display_location, in_icon_url,
			in_observation_location = false;
	public String display_location_full, temp_string, weather, icon_url,
			ob_time, ob_location, wg_icon_url, credit, loc_long, loc_lat,
			loc_ele, ob_long, ob_lat, ob_ele, station_id, ob_time_rfc,
			rel_humidity, wind, pressure, dew_point, heat_index,
			visibility = null;

	@Override
	public void startDocument() throws SAXException {
		// System.out.println("Starting the document parse");
	}

	@Override
	public void endDocument() throws SAXException {
		// System.out.println("Ended parsing the document");
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("current_observation")) {
			// System.out.println("Met current_observation");
		}

		if (localName.equals("display_location")) {
			in_display_location = true;
		}

		if (localName.equals("full") && in_display_location) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("temperature_string")) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("weather")) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("icon_set")
				&& atts.getValue("name").equals("Default")) {
			in_icon_url = true;
		}

		if (localName.equals("icon_url") && in_icon_url) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("observation_time")) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("observation_location")) {
			in_observation_location = true;
		}

		if (localName.equals("full") && in_observation_location) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("credit")) {
			buff = new StringBuffer();
			buffer = true;
		}

		if (localName.equals("url")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("latitude") && in_display_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("longitude") && in_display_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("elevation") && in_display_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("latitude") && in_observation_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("longitude") && in_observation_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if(localName.equals("elevation") && in_observation_location){
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("station_id")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("observation_time_rfc822")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("relative_humidity")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("wind_string")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("pressure_string")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("dewpoint_string")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("heat_index_string")) {
			buff = new StringBuffer();
			buffer = true;
		}
		
		if (localName.equals("visibility_km")) {
			buff = new StringBuffer();
			buffer = true;
		}

		// System.out.println("start Element was called");
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if (buffer) {
			buff.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("full") && in_display_location) {
			display_location_full = buff.toString();
			buffer = false;

			// System.out.println("Full Content: " + display_location_full);
		}
		if (localName.equals("display_location")) {
			in_display_location = false;
		}
		
		if (localName.equals("observation_location")) {
			in_observation_location = false;
		}

		if (localName.equals("temperature_string")) {
			temp_string = buff.toString();
			buffer = false;
		}

		if (localName.equals("weather")) {
			weather = buff.toString();
			buffer = false;
		}

		if (localName.equals("icon_url") && in_icon_url) {
			icon_url = buff.toString();
			buffer = false;
			in_icon_url = false;
		}

		if (localName.equals("observation_time")) {
			ob_time = buff.toString();
			buffer = false;
		}

		if (localName.equals("full") && in_observation_location) {
			ob_location = buff.toString();
			buffer = false;			
		}

		if (localName.equals("credit")) {
			credit = buff.toString();
			buffer = false;
		}

		if (localName.equals("url")) {
			wg_icon_url = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("latitude") && in_display_location){
			loc_lat = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("longitude") && in_display_location){
			loc_long = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("elevation") && in_display_location){
			loc_ele = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("latitude") && in_observation_location){
			ob_lat = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("longitude") && in_observation_location){
			ob_long = buff.toString();
			buffer = false;
		}
		
		if(localName.equals("elevation") && in_observation_location){
			ob_ele = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("station_id")) {
			station_id = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("observation_time_rfc822")) {
			ob_time_rfc = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("relative_humidity")) {
			rel_humidity = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("wind_string")) {
			wind = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("pressure_string")) {
			pressure = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("dewpoint_string")) {
			dew_point = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("heat_index_string")) {
			heat_index = buff.toString();
			buffer = false;
		}
		
		if (localName.equals("visibility_km")) {
			visibility = buff.toString();
			buffer = false;
		}
	}

}
