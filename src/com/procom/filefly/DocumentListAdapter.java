package com.procom.filefly;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.procom.filefly.model.Document;

public class DocumentListAdapter extends BaseAdapter
{
	private Context _context;
	private List<Document> _documents;
	
	public DocumentListAdapter(Context context, List<Document> documents)
	{
		_context = context;
		_documents = documents;
	}

	@Override
	public int getCount()
	{
		return _documents.size();
	}

	@Override
	public Object getItem(int index)
	{
		return _documents.get(index);
	}

	@Override
	public long getItemId(int index)
	{
		return index;
	}

	@Override
	public View getView(final int index, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// TODO: implement this function
		return null;
	}
}