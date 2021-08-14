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
	{ "����", "�s�F��", "�e�_��", "�֫ج�", "�s�F��", "���s����" };
	private int index;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


	}
	public void onClick_SingleChoiceDialog(View view)
	{
		new AlertDialog.Builder(this).setTitle("��ܬ٥�")
		.setSingleChoiceItems(provinces, -1, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				index = which;

			}
		}).setPositiveButton("�T�w", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"�z�w�g��ܤF�G " + index + ":" + provinces[index])
						.show();

			}
		}).setNegativeButton("����", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"�z���򳣥����.").show();

			}
		}).show();
	}
}