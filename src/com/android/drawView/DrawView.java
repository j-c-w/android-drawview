package com.dotpubs.customQuiz.view;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View implements OnTouchListener {
	private static final String TAG = "DrawView";

	DrawViewBackground background;

	Bitmap backgroundPicture;

	List<Point> points = new ArrayList<Point>();
	List<Paint> paint = new ArrayList<Paint>();
	List<List<Integer>> newLine = new ArrayList<List<Integer>>();

//	private Bitmap bitmap;

	int which;//to make sure the correct paint is accessed

	public int size = 8;//brush size

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawView(Context context) {
		super(context);
		init();
	}

	private void init() {
		which = 0;
		points.add(new Point());
		paint.add(new Paint());
		newLine.add(new ArrayList<Integer>());
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);


		paint.get(which).setColor(Color.BLUE);
		paint.get(which).setAntiAlias(true);

		//note that using Paint.Style.FILL 
		//gives a different effect
		paint.get(which).setStyle(Paint.Style.STROKE);
		paint.get(which).setStrokeWidth(size);

		//this.setDrawingCacheEnabled(true);
	}

	public void setBackgroundPicture(Bitmap picture) {
		this.backgroundPicture = picture;
	}


	public void setColor(int color) {
		which ++;
		Paint pain = new Paint();//paint.get(which-1);
		pain.setColor(color);
		pain.setStyle(Paint.Style.STROKE);
		pain.setStrokeWidth(size);
		pain.setAntiAlias(true);

		paint.add(pain);
		points.add(new Point());
		paint.get(which).setColor(color);
		newLine.add(new ArrayList<Integer>());
	}

	/*
	 * sets the color of the line
	 */
	public void setColor(String hexidecimalColor) {
		setColor(Color.parseColor(hexidecimalColor));
	}

	/* 
	 * sets the size of the brush
	 */
	public void setBrushSize(int size) {
		which ++;
		this.size = size;
		paint.add(paint.get(which-1));
		points.add(new Point());
		paint.get(which).setStrokeWidth((float) size);
		newLine.add(new ArrayList<Integer>());
	}

	/*
	 * This, unlike clear(), which resets absolutely everything,
	 * keeps the brush as is, and just deletes all the points.
	 *
	 * Set keep Background to true to avoid also deleting the
	 * background.
	 */
	public void clearKeepBrush(boolean keepBackground) {
		Paint oldPaint = paint.get(which);

		which = 0;
		paint = new ArrayList<Paint>();
		points = new ArrayList<Point>();
		newLine = new ArrayList<List<Integer>>();

		if (!keepBackground) {
			backgroundPicture = null;
		}

		// This is the line that preserves the old paint style
		paint.add(oldPaint);

		points.add(new Point());
		newLine.add(new ArrayList<Integer>());

		invalidate();
	}

	public void clear() {//this probably works
		which = 0;
		paint = new ArrayList<Paint>();
		points = new ArrayList<Point>();
		newLine = new ArrayList<List<Integer>>();
		backgroundPicture = null;
		init();
		invalidate();
	}

	/* 
	 * @return
	 * returns a bitmap object of the canvas
	 */
	public Bitmap get() {
		if (getWidth() <= 0 || getHeight() <= 0) {
			Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
			return null;
		}

		Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		c.drawColor(Color.WHITE);

		layout(0, 0, getWidth(), getHeight());
		drawTo(c);
		return b;
	}

	public int getColor() {
		return paint.get(which).getColor();
	}

	public int getBrushSize() {
		return (int) paint.get(which).getStrokeWidth();
	}

	@Override
	public void onMeasure(int maxWidth, int maxHeight) {
		//	bitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
		super.setMeasuredDimension(maxWidth, maxHeight);
	}

	/*
	 * This is used to prevent SO errors when building the drawing of the
	 * view when there get to be too many points.
	 *
	 * This DOES NOT check for too many points. That is only done in onDraw
	 * (which is only called by the system).
	 */
	public void drawTo(Canvas canvas) {
		//canvas = new Canvas(bitmap);
		//canvas.drawBitmap(bitmap, new Matrix(), paint.get(which));
		if (background != null) {
			background.drawOn(canvas);
		}

		if (backgroundPicture != null) {
			canvas.drawBitmap(backgroundPicture, 0, 0, new Paint());
		}

		Path path = new Path();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			path.reset();
			for (int in = 0; in < point.length(); in++) {
				if (newLine.get(i).contains(in) || in == 0) {//checks for a new line
					path.moveTo(point.x.get(in), point.y.get(in));//moves to new point
				} else {
					path.lineTo(point.x.get(in), point.y.get(in));//draw path!!
				}
				//	Log.i("AndroidRuntime", Integer.toString(in) + Integer.toString(which));
			}
			//	Log.i("AndroidRuntime", Integer.toString(i) + "-"+Integer.toString(which));
			try {
				canvas.drawPath(path, paint.get(i));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawTo(canvas);

		int totalSize = 0;

		for (Point p : points) {
			totalSize += p.length();
		}

		if (totalSize > 400 && drawingNewLine) {
			setBackgroundPicture(get());
			clearKeepBrush(true);
		}
	}


	public void setDrawViewBackground(DrawViewBackground background) {
		this.background = background;
	}

	public boolean onTouch(View view, MotionEvent event) {
		touch(view, event);
		//	Point point = points.get(which);
		points.get(which).x.add(event.getX());
		points.get(which).y.add(event.getY());
		//	points.add(which, point);
		invalidate();//can't remember what this does either
		//I wrote the basics for this a while ago
		//	Log.d(TAG, "point: " + point);
		if(event.getAction() == MotionEvent.ACTION_UP) {
			// if the user lifts thier finger up
			newLine.get(which).add(points.get(which).length());//this line sucks
		}
		return true;
	}

	public void touch(View view, MotionEvent event) {
		//nada
	}

	public static final DrawViewBackground PLAIN = new DrawViewBackground() {
		@Override
		public void drawOn(Canvas canvas) {
			return;
		}
	};

	public static final DrawViewBackground LINED = new DrawViewBackground() {
		final float SPACING_PX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, Resources.getSystem().getDisplayMetrics());
		final Paint LINE_PAINT = new Paint();

		@Override
		public void drawOn(Canvas canvas) {
			int WIDTH = canvas.getWidth();
			LINE_PAINT.setColor(Color.GRAY);

			int numberOfLines = (int) (canvas.getHeight() / SPACING_PX);

			for (int i = 0; i < numberOfLines; i ++) {
				float top =  i * SPACING_PX;
				canvas.drawRect(0, top, WIDTH, top + 2, LINE_PAINT);
			}

		}
	};


	/*
	 * This is an interface that can be used to
	 * define any kind of background you want.
	 *
	 * It should be set with setDrawViewBackground.
	 */
	public interface DrawViewBackground {
		public void drawOn(Canvas canvas);
	}
}

class Point {
	List<Float> x, y;
	public Point() {
		x = new ArrayList<Float>();
		y = new ArrayList<Float>();
	}
	public int length() {
		return x.size();
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}
