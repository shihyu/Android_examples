package mobile.android.ch07.toast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener
{

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnTextToast:
				Toast textToast = Toast.makeText(this, "���Ѫ��Ѯ�u�n�I\n���A���A���I",
						Toast.LENGTH_LONG);

				// textToast.setText("�{�b�O�X�I�H");
				textToast.show();
				break;

			case R.id.btnImageToast:
				View view = getLayoutInflater().inflate(R.layout.toast, null);
				TextView textView = (TextView) view.findViewById(R.id.textview);
				textView.setText("���Ѫ��Ѯ�u�n�I\n���A���A���I");
				Toast toast = new Toast(this);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(view);
				toast.show();

				break;
		}

	
	


		
		

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnTextToast = (Button) findViewById(R.id.btnTextToast);
		Button btnImageToast = (Button) findViewById(R.id.btnImageToast);
		btnTextToast.setOnClickListener(this);
		btnImageToast.setOnClickListener(this);
	}
}