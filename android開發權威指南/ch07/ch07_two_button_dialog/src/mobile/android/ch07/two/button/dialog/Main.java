package mobile.android.ch07.two.button.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class Main extends Activity
{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		new AlertDialog.Builder(this).setIcon(R.drawable.question).setTitle(
		"�O�_�U���ɮ�").setPositiveButton("�T�w",
		new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"�ɮפw�g���\�U��.").create().show();
			}
		}).setNegativeButton("����",
		new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{

				new AlertDialog.Builder(Main.this).setMessage(
						"�z�w�g��ܤF�������s�A���ɮץ��Q�U��.").create().show();
			}
		}).show();
		
	}
}