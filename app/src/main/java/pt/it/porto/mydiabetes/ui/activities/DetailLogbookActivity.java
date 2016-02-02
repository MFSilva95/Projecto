package pt.it.porto.mydiabetes.ui.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class DetailLogbookActivity extends BaseMealActivity {
	public static final String ARG_CARBS = "ARG_CARBS";
	public static final String ARG_INSULIN = "ARG_INSULIN";
	public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";

	public static final String SAVE_SHOWING_ERROR = "SAVE_SHOWING_ERROR";
	public static final String SAVE_AUTO_UPDATE = "SAVE_AUTO_UPDATE";
	final int MODE_REFRESH = 1;
	final int MODE_REVERT = 2;
	final int MODE_INFO = 3;
	boolean showingError = false;
	boolean autoUpdate = false;
	boolean undo = false;
	int mode = MODE_INFO;
	private GlycemiaDataBinding glycemiaData;
	private CarbsDataBinding carbsData;
	private InsulinRegDataBinding insulinData;
	private InsulinCalculator insulinCalculator;
	private int noteId;
	private String date = "";
	private String time = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle args = getIntent().getExtras();
		if (args != null) {
			if (args.containsKey(ARG_CARBS)) {
				carbsData = args.getParcelable(ARG_CARBS);
			}
			if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
				glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
			}

			if (args.containsKey(ARG_INSULIN)) {
				insulinData = args.getParcelable(ARG_INSULIN);
			}

			noteId = -1;
			if (carbsData != null) {
				noteId = carbsData.getId_Note();
				date = carbsData.getDate();
				time = carbsData.getTime();
			} else if (glycemiaData != null) {
				noteId = glycemiaData.getIdNote();
				date = glycemiaData.getDate();
				time = glycemiaData.getTime();
			} else if (insulinData != null) {
				noteId = insulinData.getIdNote();
				date = insulinData.getDate();
				time = insulinData.getTime();
			}


			insulinCalculator = new InsulinCalculator(this);

			insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
			insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
			insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
			// get insulin before this one
			Calendar timeCalendar = TimePickerFragment.getCalendar(time);
			if (timeCalendar != null) {
				insulinCalculator.setTime(this, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE));
			}
		}
		super.onCreate(savedInstanceState);

		// set date
		fillDateHour(date, time);
		insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
		setupInsulinCalculator();

		// set insulin
		if (insulinData != null) {
			DB_Read rdb = new DB_Read(this);
			String insulinName = rdb.Insulin_GetById(insulinData.getIdInsulin()).getName();
			rdb.close();
			setInsulin(insulinName, insulinData.getInsulinUnits());
		}

		// set note
		if (noteId != -1) {
			DB_Read db_read = new DB_Read(this);
			String note = db_read.Note_GetById(noteId).getNote();
			db_read.close();
			setNote(note);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setLogo(R.drawable.ic_close_grey_100_48dp);
				actionBar.setHomeAsUpIndicator(R.drawable.actionbar_empty_space);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVE_AUTO_UPDATE, autoUpdate);
		outState.putBoolean(SAVE_SHOWING_ERROR, showingError);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_AUTO_UPDATE)) {
			autoUpdate = savedInstanceState.getBoolean(SAVE_AUTO_UPDATE);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_SHOWING_ERROR)) {
			showingError = savedInstanceState.getBoolean(SAVE_SHOWING_ERROR);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.logbook_detail_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_LogbookDetail_Delete) {
			final Context c = this;
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.deleteReading))
					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							DB_Write wdb = new DB_Write(c);
							try {
								wdb.Logbook_Delete(carbsData.getId(), insulinData.getId(), glycemiaData.getId(), noteId);
								finish();
							} catch (Exception e) {
								Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
							}
							wdb.close();
						}
					})
					.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Do nothing.
						}
					}).show();
			return true;
		} else if (item.getItemId() == R.id.menuItem_LogbookDetail_EditSave) {
			saveData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void glycemiaTargetChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed;
		if (insulinData != null) {
			changed = insulinData.getTargetGlycemia() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void glycemiaChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed;
		if (glycemiaData != null) {
			changed = glycemiaData.getValue() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void carbsChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed;
		if (carbsData != null) {
			changed = carbsData.getCarbsValue() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void insulinIntakeChanged(EditText view, String text) {
		float newVal = !text.isEmpty() ? Float.parseFloat(text) : 0;
		boolean changed;
		if (insulinData != null) {
			changed = Float.compare(insulinData.getInsulinUnits(), newVal) != 0;
		} else {
			changed = newVal != 0;
		}
//		updateIndicator(view, changed);
//		setInconsistentInsulin(changed);
		if (mode == MODE_INFO && changed) {
			setModeRevert();
		} else if (mode == MODE_REVERT && !changed) {
			setModeInfo();
		}
	}

	@Override
	protected void dateChanged(EditText view, String text) {
		boolean changed = !date.equals(text);
		updateIndicator(view, changed);
	}

	@Override
	protected void timeChanged(EditText view, String text) {
		updateIndicator(view, !time.equals(text));
		DB_Read rdb = new DB_Read(this);
		int d = (int) rdb.Target_GetTargetByTime(text);
		rdb.close();
		setGlycemiaTarget(d);
		// set time and load correct insulin for Insulin On Board
		Calendar time = TimePickerFragment.getCalendar(text);
		if (time != null) {
			insulinCalculator.setTime(this, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		}
	}

	private void updateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	private void setInconsistentInsulin(boolean changed) {
		boolean inconsistent = Float.compare(getInsulinCalculator().getInsulinTotal(useIOB, true), insulinData != null ? insulinData.getInsulinUnits() : 0) != 0;

		if (inconsistent && !showingError) {
			showingError = inconsistent;
			setModeRefresh();
		} else if (!inconsistent && showingError) {
			showingError = false;
			setModeInfo();
		} else if (autoUpdate) {
			setModeRevert();
			setInsulinIntake();
		}
	}

	void setModeRefresh() {
		findViewById(R.id.et_MealDetail_InsulinUnits).setBackgroundResource(R.drawable.edit_text_holo_dark_error);
		setToggleIconImage(R.drawable.ic_refresh_grey_400_24dp);
		undo = true;
		mode = MODE_REFRESH;
	}

	void setModeRevert() {
		findViewById(R.id.et_MealDetail_InsulinUnits).setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		setToggleIconImage(R.drawable.ic_refresh_invert_grey_400_24dp);
		mode = MODE_REVERT;
		autoUpdate = true;
	}

	void setModeInfo() {
		undo = false;
		autoUpdate = false;
		findViewById(R.id.et_MealDetail_InsulinUnits).setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		setToggleIconImage(android.R.drawable.ic_menu_info_details);
		if (fragmentInsulinCalcs != null) {
			((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setChecked(true);
		}
		mode = MODE_INFO;
	}

	void setToggleIconImage(int resource) {
		ToggleButton button = (ToggleButton) findViewById(R.id.bt_insulin_calc_info);
		button.setCompoundDrawables(null, null, null, null);
		ImageSpan imageSpan = new ImageSpan(this, resource);
		SpannableString content = new SpannableString("X");
		content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		button.setText(content);
		button.setTextOn(content);
		button.setTextOff(content);
		button.setChecked(false);
	}

	@Override
	public void toggleInsulinCalcDetails(View view) {
		switch (mode) {
			case MODE_INFO:
				super.toggleInsulinCalcDetails(view);
				break;
			case MODE_REFRESH:
				setModeRevert();
				showCalcs();
				setInsulinIntake();
				break;
			case MODE_REVERT:
				setModeRefresh();
				showCalcs();
				setInsulin(null, insulinData != null ? insulinData.getInsulinUnits() : 0);
				break;
		}
	}

	@Override
	boolean shouldSetInsulin() {
		return autoUpdate;
	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		return insulinCalculator;
	}

	public void saveData() {
		DB_Write reg = new DB_Write(this);
		boolean deleteCarbs = false;
		boolean deleteBg = false;
		boolean deleteIns = false;
		int itemsToDelete = 0;

		int user_id = -1;
		if (carbsData != null) {
			user_id = carbsData.getId_User(); // why is this method different from the other two?
		} else if (glycemiaData != null) {
			user_id = glycemiaData.getIdUser();
		} else if (insulinData != null) {
			user_id = insulinData.getIdUser();
		} else {
			// go to database and get it!
			// it shouldn't be necessary, if we are where some of this values are defined
			Log.e("DetailLogbookActivity", "carbsData, glycemiaData and insulinData are all null");
		}

		String date = getDate();
		String time = getTime();
		String note = getNote();
		Uri imgUri = getImgUri();

		// IDs
		int carbsRegId = 0;
		int glycemiaRegId = 0;
		int insulinRegId = 0;


		//nota id nao existe e tem de ser criado
		if (noteId == -1 && !note.isEmpty()) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note);
			noteId = reg.Note_Add(n);
		} else if (noteId != -1) {        //nota id existe e o texto foi apagado/alterado
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note);
			n.setId(noteId);
			reg.Note_Update(n);
		}


		//carbs
		//carbs id existe e foi apagado
		String tagSelected = getPhaseOfDay();
		DB_Read rdb = new DB_Read(this);
		int tagId = rdb.Tag_GetIdByName(tagSelected);
		rdb.close();

		if (carbsData != null && insulinCalculator.getCarbs() == 0) {
			deleteCarbs = true;
			itemsToDelete++;
			carbsRegId = carbsData.getId();
		} else if (carbsData == null && insulinCalculator.getCarbs() != 0) {//carbs id nao existe e tem de ser criad
			carbsData = new CarbsDataBinding();
			if (noteId != -1) {
				carbsData.setId_Note(noteId);
			}

			carbsData.setId_User(user_id);
			carbsData.setCarbsValue(insulinCalculator.getCarbs());
			carbsData.setId_Tag(tagId);
			carbsData.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
			carbsData.setDate(date);
			carbsData.setTime(time);

			reg.Carbs_Save(carbsData);
		} else if (carbsData != null && insulinCalculator.getCarbs() != 0) {//carbs id existe e valor está igual ou foi alterado
			carbsRegId = carbsData.getId();
			if (noteId != -1) {
				carbsData.setId_Note(noteId);
			}

			carbsData.setCarbsValue(insulinCalculator.getCarbs());
			carbsData.setId_Tag(tagId);
			carbsData.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
			carbsData.setDate(date);
			carbsData.setTime(time);

			reg.Carbs_Update(carbsData);
		}

		//glycemia
		//glycemia  exists and was deleted
		if (glycemiaData != null && insulinCalculator.getGlycemia() == 0) {
			glycemiaRegId = glycemiaData.getId();
			deleteBg = true;
			itemsToDelete++;
		} else if (glycemiaData == null && insulinCalculator.getGlycemia() != 0) {//bg id nao existe e tem de ser criado
			glycemiaData = new GlycemiaDataBinding();
			if (noteId != -1) {
				glycemiaData.setIdNote(noteId);
			}
			glycemiaData.setIdUser(carbsData.getId_User());
			glycemiaData.setValue(insulinCalculator.getGlycemia());
			glycemiaData.setDate(date);
			glycemiaData.setTime(time);

			glycemiaData.setIdTag(tagId);

			glycemiaRegId = reg.Glycemia_Save(glycemiaData);
		} else if (glycemiaData != null && insulinCalculator.getGlycemia() != 0) {        //bg id existe e valor está igual ou foi alterado
			glycemiaRegId = glycemiaData.getId();
			if (noteId != -1) {
				glycemiaData.setIdNote(noteId);
			}
			glycemiaData.setIdUser(carbsData.getId_User());
			glycemiaData.setValue(insulinCalculator.getGlycemia());
			glycemiaData.setDate(date);
			glycemiaData.setTime(time);
			glycemiaData.setIdTag(tagId);

			reg.Glycemia_Update(glycemiaData);
		}


		//insulin
		rdb = new DB_Read(this);
		int insulinId = rdb.Insulin_GetByName(getInsulin()).getId();
		rdb.close();
		float insulinIntake = getInsulinIntake();

		//ins id existe e foi apagado
		if (insulinData != null && Float.compare(insulinIntake, 0) == 0) {
			insulinRegId = insulinData.getId();
			deleteIns = true;
			itemsToDelete++;
		} else if (insulinData == null && Float.compare(insulinIntake, 0) != 0) {        //ins id nao existe e tem de ser criado
			insulinData = new InsulinRegDataBinding();
			if (noteId != -1) {
				insulinData.setIdNote(noteId);
			}

			insulinData.setIdUser(carbsData.getId_User());
			insulinData.setIdInsulin(insulinId);
			insulinData.setIdBloodGlucose(glycemiaRegId != -1 ? glycemiaRegId : -1);
			insulinData.setDate(date);
			insulinData.setTime(time);
			insulinData.setTargetGlycemia(insulinCalculator.getInsulinTarget());
			insulinData.setInsulinUnits(insulinIntake);

			insulinData.setIdTag(tagId);

			insulinRegId = reg.Insulin_Save(insulinData);
		} else if (insulinData != null && Float.compare(insulinIntake, 0) != 0) {//ins id existe e valor está igual ou foi alterado
			insulinRegId = insulinData.getId();
			if (noteId != -1) {
				insulinData.setIdNote(noteId);
			}

			insulinData.setIdInsulin(insulinId);
			insulinData.setIdBloodGlucose(glycemiaRegId != -1 ? glycemiaRegId : -1);
			insulinData.setDate(date);
			insulinData.setTime(time);
			insulinData.setTargetGlycemia(insulinCalculator.getInsulinTarget());
			insulinData.setInsulinUnits(insulinIntake);

			insulinData.setIdTag(tagId);

			reg.Insulin_Update(insulinData);
		}
		reg.close();

		if (deleteCarbs || deleteBg || deleteIns) {
			final Context c = this;
			String message;
			if (itemsToDelete == 1) {
				message = getString(R.string.deleteConfirmationSingular);
			} else {
				message = getString(R.string.deleteConfirmationPlural);
			}

			if (deleteBg) {
				message += "\n" + "- " + getString(R.string.deleteGlycemiaReg);
			}
			if (deleteCarbs) {
				message += "\n" + "- " + getString(R.string.deleteCarbsReg);
			}
			if (deleteIns) {
				message += "\n" + "- " + getString(R.string.deleteInsulinReg);
			}

			final boolean delCh = deleteCarbs;
			final boolean delBg = deleteBg;
			final boolean delIns = deleteIns;

			final int finalCarbsRegId = carbsRegId;
			final int finalInsulinRegId = insulinRegId;
			final int finalGlycemiaRegId = glycemiaRegId;
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.deleteAlert))
					.setMessage(message)
					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							try {
								DB_Write wdb = new DB_Write(c);
								if (delCh && delBg && delIns) {
									wdb.Logbook_Delete(finalCarbsRegId, finalInsulinRegId, finalGlycemiaRegId, noteId);
								} else {
									if (delCh) {
										wdb.Logbook_DeleteOnSave(finalCarbsRegId, -1, -1, -1);
									}
									if (delBg) {
										if (insulinData != null) {
											wdb.Logbook_DeleteOnSave(-1, -1, finalGlycemiaRegId, finalInsulinRegId);

										} else {
											wdb.Logbook_DeleteOnSave(-1, -1, finalGlycemiaRegId, -1);
										}
									}
									if (delIns) {
										wdb.Logbook_DeleteOnSave(-1, finalInsulinRegId, -1, -1);
									}
								}
								wdb.close();
								finish();
							} catch (Exception e) {

								Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
								Log.d("Excepção", e.getMessage());
								//Log.d("LocalizedMessage",e.getLocalizedMessage());
								//Log.d("trace",e.getStackTrace().toString());
								//e.printStackTrace();

							}


						}
					})
					.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Do nothing.
						}
					}).show();
		} else {
			finish();
		}
	}

}
