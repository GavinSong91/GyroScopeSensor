package com.gavin.gyroscopesensor.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.gavin.gyroscopesensor.R


/**
 * Author:     gavinsong
 * Date:       2020/3/25
 * Description:
 *----------------------------------------------------------------------------------
 */
class CircleImageView
@JvmOverloads constructor(context: Context?, attrs: AttributeSet, var defStyleAttr: Int = 0 ) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var SCALE_TYPE = ScaleType.CENTER_CROP

    private var BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    private var COLORDRAWABLE_DIMENSION = 1

    private var DEFAULT_BORDER_WIDTH = 0
    private var DEFAULT_BORDER_COLOR: Int = Color.BLACK

    private var mDrawableRect = RectF()
    private var mBorderRect = RectF()

    private var mShaderMatrix: Matrix = Matrix()
    private var mBitmapPaint: Paint = Paint()
    private var mBorderPaint: Paint = Paint()

    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH

    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0

    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f

    private var mReady = false
    private var mSetupPending = false

    init {
        attrs?.let {
            initAttrs(it)
        }
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        var a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH)
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR)
        a.recycle()
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
        }

    }

    override fun onDraw(canvas: Canvas?) {
        if (drawable == null) {
            return
        }
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mDrawableRadius, mBitmapPaint)
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mBorderRadius, mBorderPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(borderColor: Int) {
        if (borderColor == mBorderColor) {
            return
        }
        mBorderColor = borderColor
        mBorderPaint.color = mBorderColor
        invalidate()
    }

    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        if (borderWidth == mBorderWidth) {
            return
        }
        mBorderWidth = borderWidth
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLORDRAWABLE_DIMENSION,
                    COLORDRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (mBitmap == null) {
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        mBorderRect[0f, 0f, width.toFloat()] = height.toFloat()
        mBorderRadius =
            ((mBorderRect.height() - mBorderWidth) / 2).coerceAtMost((mBorderRect.width() - mBorderWidth) / 2)
        mDrawableRect[mBorderWidth.toFloat(), mBorderWidth.toFloat(), mBorderRect.width() - mBorderWidth] =
            mBorderRect.height() - mBorderWidth
        mDrawableRadius = (mDrawableRect.height() / 2).coerceAtMost(mDrawableRect.width() / 2)
        updateShaderMatrix()
        invalidate()
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + mBorderWidth.toFloat(),
            (dy + 0.5f).toInt() + mBorderWidth.toFloat()
        )
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }


}