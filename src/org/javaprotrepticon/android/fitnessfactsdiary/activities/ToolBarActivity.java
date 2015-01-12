package org.javaprotrepticon.android.fitnessfactsdiary.activities;

import org.javaprotrepticon.android.fitnessfactsdiary.R;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ToolBarActivity extends ActionBarActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.my_layout);

	    Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	    toolbar.setTitle(""); 
	    toolbar.setSubtitle(""); 
	    toolbar.setOnMenuItemClickListener(
	            new Toolbar.OnMenuItemClickListener() {
	                @Override
	                public boolean onMenuItemClick(MenuItem item) {
	                    return true;
	                }
	    });
	    
	    toolbar.inflateMenu(R.menu.main_activity_menu);
	}
	
}
