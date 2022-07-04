package com.tigersoft.productbookv2
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.tigersoft.productbookv2.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var productList : ArrayList<Product>
    private lateinit var productAdapter : ProductAdapter
    private lateinit var db : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productList = ArrayList<Product>()
        productAdapter = ProductAdapter(productList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = productAdapter

        try {

            db = this.openOrCreateDatabase("Products", Context.MODE_PRIVATE,null)

            val cursor = db.rawQuery("SELECT * FROM products",null)
            val productNameIndex = cursor.getColumnIndex("productname")
            val idIndex = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(productNameIndex)
                val id = cursor.getInt(idIndex)
                val product = Product(name,id)
                productList.add(product)
            }

            productAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //Inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_product,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_product_item) {
            val intent = Intent(this,ProductInfoActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        } else if (item.itemId == R.id.delete_all_items){

            // uyari mesaji yaz

            db.execSQL("DELETE FROM products")
            finish()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


}