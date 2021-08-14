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
			"�Ѧa�k��",
			"�O���q��",
			"�å@�ΤH(��)",
			"�ǫL�@�K��",
			"�Ĥ��Ŷ�",
			"�ճ���",
			"�ܧΪ���3�]�u�H���^",
			"�P�ڶǩ_",
			"�m�j������n�@���A�p�c�K�O�p�n���A�L�`�j�X�Y�A�Ѥ����Z�\���泾�ܦ��Z�L�Ĥ@����F�����s�h�O�ӪZ�\���j���j�s�A�Q���|�Z�\���p�n�����ѡF�{����t�����s����U�֥C�A�g�`��ֻ����s�C ��l���ⳣ��¶�p�n���]�w�C" };

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

		// �NArrayAdapter�غc��k���̫�@�ӰѼƧ令dataList�A�t�δN�|���JList��H�����
		// List<String> dataList = new ArrayList<String>();
		// dataList.add("�����ƨ�");
		// dataList.add("���P�u�@");
		ArrayAdapter<String> aaData = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data);

		lvCommonListView.setAdapter(aaData);
	//	lvCommonListView.setSelection(6);
		lvCommonListView.setOnItemClickListener(this);
		lvCommonListView.setOnItemSelectedListener(this);

	}

}
