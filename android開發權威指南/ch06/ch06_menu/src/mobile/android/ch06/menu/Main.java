package mobile.android.ch06.menu;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends Activity implements OnMenuItemClickListener,
		OnClickListener
{
	private Menu menu;
	private int menuItemId = Menu.FIRST;

	// �����s�W�[�����ƥ�A���U�W�U����
	private void init()
	{
		Button button = (Button) findViewById(R.id.btnAddMenu);
		button.setOnClickListener(this);
		EditText editText = (EditText) findViewById(R.id.edittext);
		TextView textView = (TextView) findViewById(R.id.textview);
		// ���U�W�U����
		registerForContextMenu(button);
		registerForContextMenu(editText);
		registerForContextMenu(textView);

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	// �VActivity���W�[10�ӿ�涵�A��涵��id�q10�}�l
	@Override
	public void onClick(View view)
	{
		if (menu == null)
			return;
		for (int i = 10; i < 15; i++)
		{
			int id = menuItemId++;
			menu.add(1, id, id, "���" + i);
		}
	}

	private void showDialog(String message)
	{
		new AlertDialog.Builder(this).setMessage("�z�����F�i" + message + "�j��涵.")
				.show();

	}

	// ����Activity���B�l���B�W�U���檺��涵�ɩI�s�Ӥ�k
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		Log.d("onMenuItemClick", "true");
		showDialog(item.getTitle().toString());
		return false;
	}

	// �VActivity���W�[3�ӿ�涵
	private void addMenu(Menu menu)
	{

		MenuItem addMenuItem = menu.add(1, menuItemId++, 1, "�W�[");
		addMenuItem.setIntent(new Intent(this, AddActivity.class));
		
	//	addMenuItem.setOnMenuItemClickListener(this);
		
		MenuItem deleteMenuItem = menu.add(1, menuItemId++, 2, "�R��");
		deleteMenuItem.setIcon(R.drawable.delete);
		deleteMenuItem.setOnMenuItemClickListener(this);
		MenuItem menuItem1 = menu.add(1, menuItemId++, 3, "���1");
		menuItem1.setOnMenuItemClickListener(this);
		MenuItem menuItem2 = menu.add(1, menuItemId++, 4, "���2");
		MenuItem menuItem3 = menu.add(1, menuItemId++, 4, "���3");
		MenuItem menuItem4 = menu.add(1, menuItemId++, 4, "���4");

	}

	private void addSubMenu(Menu menu)
	{
		// �W�[�l���
		SubMenu fileSubMenu = menu.addSubMenu(1, menuItemId++, 0, "�ɮ�");

		fileSubMenu.setIcon(R.drawable.file);
		fileSubMenu.setHeaderIcon(R.drawable.headerfile);
		// �l��椣�䴩�v��
		MenuItem newMenuItem = fileSubMenu.add(1, menuItemId++, 1, "�s�W");
	
		newMenuItem.setCheckable(true);
		newMenuItem.setChecked(true);
		MenuItem openMenuItem = fileSubMenu.add(2, menuItemId++, 2, "�}��");
		MenuItem exitMenuItem = fileSubMenu.add(2, menuItemId++, 3, "�h�X");
		exitMenuItem.setChecked(true);
		
		fileSubMenu.setGroupCheckable(2, true, false);

	}

	// ����Menu���s�ɩI�s�Ӥ�k�ӫإ�Activity���
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.menu = menu;
		addMenu(menu);
		addSubMenu(menu);

		// //////////////////���~�A�ߥX 10-03 13:17:32.489: DEBUG/e(1667): Attempt to
		// add a sub-menu to a sub-menu.

		// SubMenu subMenu = fileSubMenu.addSubMenu(3, 1, 1, "�l���");
		// subMenu.add(1, 1, 1, "��涵1");
		// subMenu.add(1, 2, 2, "��涵2");
		// ////////////////////

		return super.onCreateOptionsMenu(menu);
	}

	// �bActivity�����ܫe�I�s�Ӥ�k�A�i�H�b�o�Ӥ�k���ק���w����涵
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		Log.d("onPrepareOptionsMenu", "true");
		return super.onPrepareOptionsMenu(menu);
	}

	// ��W�U���������ɩI�s�Ӥ�k
	@Override
	public void onContextMenuClosed(Menu menu)
	{
		Log.d("onContextMenuClosed", "true");
		super.onContextMenuClosed(menu);
	}

	// ��Activity�����ܮɩI�s�Ӥ�k�A�o�Ӥ�k�b onPrepareOptionsMenu����Q�I�s
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		Log.d("onMenuOpened", "true");
		return super.onMenuOpened(featureId, menu);
	}

	// ��Activity���Q�����ɩI�s�Ӥ�k
	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		Log.d("onOptionsMenuClosed", "true");
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		super.onMenuItemSelected(featureId, item);
		Log.d("onMenuItemSelected:itemId=", String.valueOf(item.getItemId()));
		if ("���1".equals(item.getTitle()))
			showDialog("<" + item.getTitle().toString() + ">");
		else if ("���2".equals(item.getTitle()))
			showDialog("<" + item.getTitle().toString() + ">");
		return false;
	}

	// �����C�@��Activity��涵�ɩI�s�Ӥ�k
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log
				.d("onOptionsItemSelected:itemid=", String.valueOf(item
						.getItemId()));
		return true;
	}

	// �����W�U���檺�Y�ӿ�涵�ɩI�s�Ӥ�k
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		Log
				.d("onContextItemSelected:itemid=", String.valueOf(item
						.getItemId()));
		if (!"�l���".equals(item.getTitle().toString()))
			showDialog("*" + item.getTitle().toString() + "*");
		return super.onContextItemSelected(item);
	}

	// ��ܤW�U����ɩI�s�Ӥ�k�ӼW�[�۩w�q���W�U���涵
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, view, menuInfo);
		// menu.clear();
		menu.setHeaderTitle("�W�U����");
		menu.setHeaderIcon(R.drawable.face);
		menu.add(0, menuItemId++, Menu.NONE, "��涵1").setCheckable(true)
				.setChecked(true);
		menu.add(20, menuItemId++, Menu.NONE, "��涵2");
		menu.add(20, menuItemId++, Menu.NONE, "��涵3").setChecked(true);
		menu.setGroupCheckable(20, true, true);
		SubMenu sub = menu.addSubMenu(0, menuItemId++, Menu.NONE, "�l���");
		sub.add("�l��涵1");
		sub.add("�l��涵2");
		getMenuInflater().inflate(R.menu.file_menu, menu);
		
		

	}

}
