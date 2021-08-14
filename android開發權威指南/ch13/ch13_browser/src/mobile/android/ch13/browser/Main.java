package mobile.android.ch13.browser;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.webkit.URLUtil; 
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener,
		OnMenuItemClickListener
{
	private WebView webView;
	private EditText etAddress;

	@Override
	public void onClick(View view)
	{
		String url = etAddress.getText().toString();
		if (URLUtil.isNetworkUrl(url))
			webView.loadUrl(url);
		else
			Toast.makeText(this, "��J�����}�����T.", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		switch (item.getItemId())
		{
			// �V��]back�^
			case 0:
				webView.goBack();
				break;
			// �V�e�]Forward�^
			case 1:
				webView.goForward();
				break;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem miBack = menu.add(0, 0, 0, "�V��]back�^");
		MenuItem miForward = menu.add(0, 1, 1, "�V�e�]Forward�^");
		miBack.setOnMenuItemClickListener(this);
		miForward.setOnMenuItemClickListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		webView = (WebView) findViewById(R.id.webview);
		etAddress = (EditText) findViewById(R.id.etAddress);
		ImageButton ibBrowse = (ImageButton) findViewById(R.id.ibBrowse);
		ibBrowse.setOnClickListener(this);


	}
}