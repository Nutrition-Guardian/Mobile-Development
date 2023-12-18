package com.capstone.project.nutritionguardian.view.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.project.nutritionguardian.databinding.ActivityAddBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val CAMERA_REQUEST_CODE = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 1002
    private val PICK_IMAGE_REQUEST = 1
    private var capturedImageBitmap: Bitmap? = null
    private var currentImageUri: Uri? = null
    private var selectedImage: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnGallery.setOnClickListener {
                openImagePicker()
            }
            btnCamera.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@AddActivity,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        this@AddActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                }
            }
            btnDetect.setOnClickListener {
                uploadImage()
            }
        }

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

//    private fun startGallery() {
//        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }
//
//    private val launcherGallery = registerForActivityResult(
//        ActivityResultContracts.PickVisualMedia()
//    ) { uri: Uri? ->
//        if (uri != null) {
//            currentImageUri = uri
//            showImage()
//        } else {
//            Log.d("Photo Picker", "No media selected")
//        }
//    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        // Replace with your actual PHP script URL
        val uploadUrl =
            "https://fastapicc-php-ku3urc7swa-uc.a.run.app" // Example IP address, replace with your server's IP

        // Create request body
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "image.jpg",
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), selectedImage!!)
            )
            .build()

        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.d("API Response Body", responseBody)
                        }

                        // Parse the JSON response
                        val jsonResponse = JSONObject(responseBody!!)
                        val predictions = jsonResponse.getJSONArray("predictions")

                        // Get the image dimensions
                        val imageInfo = jsonResponse.getJSONObject("image")
                        val originalWidth = imageInfo.getInt("width")
                        val originalHeight = imageInfo.getInt("height")

                        // Resize the image to 450x450
                        val drawable = binding.imageView.drawable as BitmapDrawable
                        val originalBitmap = drawable.bitmap
//                        val resizedBitmap =
//                            Bitmap.createScaledBitmap(originalBitmap, 450, 450, true)

                        // Calculate scaling factors for drawing on the ImageView
                        val scaleX: Float = binding.imageView.width.toFloat() / originalWidth.toFloat()
                        val scaleY: Float = binding.imageView.height.toFloat() / originalHeight.toFloat()

                        // Create a mutable bitmap for drawing
                        val mutableBitmap =
                            Bitmap.createBitmap(originalBitmap)
                                .copy(Bitmap.Config.ARGB_8888, true)

                        // Get the canvas to draw on the bitmap
                        val canvas = Canvas(mutableBitmap)

                        // Iterate through predictions and draw bounding boxes
                        for (i in 0 until predictions.length()) {
                            val prediction = predictions.getJSONObject(i)

                            val centerX = prediction.getDouble("x").toFloat()
                            val centerY = prediction.getDouble("y").toFloat()
                            val width = prediction.getDouble("width").toFloat()
                            val height = prediction.getDouble("height").toFloat()

                            // Calculate corner points
                            val x1 = centerX - (width / 2)
                            val y1 = centerY - (height / 2)
                            val x2 = centerX + (width / 2)
                            val y2 = centerY + (height / 2)

                            // Draw bounding box
                            val paint = Paint()
                            paint.color = Color.RED
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 5f
                            canvas.drawRect(x1, y1, x2, y2, paint)

                            // Draw class label
                            val label = prediction.getString("class")
                            paint.color = Color.RED
                            paint.style = Paint.Style.FILL
                            paint.textSize = 30f
                            canvas.drawText(label, x1, y1 - 10, paint)
                        }

                        // Update the ImageView with the modified bitmap
                        runOnUiThread {
                            binding.imageView.setImageBitmap(mutableBitmap)
                        }

                    } else {
                        Log.e(
                            "API Error",
                            "Unsuccessful response: " + response.code + " " + response.message
                        )
                    }
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                } finally {
                    response.body?.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("API Error", "Failed to make API request: " + e.message)
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capturedImageBitmap?.let { outState.putParcelable("capturedImage", it) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        capturedImageBitmap = savedInstanceState.getParcelable("capturedImage")
        capturedImageBitmap?.let { binding.imageView.setImageBitmap(it) }
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val picturesDir = File(storageDir, "Camera")
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        val imageFile = File(picturesDir, "$imageFileName.jpg")

        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            MediaScannerConnection.scanFile(
                this,
                arrayOf(imageFile.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                binding.imageView.setImageBitmap(bitmap)

                // Convert Bitmap to ByteArray
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                selectedImage = byteArrayOutputStream.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
