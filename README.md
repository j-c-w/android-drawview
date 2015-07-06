android-drawview
================

A simple draw view for android

The package is 'com.android.drawView' - use that to access the jar
setBrushSize(int size) - pretty self explanatory, default is 20

setColor(int Color) - this sets the color of the brush

setColor(String hexidecimalColor) - this is to set the color of 
the brush from a hexidecimal String. Eg. setColor("#333333")

get() - returns a bitmap object of the canvas

clear() - this clears the canvas and resets the brush use clearKeepBrush(boolean keepBackground) to keep the brush