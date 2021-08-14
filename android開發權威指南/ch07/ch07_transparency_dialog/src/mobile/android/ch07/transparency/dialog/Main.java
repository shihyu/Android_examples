package mobile.android.ch07.transparency.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick_TransparencyDialog(View view)
	{
		// ��ܳz������͵���
		
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setMessage("�z����͵���").setPositiveButton("�T�w", null).create();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		//  �]�w�z���׬�0.7
		lp.alpha = 0.7f;
		window.setAttributes(lp);
		alertDialog.show();
 
		// ��ܫD�z������͵���
		alertDialog = new AlertDialog.Builder(this).setMessage("�b������ܥ�͵���")
				.setPositiveButton("�T�w", null).create();
		window = alertDialog.getWindow(); 
		
		window.setGravity(Gravity.BOTTOM);
		alertDialog.show();
	}
}