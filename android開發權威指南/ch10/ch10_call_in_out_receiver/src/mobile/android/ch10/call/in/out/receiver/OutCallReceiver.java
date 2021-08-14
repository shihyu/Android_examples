package mobile.android.ch10.call.in.out.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutCallReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String outcomingNumber = intent
				.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		InCallReceiver.showToast(context, outcomingNumber);
	}

}
