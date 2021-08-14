package mobile.android.ch07.multi.choice.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class Main extends Activity
{
	private String[] provinces = new String[]
	{ "����", "�s�F��", "�e�_��", "�֫ج�", "�s�F��", "���s����" };
	private ListView lv = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick_MultiChoiceDialog(View view)
	{
		
		AlertDialog ad = new AlertDialog.Builder(this)
				.setIcon(R.drawable.image)
				.setTitle("��ܬ٥�")
				.setMultiChoiceItems(provinces, new boolean[]
				{ false, true, false, true, false, false },
						new DialogInterface.OnMultiChoiceClickListener()
						{
							public void onClick(DialogInterface dialog,
									int whichButton, boolean isChecked)
							{

							}
						})
				.setPositiveButton("�T�w", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						int count = lv.getCount();
						String s = "�z��ܤF:";
						for (int i = 0; i < provinces.length; i++)
						{

							if (lv.getCheckedItemPositions().get(i))
								s += i + ":" + lv.getAdapter().getItem(i)
										+ "  ";

						}
						if (lv.getCheckedItemPositions().size() > 0)
						{
							new AlertDialog.Builder(Main.this).setMessage(s)
									.show();
						}
						else
						{
							new AlertDialog.Builder(Main.this).setMessage(
									"�z����ܥ���٥�").show();

						}

					}
				}).setNegativeButton("����", null).create();
		lv = ad.getListView();
		ad.show();
	}
}