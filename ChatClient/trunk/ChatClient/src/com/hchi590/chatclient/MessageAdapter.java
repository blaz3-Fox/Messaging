/**
 * 
 */
package com.hchi590.chatclient;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author hchi590 Custom ArrayAdapter to display message according to whether
 *         the message is sent by this client on received from another client.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

	private ArrayList<Message> _messages;
	private String _name = "c";

	private static final int SELF = 0;
	private static final int OTHER = 1;

	public MessageAdapter(Context context, int textViewResourceId,
			ArrayList<Message> objects) {
		super(context, textViewResourceId, objects);
		_messages = objects;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public int getItemViewType(int position) {
		String author = _messages.get(position).getAuthor();
		if (author.equals(_name)) {
			return SELF;
		} else {
			return OTHER;
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Message message = _messages.get(position);

		int type = getItemViewType(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (type) {
			case SELF:
				convertView = inflater.inflate(R.layout.row_self, null);
				break;
			case OTHER:
				convertView = inflater.inflate(R.layout.row_other, null);
				break;
			}
		}

		if (message != null) {
			TextView m = (TextView) convertView.findViewById(R.id.message);
			TextView a = (TextView) convertView.findViewById(R.id.author);

			if (a != null) {
				a.setText(message.getAuthor());
			}
			
			if (m != null) {
				m.setText(message.getMessage());
			}
		}

		return convertView;
	}
}