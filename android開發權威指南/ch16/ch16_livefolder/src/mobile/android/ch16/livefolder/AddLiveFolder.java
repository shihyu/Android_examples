package mobile.android.ch16.livefolder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.LiveFolders;

public class AddLiveFolder extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(getIntent()
				.getAction()))
		{
			Intent intent = new Intent();
			intent.setData(Uri.parse("content://contacts/people"));
			intent.putExtra(
					LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT,
					new Intent(Intent.ACTION_CALL, Uri
							.parse("content://contacts/people")));
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, "�q�ܥ�");
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON,
					Intent.ShortcutIconResource.fromContext(this,
							R.drawable.phone));
			intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE,
					LiveFolders.DISPLAY_MODE_LIST);

			setResult(RESULT_OK, intent);
		}
		else
		{
			setResult(RESULT_CANCELED);
		}
		finish();
	}
}
