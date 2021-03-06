package mobile.android.ch12.complex.type.aidlclient;


import mobile.android.ch12.complex.type.aidl.IMyService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener
{
	private IMyService myService = null;
	private Button btnInvokeAIDLService;
	private Button btnBindAIDLService;
	private TextView textView;
	private ServiceConnection serviceConnection = new ServiceConnection()
	{ 

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			myService = IMyService.Stub.asInterface(service);
			btnInvokeAIDLService.setEnabled(true);

		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btnBindAIDLService:
				bindService(new Intent("mobile.android.ch12.complex.type.aidl.IMyService"),
						serviceConnection, Context.BIND_AUTO_CREATE);
				break;

			case R.id.btnInvokeAIDLService:
				try
				{
					String s = "";
					s = "Product.id = " + myService.getProduct().getId() + "\n";
					s += "Product.name = " + myService.getProduct().getName()
							+ "\n";
					s += "Product.price = " + myService.getProduct().getPrice()
							+ "\n";
					
					s += myService.getMap("China", myService.getProduct()).toString();
					textView.setText(s);
				}
				catch (Exception e)
				{

				}
				break;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnInvokeAIDLService = (Button) findViewById(R.id.btnInvokeAIDLService);
		btnBindAIDLService = (Button) findViewById(R.id.btnBindAIDLService);
		btnInvokeAIDLService.setEnabled(false);
		textView = (TextView) findViewById(R.id.textview);
		btnInvokeAIDLService.setOnClickListener(this);
		btnBindAIDLService.setOnClickListener(this);
	}
}