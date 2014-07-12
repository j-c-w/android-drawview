package com.android.drawView;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.util.*;
import android.graphics.Path;
import android.graphics.Bitmap;

public class DrawView extends View implements OnTouchListener
{
	private static final String TAG = "DrawView";

	List<Point> points = new ArrayList<Point>();

	//stores the different colors used so far
	List<Paint> paint = new ArrayList<Paint>();

	//stores the points along each line that has been drawn
	List<List<Integer>> newLine = new ArrayList<List<Integer>>();


	int which;//to make sure the correct paint is accessed

	public int size =20;//brush size

	public DrawView(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}

	public DrawView(Context context) {
		super(context);
		init();
	}

	private void init()
	{
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


	public void setColor(int color){
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
	public void setColor(String hexidecimalColor){
		which ++;
		Paint pain = new Paint();//paint.get(which-1);
		pain.setColor(Color.parseColor((hexidecimalColor)));
		pain.setStyle(Paint.Style.STROKE);
		pain.setStrokeWidth(size);
		pain.setAntiAlias(true);
		paint.add(pain);
		points.add(new Point());
		paint.get(which).setColor(Color.parseColor(hexidecimalColor));
		newLine.add(new ArrayList<Integer>());
	}

	/* 
	 * sets the size of the brush
	 */
	public void setBrushSize(int size){
		which ++;
		this.size = size;
		Paint pain = new Paint();//paint.get(which-1);
		pain.setColor(paint.get(which-1).getColor());
		pain.setStyle(Paint.Style.STROKE);
		pain.setStrokeWidth(size);
		pain.setAntiAlias(true);
		paint.add(pain);
		paint.add(paint.get(which-1));
		points.add(new Point());
		paint.get(which).setStrokeWidth((float)size);
		newLine.add(new ArrayList<Integer>());
	}

	public void clear(){//this probably works
		which = 0;
		paint = new ArrayList<Paint>();
		points = new ArrayList<Point>();
		newLine = new ArrayList<List<Integer>>();
		init();
		this.onDraw(new Canvas());
	}

	/* 
	 * @return
	 * returns a bitmap object of the canvas
	 */
	public Bitmap get(){
		super.buildDrawingCache();
		return this.getDrawingCache();
	}

	public int getColor(){
		return paint.get(which).getColor();
	}

	@Override
	public void onMeasure(int maxWidth, int maxHeight){
		//	bitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
		super.setMeasuredDimension(maxWidth, maxHeight);
	}

	@Override
	public void onDraw(Canvas canvas){
		int totalSize = 1;
		//canvas = new Canvas(bitmap);
		//canvas.drawBitmap(bitmap, new Matrix(), paint.get(which));
		for (int i = 0; i<points.size(); i++){
			Point point = points.get(i);
			totalSize *= point.length();
			Path path = new Path();
			for (int in = 0; in<point.length(); in++){
				if (newLine.get(i).contains(in)||in==0){//checks for a new line
					path.moveTo(point.x.get(in), point.y.get(in));//moves to new point
				} else {
					path.lineTo(point.x.get(in), point.y.get(in));//draw path!!
				}
				//	Log.i("AndroidRuntime", Integer.toString(in) + Integer.toString(which));
			}
			//	Log.i("AndroidRuntime", Integer.toString(i) + "-"+Integer.toString(which));
			try{
				canvas.drawPath(path, paint.get(i));
			} catch(Exception e){
				e.printStackTrace();
			}
		}
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
		if(event.getAction() == MotionEvent.ACTION_UP){
			// if the user lifts thier finger up
			newLine.get(which).add(points.get(which).length());//this line sucks
		}
		return true;
	}
	public void touch(View view, MotionEvent event){
		//nada
	}
}

class Point {
	List<Float> x, y;
	public Point(){
		x = new ArrayList<Float>();
		y = new ArrayList<Float>();
	}
	public int length(){
		return x.size();
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}
