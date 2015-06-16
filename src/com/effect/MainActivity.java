package com.effect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ImageView inImageView, outImageView;
	Bitmap outBitmap;
	Allocation allocationIn1, allocationIn2;
	RenderScript rs;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

		inImageView = (ImageView) findViewById(R.id.in_imageview);

		inImageView.setImageResource(R.drawable.image);

		outImageView = (ImageView) findViewById(R.id.out_imageview);

		outImageView.setImageResource(R.drawable.image);
		context = this;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

//		rs.finish();
//		rs.destroy();
//		//
//		rs = null;
//
//		allocationIn1 = null;
//		allocationIn2 = null;
		ImageProcessing2.destroy();
		super.onPause();
	}

	protected void onResume() {

		super.onResume();

		applyImageOverlay(null);

	};

	void applyImageOverlay(Allocation allo) {

		rs = RenderScript.create(context);
		Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.e);

		int imageSize = 512;

		Bitmap b2 = Bitmap.createScaledBitmap(b1, imageSize, imageSize, false);

		outBitmap = Bitmap.createScaledBitmap(b1, imageSize, imageSize, false);

		inImageView.setImageBitmap(b2);

		Log.i("image", "w: " + b1.getWidth() + " h: " + b1.getHeight());

		allocationIn1 = Allocation.createFromBitmap(rs, outBitmap,
				Allocation.MipmapControl.MIPMAP_NONE, 1);

		allocationIn2 = Allocation.createFromBitmap(rs, b2,
				Allocation.MipmapControl.MIPMAP_NONE, 1);

		// printBitmapPixelsHex(outBitmap);

		// printBitmapPixelsFloat(outBitmap);\

		new AsyncTask<String, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub

				ImageProcessing2.getInstance(context, rs, outBitmap)
						.process(allocationIn1, allocationIn2);
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				outImageView.setImageBitmap(outBitmap);
//				
			}
		}.execute();
	
	}

	void printBitmapPixelsHex(Bitmap bitmap) {

		System.out.println();

		for (int i = 0; i < bitmap.getWidth(); i++) {
			System.out.println("row" + i);
			for (int j = 0; j < bitmap.getHeight(); j++) {

				System.out
						.print(" "
								+ String.format("#%X",
										Color.red(bitmap.getPixel(i, j)))
								+ ":"
								+ String.format("#%X",
										Color.green(bitmap.getPixel(i, j)))
								+ ":"
								+ String.format("#%X",
										Color.blue(bitmap.getPixel(i, j)))
								+ " ");

			}
			System.out.println("\n");
		}

	}

	void printBitmapPixelsFloat(Bitmap bitmap) {

		System.out.println();

		for (int i = 0; i < bitmap.getWidth(); i++) {
			System.out.println("row" + i);
			System.out.println("{");
			for (int j = 0; j < bitmap.getHeight(); j++) {

				System.out.print(" " + (float) Color.red(bitmap.getPixel(i, j))
						/ 255 + ",");

			}
			System.out.println("}");
			System.out.println("\n");
		}

	}

}
