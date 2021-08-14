package mobile.android.ch15.rotate.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Main extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick_RotateImage(View view)
	{
		ImageView imageView = (ImageView) findViewById(R.id.imageview);

		Matrix matrix = new Matrix();
		matrix.setRotate(45); // 順時針旋轉45度

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.image); 
		// 旋轉影像，並產生旋轉後的Bitmap對像
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 
				bitmap.getHeight(), matrix, true);

		imageView.setImageBitmap(bitmap);

	}
}