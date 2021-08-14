package mobile.android.ch16.gesture;

import java.util.ArrayList;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.widget.Toast;

public class Main extends Activity implements OnGesturePerformedListener
{
	private GestureLibrary gestureLibrary;

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
	{
		ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

		if (predictions.size() > 0)
		{

			StringBuilder sb = new StringBuilder();
			int n = 0;
			if (predictions.size() > 0)
			{
				Prediction prediction = predictions.get(0);
				if (prediction.score > 1.0)
				{
					sb.append("��ﵲ�G�Aname:" + prediction.name);

				}
				Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (gestureLibrary.load())
		{
			setTitle("����ɮץ[�����\�]��X��r�^.");
			GestureOverlayView gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
			gestureOverlayView.addOnGesturePerformedListener(this);
		}
		else
		{
			setTitle("����ɮץ[������.");
		}
	}
}