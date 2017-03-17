package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.data.Insulin;


public class InsulinsDetail extends BaseActivity {

	int idInsulin = 0;
	String originalName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insulins_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}



		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idInsulin = Integer.parseInt(id);
			Insulin toFill = rdb.Insulin_GetById(Integer.parseInt(id));

			EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
			name.setText(toFill.getName());
			originalName = toFill.getName();
			EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
			type.setText(toFill.getType());

			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
			int index = 0;
			try {
				index = Integer.parseInt(toFill.getAction());
			} catch (NumberFormatException nfe) {
				// index will get 0
				Log.e ("onCreate", "Read a text that was not a number from action"+ nfe);
			}
			((RadioButton)myRadioGroup.getChildAt(index)).setChecked(true);


			rdb.close();

		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.insulins_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateInsulin();
				}
			});
		}else{
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddNewInsulin();
				}
			});
		}

		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menuItem_InsulinsDetail_Delete:
				DeleteInsulin();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void AddNewInsulin(){
		EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
		EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
		RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
		int index = myRadioGroup.indexOfChild(findViewById(myRadioGroup.getCheckedRadioButtonId()));


		//EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(name.getText().toString().equals("")){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(type.getText().toString().equals("")){
			type.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(type, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(index==-1){
			myRadioGroup.requestFocus();
			Toast.makeText(this, getString(R.string.insulin_message_action), Toast.LENGTH_SHORT).show();
			return;
		}

		DB_Read read = new DB_Read(this);
		boolean name_exists = read.Insulin_NameExists(name.getText().toString());
		if(name_exists){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			Toast.makeText(this, getString(R.string.insulin_message_name_exists), Toast.LENGTH_SHORT).show();
			return;
		}

		read.close();




		DB_Write wdb = new DB_Write(this);

		Insulin insulin = new Insulin();


		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(String.valueOf(index));


		wdb.Insulin_Add(insulin);
		wdb.close();

		finish();
	}


	public void UpdateInsulin(){
		EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
		EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);

		RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
		int index = myRadioGroup.indexOfChild(findViewById(myRadioGroup.getCheckedRadioButtonId()));

		//EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(name.getText().toString().equals("")){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(type.getText().toString().equals("")){
			type.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(type, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(index==-1){
			myRadioGroup.requestFocus();
			Toast.makeText(this,getString(R.string.insulin_message_action),Toast.LENGTH_SHORT).show();

			return;
		}


		DB_Read read = new DB_Read(this);
		boolean name_exists = read.Insulin_NameExists(name.getText().toString());
		if(name_exists && !originalName.equals(name.getText().toString())){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			Toast.makeText(this, getString(R.string.insulin_message_name_exists), Toast.LENGTH_SHORT).show();
			return;
		}

		read.close();

		DB_Write wdb = new DB_Write(this);

		Insulin insulin = new pt.it.porto.mydiabetes.data.Insulin();

		insulin.setId(idInsulin);
		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(String.valueOf(index));


		wdb.Insulin_Update(insulin);
		wdb.close();

		finish();
	}



	public void DeleteInsulin(){
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(R.string.delete_insulin)
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Insulin_Remove(idInsulin);
							goUp();
						}catch (Exception e) {
							Toast.makeText(c, getString(R.string.delete_insulin_exception), Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void goUp(){
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}
}
