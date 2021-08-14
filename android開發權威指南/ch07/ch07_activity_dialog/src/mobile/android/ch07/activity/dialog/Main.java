package mobile.android.ch07.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Main extends Activity implements OnClickListener
{
	private final int DIALOG_DELETE_FILE = 1;
	private final int DIALOG_SIMPLE_LIST = 2;
	private final int DIALOG_SINGLE_CHOICE_LIST = 3;
	private final int DIALOG_MULTI_CHOICE_LIST = 4;	
	private ListView lv = null;
	private String[] provinces = new String[]
	{ "����", "�s�F��", "�e�_��", "�֫ج�", "�s�F��", "���s����" };
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btnDeleteFile:
				showDialog(DIALOG_DELETE_FILE);
				break;
			case R.id.btnSimpleList:
				showDialog(DIALOG_SIMPLE_LIST);
				break;
			case R.id.btnSingleChoiceList:
				showDialog(DIALOG_SINGLE_CHOICE_LIST);
				break;
			case R.id.btnMultiChoiceList:
				showDialog(DIALOG_MULTI_CHOICE_LIST);
				break;
			case R.id.btnRemoveDialog:
				removeDialog(DIALOG_DELETE_FILE);
				removeDialog(DIALOG_SIMPLE_LIST);
				removeDialog(DIALOG_SINGLE_CHOICE_LIST);
				removeDialog(DIALOG_MULTI_CHOICE_LIST);
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		Log.d("dialog", String.valueOf(id));
		switch (id)
		{
			case DIALOG_DELETE_FILE:
				return new AlertDialog.Builder(this).setIcon(
						R.drawable.question).setTitle("�O�_�R���ɮ�")
						.setPositiveButton("�T�w",
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int whichButton)
									{
										new AlertDialog.Builder(Main.this)
												.setMessage("�ɮפw�g�Q�R��.")
												.create().show();
									}
								}).setNegativeButton("����",
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int whichButton)
									{

										new AlertDialog.Builder(Main.this)
												.setMessage(
														"�z�w�g��ܤF�������s�A���ɮץ��Q�R��.")
												.create().show();
									}
								}).create();

			case DIALOG_SIMPLE_LIST:
				return new AlertDialog.Builder(this).setTitle("��ܬ٥�").setItems(
						provinces, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								final AlertDialog ad = new AlertDialog.Builder(
										Main.this).setMessage(
										"�z�w�g��ܤF: " + which + ":"
												+ provinces[which]).show();
								android.os.Handler hander = new android.os.Handler();
								hander.postDelayed(new Runnable()
								{
									@Override
									public void run()
									{
										ad.dismiss();

									}
								}, 5 * 1000);

							}
						}).create();
			case DIALOG_SINGLE_CHOICE_LIST:
				return new AlertDialog.Builder(this).setTitle("��ܬ٥�")
						.setSingleChoiceItems(provinces, 1, buttonOnClick)
						.setPositiveButton("�T�w", buttonOnClick)
						.setNegativeButton("����", buttonOnClick).create();
			case DIALOG_MULTI_CHOICE_LIST:
				AlertDialog ad = new AlertDialog.Builder(this).setIcon(
						R.drawable.image).setTitle("��ܬ٥�").setMultiChoiceItems(
						provinces, new boolean[]
						{ false, true, false, true, false, false },
						new DialogInterface.OnMultiChoiceClickListener()
						{
							public void onClick(DialogInterface dialog,
									int whichButton, boolean isChecked)
							{

							}
						}).setPositiveButton("�T�w",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								int count = lv.getCount();
								String s = "�z��ܤF:";
								for (int i = 0; i < provinces.length; i++)
								{

									if (lv.getCheckedItemPositions().get(i))
										s += i + ":"
												+ lv.getAdapter().getItem(i)
												+ "  ";

								}
								if (lv.getCheckedItemPositions().size() > 0)
								{
									new AlertDialog.Builder(Main.this)
											.setMessage(s).show();
								}
								else
								{
									new AlertDialog.Builder(Main.this)
											.setMessage("�z����ܥ���٥�").show();

								}

							}
						}).setNegativeButton("����", null).create();
				lv = ad.getListView();
				return ad;

		}

		return null;
	}

	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		private int index;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
			}
			else
			{
				if (whichButton == DialogInterface.BUTTON_POSITIVE)
				{
					new AlertDialog.Builder(Main.this).setMessage(
							"�z�w�g��ܤF�G " + index + ":" + provinces[index]).show();
				}
				else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
				{
					new AlertDialog.Builder(Main.this).setMessage("�z���򳣥����.")
							.show();
				}
			}
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnDeleteFile = (Button) findViewById(R.id.btnDeleteFile);
		Button btnSimpleList = (Button) findViewById(R.id.btnSimpleList);
		Button btnSingleChoiceList = (Button) findViewById(R.id.btnSingleChoiceList);
		Button btnMultiChoiceList = (Button) findViewById(R.id.btnMultiChoiceList);
		Button btnRemoveDialog = (Button)findViewById(R.id.btnRemoveDialog);
		btnDeleteFile.setOnClickListener(this);
		btnSimpleList.setOnClickListener(this);
		btnSingleChoiceList.setOnClickListener(this);
		btnMultiChoiceList.setOnClickListener(this);
		btnRemoveDialog.setOnClickListener(this);
	}
}
