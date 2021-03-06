package mobile.android.ch08.sharedpreferences;

import java.io.Serializable;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick_WriteData(View view)
	{		
		SharedPreferences mySharedPreferences = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("name", "李寧");
		editor.putString("habit", "Android、寫作、旅遊");
		editor.commit();
	}

	public void onClick_ReadData(View view)
	{
		SharedPreferences sharedPreferences = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 使用getXxx方法獲得value，getXxx方法的第2個參數是value的預設值
		String name = sharedPreferences.getString("name", "");
		String habit = sharedPreferences.getString("habit", "");
		Toast.makeText(this, "name：" + name + "\n" + "habit：" + habit,
				Toast.LENGTH_LONG).show();
		

	}
}