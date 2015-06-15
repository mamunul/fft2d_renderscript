package com.effect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

		imageView = (ImageView) findViewById(R.id.imageview);

		imageView.setImageResource(R.drawable.image);

		applyImageOverlay(null);
	}

	void applyImageOverlay(Allocation allocationIn2) {
		
		int N = 8;
		int M = 8;
		double[][] re2 = new double[N][M];
		double[][] im2 = new double[N][M];
		
		for (int i = 0; i < N; i++) {

			for (int j = 0; j < M; j++) {

				// re2[i][j] = i;
				// im2[i][j] = 0;

				re2[i][j] = i + j;
				im2[i][j] = 0;

//				System.out.print(" " + re2[i][j] + "+j" + im2[i][j]);
			}

//			System.out.println();
		}

		RenderScript rs = RenderScript.create(this);
		Bitmap b1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.image);
		Bitmap outBitmap = Bitmap.createScaledBitmap(b1, 8, 8, false);

		imageView.setImageBitmap(outBitmap);

		Log.i("image", "w: " + b1.getWidth() + " h: " + b1.getHeight());

		Allocation allocationIn1 = Allocation.createFromBitmap(rs, outBitmap,
				Allocation.MipmapControl.MIPMAP_NONE, 1);

		printBitmapPixelsHex(outBitmap);

		printBitmapPixelsFloat(outBitmap);

		ImageProcessing.getInstance(this, rs, outBitmap).process(allocationIn1);

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
			for (int j = 0; j < bitmap.getHeight(); j++) {

				System.out.println(" "
						+ (float) Color.red(bitmap.getPixel(i, j)) / 255 + ":"
						+ (float) Color.green(bitmap.getPixel(i, j)) / 255
						+ ":" + (float) Color.blue(bitmap.getPixel(i, j)) / 255
						+ " ");

			}

			System.out.println("\n");
		}

	}

}
