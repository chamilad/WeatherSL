package lk.ucsc.wsl2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textview = new TextView(this);
        textview.setText("This is the Details tab");
        setContentView(textview);
    }

}
