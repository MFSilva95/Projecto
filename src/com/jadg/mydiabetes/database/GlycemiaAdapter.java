package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jadg.mydiabetes.GlycemiaDetail;
import com.jadg.mydiabetes.R;


public class GlycemiaAdapter extends BaseAdapter {

	private ArrayList<GlycemiaDataBinding> _data;
    Context _c;
    
    public GlycemiaAdapter (ArrayList<GlycemiaDataBinding> data, Context c){
        _data = data;
        _c = c;
    }
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
         if (v == null)
         {
            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_glycemia_row, null);
         }
 
           TextView data = (TextView)v.findViewById(R.id.tv_list_glicemia_data);
           TextView hora = (TextView)v.findViewById(R.id.tv_list_glicemia_hora);
           TextView value = (TextView)v.findViewById(R.id.tv_list_glicemia_value);
           final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_glicemia_detail);
           
           final GlycemiaDataBinding glycemia = _data.get(position);
           final String _id = ""+glycemia.getId();
           data.setText(glycemia.getDate());
           hora.setText(glycemia.getTime());
           value.setTag(_id);
           value.setText(glycemia.getValue().toString());
           viewdetail.setTag(_id);
           
           
           viewdetail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", _id); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        	   
           });
                                     
                        
        return v;
	}
	
}
