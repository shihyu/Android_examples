package mobile.android.demo.sina.microblog;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity
{
	private TextView tvAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		tvAbout = (TextView) findViewById(R.id.tvAbout);
		String about = "�s���L��������     �����G1.0\n\n";
		about += "�@�̡G����\n\n";
		about += "���W�G�Ȫe�Ϫ�\n\n";
		about += "������Ghttp://nokiaguy.blogjava.net\n\n";
		
		tvAbout.setText(about);
	}
}
 