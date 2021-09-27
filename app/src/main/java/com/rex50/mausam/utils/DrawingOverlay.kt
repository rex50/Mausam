package com.rex50.mausam.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class DrawingOverlay(context : Context, attributeSet : AttributeSet) : SurfaceView( context , attributeSet ) , SurfaceHolder.Callback {

    var maskBitmap : Bitmap? = null

    override fun surfaceCreated(holder: SurfaceHolder) {
        //TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //TODO("Not yet implemented")
    }

    private fun flipBitmap( source: Bitmap ): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, source.width / 2f, source.height / 2f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    override fun onDraw(canvas: Canvas?) {
        // Draw the segmentation here
        if ( maskBitmap != null ) {
            canvas?.drawBitmap( flipBitmap( maskBitmap!! ), 0f , 0f , null )
        }
    }

}