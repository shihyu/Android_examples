package mobile.android.ch05.color.listview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

public class Main extends Activity implements OnClickListener
		
{
	private Drawable defaultSelector;
	private ListView listView;

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.rbdefault:
				listView.setSelector(defaultSelector);
				break;
			case R.id.rbGreen:
				listView.setSelector(R.drawable.green);
				break;
			case R.id.rbSpectrum:
				listView.setSelector(R.drawable.spectrum);
				break;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String[] data = new String[]
		{ "�p���", "�^�y", "����", "�ƾ�" };
		ArrayAdapter<String> aaAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(aaAdapter);
		defaultSelector = listView.getSelector();

		RadioButton rbDefault = (RadioButton) findViewById(R.id.rbdefault);
		rbDefault.setNextFocusDownId(R.id.listview);
		rbDefault.setOnClickListener(this);
		RadioButton rbGreen = (RadioButton) findViewById(R.id.rbGreen);
		rbGreen.setNextFocusDownId(R.id.listview);
		rbGreen.setOnClickListener(this);
		RadioButton rbSpectrum = (RadioButton) findViewById(R.id.rbSpectrum);
		rbSpectrum.setNextFocusDownId(R.id.listview);
		rbSpectrum.setOnClickListener(this);

	}
}