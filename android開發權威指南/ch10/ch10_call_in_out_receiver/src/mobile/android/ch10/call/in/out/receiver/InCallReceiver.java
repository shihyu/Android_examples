package mobile.android.ch10.call.in.out.receiver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class InCallReceiver extends BroadcastReceiver
{
	private static Object obj;

	public static void showToast(Context context, String msg)
	{
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		try
		{
			Field field = toast.getClass().getDeclaredField("mTN");
			field.setAccessible(true);
			obj = field.get(toast);
			Method method = obj.getClass().getDeclaredMethod("show", null);
			method.invoke(obj, null);
		}
		catch (Exception e)
		{
		}

	}

	public static void closeToast()
	{
		if (obj != null)
		{
			try
			{

				Method method = obj.getClass().getDeclaredMethod("hide", null);
				method.invoke(obj, null);
			}
			catch (Exception e)
			{
			}

		}
	}

	@Override
	public void onReceive(final Context context, final Intent intent)
	{

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);

		switch (tm.getCallState())
		{
			case TelephonyManager.CALL_STATE_RINGING:
				String incomingNumber = intent
						.getStringExtra("incoming_number");
				showToast(context, incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d("call_state", "offhook");
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				closeToast();

		}

	}
}
