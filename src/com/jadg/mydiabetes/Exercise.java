package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.ExerciseRegAdapter;
import com.jadg.mydiabetes.database.ExerciseRegDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;



public class Exercise extends Activity {

	ListView exercisesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();
		
		exercisesList = (ListView)findViewById(R.id.ExercisesActivityList);
		fillListView(exercisesList);
		
		
		EditText datefrom = (EditText)findViewById(R.id.et_Exercise_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Exercise_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(exercisesList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(exercisesList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.exercise, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Exercise_Add:
				Intent intent = new Intent(this, ExerciseDetail.class);
				startActivity(intent);
				return true;
		
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDates(){
		EditText dateago = (EditText)findViewById(R.id.et_Exercise_DataFrom);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -8);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    Date newDate = cal.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        
	    dateago.setText(dateString);
        
        EditText datenow = (EditText)findViewById(R.id.et_Exercise_DataTo);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
	    newDate = cal.getTime();
	    dateString = formatter.format(newDate);
        datenow.setText(dateString);
	}
	
	@SuppressWarnings("deprecation")
	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Exercise_DataFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	@SuppressWarnings("deprecation")
	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Exercise_DataTo);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	
	public void fillListView(ListView lv){

		EditText datefrom = (EditText)findViewById(R.id.et_Exercise_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Exercise_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<ExerciseRegDataBinding> allExercises = rdb.ExerciseReg_GetByDate(datefrom.getText().toString(), dateto.getText().toString());
		
		rdb.close();
		lv.setAdapter(new ExerciseRegAdapter(allExercises, this));
	}
}
