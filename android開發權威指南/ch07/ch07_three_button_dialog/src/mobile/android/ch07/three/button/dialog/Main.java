package mobile.android.ch07.three.button.dialog;

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
        "�O�_�л\�ɮסH").setPositiveButton("�л\",
        new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this)
                        .setMessage("�ɮפw�g�л\.").create().show();
            }
        }).setNeutralButton("����", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this).setMessage("�����F�л\�ɮת��ާ@.")
                    .create().show();
            }
        }).setNegativeButton("����", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this).setMessage("�z�w�g�����F�Ҧ����ާ@.").
                    create().show();
            }
        }).show();

		
	}
}