/**
 * 
 */
package com.jadg.mydiabetes.ui.fragments;

import android.view.LayoutInflater;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.jadg.mydiabetes.R;

/**
 * @author artur
 *
 */


public class Sync extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_sync, null);
//		FillDates(v);
		

		return v;
	}

}
