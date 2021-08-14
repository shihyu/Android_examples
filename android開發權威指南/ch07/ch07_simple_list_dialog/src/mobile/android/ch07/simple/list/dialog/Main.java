package mobile.android.ch07.simple.list.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;

public class Main extends Activity
{
	private String[] provinces = new String[]
	{ "����", "�s�F��", "�e�_��", "�֫ج�", "�s�F��", "���s����" };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new AlertDialog.Builder(this).setTitle("��ܬ٥�")
				.setItems(provinces, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						final AlertDialog ad = new AlertDialog.Builder(
								Main.this).setMessage(
								"�z�w�g��ܤF: " + which + ":" + provinces[which])
								.show();
						android.os.Handler hander = new android.os.Handler();
						// �]�w�w�ɾ��A5���I�srun��k
						hander.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								// �I�sAlertDialog���O��dismiss��k������͵����A�]�i�H�I�scancel��k
								ad.dismiss();
							} 
						}, 5 * 1000);
					}
				}).show();

	}
}