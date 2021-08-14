package mobile.android.ch07.login.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Main extends Activity implements OnClickListener
{
	@Override
	public void onClick(View v)
	{
		LinearLayout loginLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.login, null);
		new AlertDialog.Builder(this).setIcon(R.drawable.login)
				.setTitle("�ϥΪ̵n�J").setView(loginLayout).setPositiveButton("�n�J",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								// ���g�B�z�ϥΪ̵n�J���{���X
							}
						}).setNegativeButton("����",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								// �����ϥΪ̵n�J�A�h�X�{��

							}
						}).show();

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);

	}
}