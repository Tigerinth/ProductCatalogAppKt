package com.tigersoft.productbookv2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.tigersoft.productbookv2.databinding.ActivityInfoBinding
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProductInfoActivity : AppCompatActivity() {

    var selectedBitmap : Bitmap? = null
    var selectedBitmapbar : Bitmap? = null
    private lateinit var binding: ActivityInfoBinding
    private lateinit var db : SQLiteDatabase
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var activityResultLauncher1: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = this.openOrCreateDatabase("Products", Context.MODE_PRIVATE,null)


        registerLauncher()

        val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")) {
            binding.productText.setText("") // urun ismi
            binding.infoText.setText("")    // aciklamamiz
            binding.button.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectfoto)
            val selectedBarcodeBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectbarkod)
            binding.imageView.setImageBitmap(selectedImageBackground)
            binding.imageView2.setImageBitmap(selectedBarcodeBackground)

        } else {
            binding.button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)

            val cursor = db.rawQuery("SELECT * FROM products WHERE id = ?", arrayOf(selectedId.toString()))

            val productNameIndex = cursor.getColumnIndex("productname")
            val infoNameIndex = cursor.getColumnIndex("infoname")
            val imageIndex = cursor.getColumnIndex("image")
            val barcodeIndex = cursor.getColumnIndex("barcode")

            while (cursor.moveToNext()) {
                binding.productText.setText(cursor.getString(productNameIndex))
                binding.infoText.setText(cursor.getString(infoNameIndex))

                val byteArray = cursor.getBlob(imageIndex)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)

                val barbyteArray = cursor.getBlob(barcodeIndex)
                val barbitmap = BitmapFactory.decodeByteArray(barbyteArray,0,barbyteArray.size)

                binding.imageView.setImageBitmap(bitmap)
                binding.imageView2.setImageBitmap(barbitmap)

            }

            cursor.close()

        }


    }


    fun save(view: View) {

        val productName = binding.productText.text.toString()
        val infoName = binding.infoText.text.toString()

        if (selectedBitmap != null && selectedBitmapbar != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            val smallBitmapbar = makeSmallerBitmap(selectedBitmapbar!!,300)

            val outputStream = ByteArrayOutputStream()
            val outputStream1 = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,68,outputStream)
            smallBitmapbar.compress(Bitmap.CompressFormat.PNG,68,outputStream1)
            val byteArray = outputStream.toByteArray()
            val byteArray1 = outputStream1.toByteArray()

            try {

                db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY, productname VARCHAR, infoname VARCHAR, image BLOB ,barcode BLOB)")         //YEAR

                val sqlString = "INSERT INTO products (productname, infoname, image, barcode) VALUES (?, ?, ?, ?)"           //YEAR
                val statement = db.compileStatement(sqlString)
                statement.bindString(1, productName)
                statement.bindString(2, infoName)
                statement.bindBlob(3, byteArray)
                statement.bindBlob(4, byteArray1)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)

            //finish()
        }
    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    fun selectImage(view: View) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    fun selectImage1(view: View) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher1.launch(intentToGallery)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this@ProductInfoActivity.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)

                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@ProductInfoActivity.contentResolver, imageData)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        activityResultLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result1 ->
            if (result1.resultCode == RESULT_OK) {
                val intentFromResult = result1.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this@ProductInfoActivity.contentResolver, imageData!!)
                            selectedBitmapbar = ImageDecoder.decodeBitmap(source)
                            binding.imageView2.setImageBitmap(selectedBitmapbar)

                        } else {
                            selectedBitmapbar = MediaStore.Images.Media.getBitmap(this@ProductInfoActivity.contentResolver, imageData)
                            binding.imageView2.setImageBitmap(selectedBitmapbar)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@ProductInfoActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }


}