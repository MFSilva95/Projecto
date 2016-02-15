package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.TagDetail;
import pt.it.porto.mydiabetes.ui.dataBinding.TagDataBinding;


public class TagAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<TagDataBinding> _data;

	public TagAdapter(ArrayList<TagDataBinding> data, Context c) {
		_data = data;
		_c = c;
	}


	@Override
	public int getCount() {
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_tag_row, parent, false);
		}


		LinearLayout rLayout = (LinearLayout) v.findViewById(R.id.FaseDiaRow);


		TextView tagName = (TextView) v.findViewById(R.id.list_tagName);
		TextView tagStart = (TextView) v.findViewById(R.id.list_tagStart);
		TextView tagEnd = (TextView) v.findViewById(R.id.list_tagEnd);


		TagDataBinding tag = _data.get(position);
		rLayout.setTag(tag);
		tagName.setText(tag.getName());
		tagStart.setText(tag.getStart());
		tagEnd.setText(tag.getEnd());


		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), TagDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((TagDataBinding) v.getTag()).getId()));
				args.putParcelable(TagDetail.DATA, ((TagDataBinding) v.getTag()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});


		return v;
	}

}
