package mobile.android.ch05.listview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Main extends Activity implements OnItemSelectedListener,
		OnItemClickListener
{
	private static String[] data = new String[]
	{
			"天地逃生",
			"保持通話",
			"亂世佳人(飄)",
			"怪俠一枝梅",
			"第五空間",
			"孔雀翎",
			"變形金剛3（真人版）",
			"星際傳奇",
			"《大笑江湖》劇中，小鞋匠是小瀋陽，他常強出頭，由不懂武功的菜鳥變成武林第一高手；趙本山則是個武功高強的大盜，被不會武功的小瀋陽打敗；程野扮演趙本山的手下皮丘，經常拖累趙本山。 其餘角色都圍繞小瀋陽設定。" };

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		Log.d("itemclick", "click " + position + " item");

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id)
	{
		Log.d("itemselected", "select " + position + " item");
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		Log.d("nothingselected", "nothing selected");

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ListView lvCommonListView = (ListView) findViewById(R.id.lvCommonListView);

		// 將ArrayAdapter建構方法的最後一個參數改成dataList，系統就會載入List對象的資料
		// List<String> dataList = new ArrayList<String>();
		// dataList.add("機器化身");
		// dataList.add("火星工作");
		ArrayAdapter<String> aaData = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data);

		lvCommonListView.setAdapter(aaData);
	//	lvCommonListView.setSelection(6);
		lvCommonListView.setOnItemClickListener(this);
		lvCommonListView.setOnItemSelectedListener(this);

	}

}
