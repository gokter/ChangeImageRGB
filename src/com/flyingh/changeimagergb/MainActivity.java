package com.flyingh.changeimagergb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String JPG_FILE_SUFFIX = ".jpg";
	private static final int FOR_SELECT_IMAGE = 0;
	private ImageView imageView;
	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	private SeekBar alphaSeekBar;
	private ColorMatrix colorMatrix;
	private Paint paint;
	private Canvas canvas;
	private Bitmap bitmap;
	private Bitmap mutableBitmap;
	private Matrix matrix;
	private int redProgress = 127;
	private int greenProgress = 127;
	private int blueProgress = 127;
	private int alphaProgress = 127;
	private BeautyOnSeekBarChangeListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.beauty);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beauty);
		imageView.setImageBitmap(bitmap);
		mutableBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		canvas = new Canvas(mutableBitmap);
		paint = new Paint();
		colorMatrix = new ColorMatrix();
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		matrix = new Matrix();
		canvas.drawBitmap(bitmap, matrix, paint);
		redSeekBar = (SeekBar) findViewById(R.id.red_seek_bar);
		greenSeekBar = (SeekBar) findViewById(R.id.green_seek_bar);
		blueSeekBar = (SeekBar) findViewById(R.id.blue_seek_bar);
		alphaSeekBar = (SeekBar) findViewById(R.id.alpha_seek_bar);
		listener = new BeautyOnSeekBarChangeListener();
		redSeekBar.setOnSeekBarChangeListener(listener);
		greenSeekBar.setOnSeekBarChangeListener(listener);
		blueSeekBar.setOnSeekBarChangeListener(listener);
		alphaSeekBar.setOnSeekBarChangeListener(listener);
	}

	private class BeautyOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			float redSeekBarProgressPercent = redSeekBar.getProgress() / (seekBar.getMax() / 2f);
			float greenSeekBarProgressPercent = greenSeekBar.getProgress() / (seekBar.getMax() / 2f);
			float blueSeekBarProgressPercent = blueSeekBar.getProgress() / (seekBar.getMax() / 2f);
			float alphaSeekBarProgressPercent = alphaSeekBar.getProgress() / (seekBar.getMax() / 2f);
			colorMatrix.set(new float[] { redSeekBarProgressPercent, 0, 0, 0, 0, 0, greenSeekBarProgressPercent, 0, 0,
					0, 0, 0, blueSeekBarProgressPercent, 0, 0, 0, 0, 0, alphaSeekBarProgressPercent, 0 });
			paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			canvas.drawBitmap(bitmap, matrix, paint);
			imageView.setImageBitmap(mutableBitmap);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}

	public void open(View view) {
		Intent intent = new Intent();
		intent.setClassName("com.miui.gallery", "com.miui.gallery.app.Gallery");
		startActivityForResult(intent, FOR_SELECT_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Uri uri = data.getData();
			imageView.setImageURI(uri);
		}
	}

	public void backup(View view) {
		redProgress = redSeekBar.getProgress();
		greenProgress = greenSeekBar.getProgress();
		blueProgress = blueSeekBar.getProgress();
		alphaProgress = alphaSeekBar.getProgress();
		Toast.makeText(this, R.string.backup_success_, Toast.LENGTH_SHORT).show();
	}

	public void restore(View view) {
		redSeekBar.setProgress(redProgress);
		greenSeekBar.setProgress(greenProgress);
		blueSeekBar.setProgress(blueProgress);
		alphaSeekBar.setProgress(alphaProgress);
		Toast.makeText(this, R.string.restore_success_, Toast.LENGTH_SHORT).show();
	}

	public void save(View view) {
		try {
			String path = Environment.getExternalStorageDirectory() + File.separator
					+ String.format("%1$tF %1$tT", new Date(System.currentTimeMillis())) + JPG_FILE_SUFFIX;
			mutableBitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(path));
			Toast.makeText(this, R.string.save_success_, Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			Toast.makeText(this, R.string.save_fail_, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
