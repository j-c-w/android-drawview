package com.dotpubs.drawView;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.util.Log;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		final DrawView drawView = new DrawView(this);
		drawView.setBackgroundColor (Color.parseColor("#333333"));
        setContentView(drawView);
		Thread thread = new Thread(new Runnable(){
			synchronized public void run(){
				try
				{
					wait(10000);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					finish();
				}
				drawView.setColor("#111111");

			}
		});
		thread.start();
    }
}
