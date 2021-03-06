package mobile.android.ch05.dynamic.checkbox;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class Main extends Activity implements OnClickListener
{
	private List<CheckBox> checkboxs = new ArrayList<CheckBox>();

	@Override
	public void onClick(View view)
	{
		String s = "";
		for (CheckBox checkbox : checkboxs)
		{
			if (checkbox.isChecked())
				s += checkbox.getText() + "\n";
		}
		if ("".equals(s))
			s = "您還沒選呢！";
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton("關閉", null).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		String[] checkboxText = new String[]
		{ "是學生嗎？", "是否喜歡Android？", "買車了嗎？", "打算出國嗎？" };
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.main, null);
		for (int i = 0; i < checkboxText.length; i++)
		{
			CheckBox checkbox = (CheckBox) getLayoutInflater().inflate(
					R.layout.checkbox, null);
			checkboxs.add(checkbox);
			checkboxs.get(i).setText(checkboxText[i]);

			linearLayout.addView(checkbox, i);
		}
		setContentView(linearLayout);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);

	}
}