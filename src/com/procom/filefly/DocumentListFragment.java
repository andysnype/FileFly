package com.procom.filefly;

import java.util.List;

import com.procom.filefly.model.Document;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DocumentListFragment extends Fragment
{
	private List<Document> documents;
	private DocumentListAdapter listadapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.document_list_fragment, container, false);
        return rootView;
	}
}