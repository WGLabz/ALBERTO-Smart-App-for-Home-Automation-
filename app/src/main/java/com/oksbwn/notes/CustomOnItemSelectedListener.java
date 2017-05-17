package com.oksbwn.notes;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {
 String getText="Note";
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
        getText= parent.getItemAtPosition(pos).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
public String getText(){
    return this.getText;
}
}