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

	// 為按鈕增加單擊事件，註冊上下文選單
	private void init()
	{
		Button button = (Button) findViewById(R.id.btnAddMenu);
		button.setOnClickListener(this);
		EditText editText = (EditText) findViewById(R.id.edittext);
		TextView textView = (TextView) findViewById(R.id.textview);
		// 註冊上下文選單
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

	// 向Activity選單增加10個選單項，選單項的id從10開始
	@Override
	public void onClick(View view)
	{
		if (menu == null)
			return;
		for (int i = 10; i < 15; i++)
		{
			int id = menuItemId++;
			menu.add(1, id, id, "選單" + i);
		}
	}

	private void showDialog(String message)
	{
		new AlertDialog.Builder(this).setMessage("您單擊了【" + message + "】選單項.")
				.show();

	}

	// 單擊Activity選單、子選單、上下文選單的選單項時呼叫該方法
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		Log.d("onMenuItemClick", "true");
		showDialog(item.getTitle().toString());
		return false;
	}

	// 向Activity選單增加3個選單項
	private void addMenu(Menu menu)
	{

		MenuItem addMenuItem = menu.add(1, menuItemId++, 1, "增加");
		addMenuItem.setIntent(new Intent(this, AddActivity.class));
		
	//	addMenuItem.setOnMenuItemClickListener(this);
		
		MenuItem deleteMenuItem = menu.add(1, menuItemId++, 2, "刪除");
		deleteMenuItem.setIcon(R.drawable.delete);
		deleteMenuItem.setOnMenuItemClickListener(this);
		MenuItem menuItem1 = menu.add(1, menuItemId++, 3, "選單1");
		menuItem1.setOnMenuItemClickListener(this);
		MenuItem menuItem2 = menu.add(1, menuItemId++, 4, "選單2");
		MenuItem menuItem3 = menu.add(1, menuItemId++, 4, "選單3");
		MenuItem menuItem4 = menu.add(1, menuItemId++, 4, "選單4");

	}

	private void addSubMenu(Menu menu)
	{
		// 增加子選單
		SubMenu fileSubMenu = menu.addSubMenu(1, menuItemId++, 0, "檔案");

		fileSubMenu.setIcon(R.drawable.file);
		fileSubMenu.setHeaderIcon(R.drawable.headerfile);
		// 子選單不支援影像
		MenuItem newMenuItem = fileSubMenu.add(1, menuItemId++, 1, "新增");
	
		newMenuItem.setCheckable(true);
		newMenuItem.setChecked(true);
		MenuItem openMenuItem = fileSubMenu.add(2, menuItemId++, 2, "開啟");
		MenuItem exitMenuItem = fileSubMenu.add(2, menuItemId++, 3, "退出");
		exitMenuItem.setChecked(true);
		
		fileSubMenu.setGroupCheckable(2, true, false);

	}

	// 單擊Menu按鈕時呼叫該方法來建立Activity選單
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.menu = menu;
		addMenu(menu);
		addSubMenu(menu);

		// //////////////////錯誤，拋出 10-03 13:17:32.489: DEBUG/e(1667): Attempt to
		// add a sub-menu to a sub-menu.

		// SubMenu subMenu = fileSubMenu.addSubMenu(3, 1, 1, "子選單");
		// subMenu.add(1, 1, 1, "選單項1");
		// subMenu.add(1, 2, 2, "選單項2");
		// ////////////////////

		return super.onCreateOptionsMenu(menu);
	}

	// 在Activity選單顯示前呼叫該方法，可以在這個方法中修改指定的選單項
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		Log.d("onPrepareOptionsMenu", "true");
		return super.onPrepareOptionsMenu(menu);
	}

	// 當上下文選單關閉時呼叫該方法
	@Override
	public void onContextMenuClosed(Menu menu)
	{
		Log.d("onContextMenuClosed", "true");
		super.onContextMenuClosed(menu);
	}

	// 當Activity選單顯示時呼叫該方法，這個方法在 onPrepareOptionsMenu之後被呼叫
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		Log.d("onMenuOpened", "true");
		return super.onMenuOpened(featureId, menu);
	}

	// 當Activity選單被關閉時呼叫該方法
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
		if ("選單1".equals(item.getTitle()))
			showDialog("<" + item.getTitle().toString() + ">");
		else if ("選單2".equals(item.getTitle()))
			showDialog("<" + item.getTitle().toString() + ">");
		return false;
	}

	// 單擊每一個Activity選單項時呼叫該方法
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log
				.d("onOptionsItemSelected:itemid=", String.valueOf(item
						.getItemId()));
		return true;
	}

	// 單擊上下文選單的某個選單項時呼叫該方法
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		Log
				.d("onContextItemSelected:itemid=", String.valueOf(item
						.getItemId()));
		if (!"子選單".equals(item.getTitle().toString()))
			showDialog("*" + item.getTitle().toString() + "*");
		return super.onContextItemSelected(item);
	}

	// 顯示上下文選單時呼叫該方法來增加自定義的上下文選單項
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, view, menuInfo);
		// menu.clear();
		menu.setHeaderTitle("上下文選單");
		menu.setHeaderIcon(R.drawable.face);
		menu.add(0, menuItemId++, Menu.NONE, "選單項1").setCheckable(true)
				.setChecked(true);
		menu.add(20, menuItemId++, Menu.NONE, "選單項2");
		menu.add(20, menuItemId++, Menu.NONE, "選單項3").setChecked(true);
		menu.setGroupCheckable(20, true, true);
		SubMenu sub = menu.addSubMenu(0, menuItemId++, Menu.NONE, "子選單");
		sub.add("子選單項1");
		sub.add("子選單項2");
		getMenuInflater().inflate(R.menu.file_menu, menu);
		
		

	}

}
