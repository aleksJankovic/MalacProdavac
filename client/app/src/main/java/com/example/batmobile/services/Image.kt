package com.example.batmobile.services

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import com.example.batmobile.R
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Image {
    companion object{
        fun uriToBase64(context: Context, uri: Uri): String {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val outputStream = ByteArrayOutputStream()

            bufferedInputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val imageByteArray = outputStream.toByteArray()
            return Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        }

        fun displayImageFromBase64(base64: String, image_view: ImageView){
            val decodedBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            image_view.setImageBitmap(bitmap)
        }

        fun base64ToDrawable(base64Image: String): Drawable{
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            return BitmapDrawable(null, decodedBitmap)
        }

        fun getResizedRoundedBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
            val scaleFactor = Math.max(targetWidth.toFloat() / bitmap.width, targetHeight.toFloat() / bitmap.height)

            val matrix = Matrix()
            matrix.postScale(scaleFactor, scaleFactor)

            val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = Color.RED // Promenite ovo na željenu boju okvira
            val paint = Paint()

            paint.color = color

            canvas.drawCircle(targetWidth / 2f, targetHeight / 2f, targetWidth / 2f, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

            return output
        }

        fun setImageResource(image_view: ImageView, productPicture: String?, categoryId: Int ){
            if(productPicture==null){
                when(categoryId){
                    1->{image_view.setImageResource(R.drawable.dairy_products)}
                    2->{image_view.setImageResource(R.drawable.fruits_and_vegetables)}
                    3->{image_view.setImageResource(R.drawable.meet_products)}
                    4->{image_view.setImageResource(R.drawable.fresh_meet)}
                    5->{image_view.setImageResource(R.drawable.cereals)}
                    6->{image_view.setImageResource(R.drawable.drinks)}
                    7->{image_view.setImageResource(R.drawable.vegetable_oils)}
                    8->{image_view.setImageResource(R.drawable.spread)}
                    -1->{image_view.setImageResource(R.drawable.image_blank)}
                }
            }
            else{
                displayImageFromBase64(productPicture, image_view)
            }
        }

        var slika = "iVBORw0KGgoAAAANSUhEUgAAAEwAAAAWCAYAAABqgnq6AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAH9SURBVFhH7ZjbbcQgEEUtpYs0ECX8pBA6oREKoQ+XNpkHsODF4PE6khPxcbVexsBweN3d5evtG5I+3j9hWRaUBb+u4J3h78Z6WNcAzqRYAG/pGWOOYh4s13uOJxkXIHB7FJf3c5lxEHL7qOp7L5ckAy7gO5s+k6xfY9912Roc2NyOAYPPlFNVbrA81qFYB5gHhzACJksJlskQpFzuLCdbQrPUKccf5V1gVA9BhIDt+fiZ++vnItID4zq+zJPajZOS+0IRvFinA2xqVxOYUhOYUhOYUhOYUj1gfMUWt0h9lTfEdiC937qZ/oFGwJpXNXoTy1dv6b9qVZZhKPFaj8kRpb7Fe8XygBajmLhejOMbi5A81WlpgVEC3qM3uRTYRrRSy8EV5tGSdzoaY5iYY4RoindPSwssi7ffbwAjE7q3/XFlN01oK9Y3s6d1O2Dk+J/qyeBpWwVvN6tkL0bbHMHbdK7iWE5N4Eb3AoaD7J4zsopWBDOOybmYIWK+/Jv01RV3J2B0Bg0HlM6lYUyAle3tb2eFbgOs0x4d1vIcV1GxCnuxtOJkhcUtOrJGI2mBUVm+xgu13tMA43abWy3efqkvtA6Pv2T6MT7fcpzG8uLqImmBHZUW2J/RCFieven0RT1gUw1NYEpNYEq1gV10BV+iI7lEk1qdmVJ27Xm6wA9V6UC/M2xTsAAAAABJRU5ErkJggg=="

    }
}