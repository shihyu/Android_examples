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
		String about = "新浪微部落格戶端     版本：1.0\n\n";
		about += "作者：李寧\n\n";
		about += "網名：銀河使者\n\n";
		about += "部落格：http://nokiaguy.blogjava.net\n\n";
		
		tvAbout.setText(about);
	}
}
 