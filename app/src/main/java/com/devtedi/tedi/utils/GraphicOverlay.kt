package com.devtedi.tedi.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View

class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs){
    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()

    // Matrix for transforming from image coordinates to overlay view coordinates.
    val transformationMatrix = Matrix()
    var imageWidth = 0
        private set
    var imageHeight = 0
        private set

    // The factor of overlay View size to image size. Anything in the image coordinates need to be
    // scaled by this amount to fit with the area of overlay View. Semakin besar maka nanti pas prediksi bakaln kedeteksi multiple item pada satu bounding box
    private var scaleFactor = 1.0f

    // The number of horizontal pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleWidthOffset = 0f

    // The number of vertical pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleHeightOffset = 0f
    var isImageFlipped = false
    // Variable for Doing a new Transformation If new image inserted
    private var needUpdateTransformation = true
    abstract class Graphic(private val overlay: GraphicOverlay) {
        val applicationContext : Context get() = overlay.context.applicationContext

        abstract fun draw(canvas : Canvas?)

        fun isImageFlipped() : Boolean {
            return overlay.isImageFlipped
        }

        // Transform the Image scale to View Scale
        fun scale(imagePixel : Float) : Float {
            return imagePixel * overlay.scaleFactor
        }

        // Return a [Matrix] For transforming from image coordinate to overlay view coordinates
        // Dalam artian Ini code return sebuah Matrix yang digunakan untuk transformasi ke overlay
        fun getTransformationMatrix() : Matrix {
            return overlay.transformationMatrix
        }

        // DO Draw
        fun postInvalidate() {
            overlay.postInvalidate()
        }

        fun translateX(x : Float ): Float {
            // Ini cuman Adjusting X coordinate dari gambar ke view coordinate system, dalam artian
            // disini ditentukan besar / kecil koordinat X pada saat transformasi aplikasi
            return if (overlay.isImageFlipped) {
                overlay.width - ( scale(x) - overlay.postScaleWidthOffset)
            }
            else {
                scale(x) - overlay.postScaleWidthOffset
            }
        }
        fun translateY(y : Float) : Float {
            return scale(y) - overlay.postScaleHeightOffset
        }

    }
    // Removes Graphics Dari Overlay ( Maksudnya ngapus seluruh bounding box setiap n sec )
    fun clear() {
        synchronized(lock)  {graphics.clear()}
        postInvalidate()
    }
    // Nambahin Graphics ke Overlay
    fun add(graphic: ObjectGraphic){
        synchronized(lock) {graphics.add(graphic)}
    }

    // Ngapus Graphics Dari overlay

    fun remove(graphic : Graphic) {
        synchronized(lock) {graphics.remove(graphic)}
        postInvalidate()
    }

    fun setImageSourceInfo(imageWidth : Int, imageHeight : Int, isFlipped : Boolean) {
        synchronized(lock) {
            this.imageWidth = imageWidth
            this.imageHeight = imageHeight
            isImageFlipped = isFlipped
            needUpdateTransformation = true
        }
        postInvalidate()
    }

    private fun updateTransformation() {

        // INi mastiin inputan imagenya valid
        if (!needUpdateTransformation || imageWidth <=0 || imageHeight <= 0) return
        else {
            // Kek 16:9 di TV , fungsi aspectRatio untuk tau rasionya aja, buat scaling Imagenya pada saat di porting ke view
            val viewAspectRatio = width.toFloat() / height
            val imageAspectRatio = imageWidth.toFloat() / imageHeight

            postScaleHeightOffset =0f
            postScaleWidthOffset = 0f

            if (viewAspectRatio > imageAspectRatio) {
                scaleFactor = width.toFloat() / imageWidth
                postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 4
                postScaleWidthOffset = (height.toFloat()  / imageAspectRatio - width) / 4
            }

            else {
                scaleFactor =
                    height.toFloat() / imageHeight
                postScaleWidthOffset = (height.toFloat() / imageAspectRatio - width) / 4
                postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 4
            }

            transformationMatrix.reset()
            transformationMatrix.setScale(scaleFactor, scaleFactor)
            transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
            if (isImageFlipped) transformationMatrix.postScale(-1f, 1f, width/ 2f, height/ 2f)
            needUpdateTransformation = false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            updateTransformation()
            for (graphic in graphics)
            {
                graphic.draw(canvas)
            }
        }
    }
}
