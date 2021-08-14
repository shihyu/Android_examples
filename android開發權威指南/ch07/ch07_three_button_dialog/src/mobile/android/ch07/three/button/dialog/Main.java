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
        "是否覆蓋檔案？").setPositiveButton("覆蓋",
        new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this)
                        .setMessage("檔案已經覆蓋.").create().show();
            }
        }).setNeutralButton("忽略", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this).setMessage("忽略了覆蓋檔案的操作.")
                    .create().show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                new AlertDialog.Builder(Main.this).setMessage("您已經取消了所有的操作.").
                    create().show();
            }
        }).show();

		
	}
}