package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.HbA1cDetail;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class HbA1cAdapter extends BaseAdapter {

	Context _c;
	private Cursor cursor;

	public HbA1cAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		_c = c;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public HbA1cReg getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new HbA1cReg(cursor.getInt(pox++), cursor.getString(pox++), cursor.getFloat(pox));
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).id;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_hba1c_row, parent, false);
			v.setTag(new ViewHolder(v));
		}

		ViewHolder viewHolder = (ViewHolder) v.getTag();

		HbA1cReg bp = getItem(position);
		viewHolder.item = bp;

		viewHolder.date.setText(bp.getFormattedDate());
		viewHolder.time.setText(bp.getFormattedTime());
		viewHolder.value.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", bp.value));


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), HbA1cDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((ViewHolder) v.getTag()).item.id));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}


	private class HbA1cReg {
		int id;
		Calendar dateTime;
		float value;

		public HbA1cReg(int id, String dateTime, float value) {
			this.id = id;
			try {
				this.dateTime = DateUtils.parseDateTime(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.value = value;
		}


		public String getFormattedDate() {
			return DateUtils.getFormattedDate(dateTime);
		}

		public String getFormattedTime() {
			return DateUtils.getFormattedTime(dateTime);
		}
	}

	private class ViewHolder {
		TextView date;
		TextView time;
		TextView value;
		HbA1cReg item;

		public ViewHolder(View view) {
			date = (TextView) view.findViewById(R.id.tv_list_hba1c_data);
			time = (TextView) view.findViewById(R.id.tv_list_hba1c_hora);
			value = (TextView) view.findViewById(R.id.tv_list_hba1c_value);
		}
	}

}