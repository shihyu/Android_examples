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
		"是否下載檔案").setPositiveButton("確定",
		new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				new AlertDialog.Builder(Main.this).setMessage(
						"檔案已經成功下載.").create().show();
			}
		}).setNegativeButton("取消",
		new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{

				new AlertDialog.Builder(Main.this).setMessage(
						"您已經選擇了取消按鈕，該檔案未被下載.").create().show();
			}
		}).show();
		
	}
}