package mobile.android.ch08.xml.object;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;

public class Main extends Activity 
{


	public void onClick_XMLToObject(View view)
	{
		try
		{
			
			InputStream is = getResources().openRawResource(R.raw.products);
			XML2Product xml2Product = new XML2Product();
			android.util.Xml.parse(is, Xml.Encoding.UTF_8, xml2Product);

			List<Product> products = xml2Product.getProducts();
			String msg = "共" + products.size() + "個產品\n";
			for (Product product : products)
			{
				msg += "id:" + product.getId() + "  產品名：" + product.getName()
						+ "  價格：" + product.getPrice() + "\n";
			}
			new AlertDialog.Builder(this).setTitle("產品資訊").setMessage(msg)
					.setPositiveButton("關閉", null).show();
		}
		catch (Exception e)
		{

		}

	}
 
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}
}