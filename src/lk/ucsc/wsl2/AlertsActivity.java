package lk.ucsc.wsl2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AlertsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textview = new TextView(this);
        textview.setText("This is the Alerts tab");
        setContentView(textview);
    }
}
