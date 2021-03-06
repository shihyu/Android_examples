package mobile.android.ch07.single.choice.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

public class Main extends Activity
{
	private String[] provinces = new String[]
	{ "遼寧省", "山東省", "河北省", "福建省", "廣東省", "黑龍江省" };
	private int index;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


	}
	public void onClick_SingleChoiceDialog(View view)
	{
		new AlertDialog.Builder(this).setTitle("選擇省份")
		.setSingleChoiceItems(provinces, -1, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				index = which;

			}
		}).setPositiveButton("確定", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"您已經選擇了： " + index + ":" + provinces[index])
						.show();

			}
		}).setNegativeButton("取消", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"您什麼都未選擇.").show();

			}
		}).show();
	}
}