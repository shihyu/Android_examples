package mobile.android.ch05.expandable.listview;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ExpandableListActivity
{
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ExpandableListAdapter adapter = new MyExpandableListAdapter();
		setListAdapter(adapter);
		registerForContextMenu(getExpandableListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo)
	{

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		String title = ((TextView) info.targetView).getText().toString();
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
		{
			menu.setHeaderTitle("�X�{���");
			menu.add(0, 0, 0, title);

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();
		String title = ((TextView) info.targetView).getText().toString();

		Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
		return true;

	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter
	{
		private String[] provinces =
		{ "���", "�s�F", "����", "�|�t" };
		private String[][] cities =
		{
		{ "�n��", "�j�s", "�b�s", "����" },
		{ "�٫n", "�C�q", "�d��", "�ǲ�" },
		{ "�n��", "���w��" },
		{ "����", "�۰^", "�k�K��" } };

		public Object getChild(int groupPosition, int childPosition)
		{
			return cities[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		public int getChildrenCount(int groupPosition)
		{
			return cities[groupPosition].length;
		}

		public TextView getGenericView()
		{
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(Main.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setPadding(36, 0, 0, 0);
			textView.setTextSize(20);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent)
		{
			TextView textView = getGenericView();			
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition)
		{
			return provinces[groupPosition];
		}

		public int getGroupCount()
		{
			return provinces.length;
		}

		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent)
		{
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		public boolean hasStableIds()
		{
			return true;
		}

	}
}