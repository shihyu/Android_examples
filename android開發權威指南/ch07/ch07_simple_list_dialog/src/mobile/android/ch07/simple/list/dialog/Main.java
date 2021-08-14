package mobile.android.ch07.simple.list.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;

public class Main extends Activity
{
	private String[] provinces = new String[]
	{ "遼寧省", "山東省", "河北省", "福建省", "廣東省", "黑龍江省" };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new AlertDialog.Builder(this).setTitle("選擇省份")
				.setItems(provinces, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						final AlertDialog ad = new AlertDialog.Builder(
								Main.this).setMessage(
								"您已經選擇了: " + which + ":" + provinces[which])
								.show();
						android.os.Handler hander = new android.os.Handler();
						// 設定定時器，5秒後呼叫run方法
						hander.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								// 呼叫AlertDialog類別的dismiss方法關閉交談視窗，也可以呼叫cancel方法
								ad.dismiss();
							} 
						}, 5 * 1000);
					}
				}).show();

	}
}