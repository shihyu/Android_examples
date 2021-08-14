package mobile.android.ch04.transmit.data;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyActivity1 extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myactivity);
		TextView textView = (TextView)findViewById(R.id.textview);
		
		String intentString =  getIntent().getStringExtra("intent_string");
		int intentInteger = getIntent().getExtras().getInt("intent_integer");
		Data data = (Data)getIntent().getExtras().get("intent_object");
		
		StringBuffer sb = new StringBuffer();
		sb.append("intent_string¡G");
		sb.append(intentString);
		sb.append("\n");
		sb.append("intent_integer¡G");
		sb.append(intentInteger);
		sb.append("\n");
		sb.append("data.id¡G");
		sb.append(data.id);
		sb.append("\n");
		sb.append("data.name¡G");
		sb.append(data.name);
		sb.append("\n");
		
		textView.setText(sb.toString());
	}

}
