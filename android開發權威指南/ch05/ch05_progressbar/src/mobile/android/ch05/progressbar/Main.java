package mobile.android.ch05.progressbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class Main extends Activity implements OnClickListener
{
	private ProgressBar progressBarHorizontal;

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			// �B�z�u�W�[�i��"���s�ƥ�
			case R.id.button1:
				progressBarHorizontal.setProgress((int) (progressBarHorizontal
						.getProgress() * 1.2));
				progressBarHorizontal
						.setSecondaryProgress((int) (progressBarHorizontal
								.getSecondaryProgress() * 1.2));
				break;
			// �B�z�u��ֶi��"���s�ƥ�
			case R.id.button2:
				progressBarHorizontal.setProgress((int) (progressBarHorizontal
						.getProgress() * 0.8));
				progressBarHorizontal
						.setSecondaryProgress((int) (progressBarHorizontal
								.getSecondaryProgress() * 0.8));
				break;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		setProgressBarVisibility(true);
		setProgressBarIndeterminateVisibility(true);
		setProgress(3500);
		progressBarHorizontal = (ProgressBar) findViewById(R.id.progressBarHorizontal);
		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
	}
}