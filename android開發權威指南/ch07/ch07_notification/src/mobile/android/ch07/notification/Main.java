package mobile.android.ch07.notification;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener
{
	private NotificationManager notificationManager;
	

	private void setDefaults(String tickerText, String contentTitle,
			String contentText, int id, int resId, int defaults)
	{
		Notification notification = new Notification(resId,
				tickerText, System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Main.class), 0);
		notification.deleteIntent = contentIntent;
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);
		notification.defaults = defaults;
		notificationManager.notify(id, notification);
		

	}

	private void showNotification(String tickerText, String contentTitle,
			String contentText, int id, int resId)
	{
		Notification notification = new Notification(resId,
				tickerText, System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Main.class), 0);
		

		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);
		notificationManager.notify(id, notification);

	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.btnSmile:
				showNotification("今天非常高興", "今天考試得了全年級第一",
						"數學100分、語文99分、英語100分，yeah！", R.drawable.smile,
						R.drawable.smile);
				break;

			case R.id.btnWhy:
				showNotification("這是為什麼呢？", "這道題為什麼會出錯呢？", "誰有正確答案啊.",
						R.drawable.why, R.drawable.why);
				break;
			case R.id.btnWrath:
				showNotification("今天心情不好", "也不知道為什麼，這幾天一直很鬱悶.", "也許應該去公園散心了",
						R.drawable.why, R.drawable.wrath);
				break;
			case R.id.btnClear:
				// notificationManager.cancel(R.drawable.smile);
				// notificationManager.cancel(R.drawable.why);
				notificationManager.cancelAll();
				break;
			case R.id.btnRing:
				setDefaults("使用預設的聲音", "使用預設的聲音", "使用預設的聲音", R.id.btnRing, R.drawable.smile,
						Notification.DEFAULT_SOUND);
			case R.id.btnVibrate:
				setDefaults("使用預設的震動", "使用預設的震動", "使用預設的震動", R.id.btnVibrate,
						R.drawable.smile, Notification.DEFAULT_VIBRATE);
			case R.id.btnLight:
				setDefaults("使用預設的Light", "使用預設的Light", "使用預設的Light", R.id.btnLight,
						R.drawable.smile, Notification.DEFAULT_LIGHTS);
			case R.id.btnRingAndVibrate:
				setDefaults("所有的都使用預設值", "所有的都使用預設值", "所有的都使用預設值",
						R.id.btnRingAndVibrate, R.drawable.smile,
						Notification.DEFAULT_ALL);
			
				break;

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Button btnSmile = (Button) findViewById(R.id.btnSmile);
		Button btnWhy = (Button) findViewById(R.id.btnWhy);
		Button btnWrath = (Button) findViewById(R.id.btnWrath);
		Button btnClear = (Button) findViewById(R.id.btnClear);
		Button btnRing = (Button) findViewById(R.id.btnRing);		
		Button btnVibrate = (Button) findViewById(R.id.btnVibrate);
		Button btnLight = (Button) findViewById(R.id.btnLight);
		Button btnRingAndVibrate = (Button) findViewById(R.id.btnRingAndVibrate);
		btnSmile.setOnClickListener(this);
		btnWhy.setOnClickListener(this);
		btnWrath.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnRing.setOnClickListener(this);
		btnVibrate.setOnClickListener(this);
		btnLight.setOnClickListener(this);
		btnRingAndVibrate.setOnClickListener(this);
	}
}
