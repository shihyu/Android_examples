package mobile.android.ch05.tab;

import java.util.ArrayList;
import java.util.List;

import mobile.android.ch05.tab.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RatingListView extends ListActivity
{
	private static String[] applicationNames = new String[]
	{ "�h�\����", "eoeMarket�Ȥ��", "�@�������O���j��", "�ժ��|", "�{���׵���" };
	private static String[] authors = new String[]
	{ "����", "eoemobile", "wawa", "ApkHome", "lucyfa" };
	private static int[] resIds = new int[]
	{ R.drawable.calendar, R.drawable.eoemarket, R.drawable.brick,
			R.drawable.whitesociety, R.drawable.terminater };
	private static float[] applicationRating = new float[]
	{ (float) 5.0, (float) 5.0, (float) 3.5, (float) 5.0, (float) 4.0 };
	String inflater = Context.LAYOUT_INFLATER_SERVICE;
	LayoutInflater layoutInflater;
	private RatingAdapter raAdapter;

	private class RatingAdapter extends BaseAdapter
	{
		private Context context;

		public RatingAdapter(Context context)
		{
			this.context = context;
			layoutInflater = (LayoutInflater) context
					.getSystemService(inflater);
		}

		@Override
		public int getCount()
		{
			return applicationNames.length;
		}

		@Override
		public Object getItem(int position)
		{
			return applicationNames[position];
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		public void setRating(int position, float rating)
		{
			applicationRating[position] = rating;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(
					R.layout.rating_main, null);
			ImageView ivLogo = (ImageView) linearLayout
					.findViewById(R.id.ivLogo);
			TextView tvApplicationName = ((TextView) linearLayout
					.findViewById(R.id.tvApplicationName));
			TextView tvAuthor = (TextView) linearLayout
					.findViewById(R.id.tvAuthor);
			TextView tvRating = (TextView) linearLayout 
					.findViewById(R.id.tvRating);
			RatingBar ratingBar = (RatingBar) linearLayout
					.findViewById(R.id.ratingbar);
			ivLogo.setImageResource(resIds[position]);
			tvApplicationName.setText(applicationNames[position]);
			tvAuthor.setText(authors[position]);
			tvRating.setText(String.valueOf(applicationRating[position]));
			ratingBar.setRating(applicationRating[position]);
			return linearLayout;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View view, final int position,
			long id)
	{
		View myView = getLayoutInflater().inflate(R.layout.rating, null);
		final RatingBar ratingBar = (RatingBar) myView
				.findViewById(R.id.ratingbar);
		ratingBar.setRating(applicationRating[position]);
		new AlertDialog.Builder(this).setTitle(applicationNames[position])
				.setMessage("�����ε{������").setIcon(resIds[position])
				.setView(myView).setPositiveButton("�T�w", new OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						raAdapter.setRating(position, ratingBar.getRating());
						
					}
				}).setNegativeButton("����", null).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<View> viewList = new ArrayList<View>();
		viewList.add(getLayoutInflater().inflate(R.layout.rating_main, null));
		raAdapter = new RatingAdapter(this);
		setListAdapter(raAdapter);
		

	}
}