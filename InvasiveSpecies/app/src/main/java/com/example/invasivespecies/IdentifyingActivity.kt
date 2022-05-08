package com.example.invasivespecies

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.URL
import kotlin.random.Random


class IdentifyingActivity: AppCompatActivity() {
    /*
    * Given: User image of an invasive species
    * Do the following:
    * - Attempt to identify the species using image classification API (not necessary)
    * - Show a scrolling list of invasive species with images and names
    * - Have user select the species they saw from list
    *   - If species not in list, have user select "other" from list
    * - Update frequency of selected species in Firebase
    * - Display "thank you" message and shut down
    */
    lateinit var mListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identifying)

        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        mListView = findViewById(R.id.listview)
        mListView.adapter = CustomAdapter(this, generateValues())
    }

    private fun generateValues(): List<List<Any>> {
        val data = ArrayList<ArrayList<Any>>()
        var filepath = "invasive_species_names.txt"
        var inputStream: InputStream = assets.open(filepath)

        inputStream.bufferedReader().forEachLine {
            val dataItem = ArrayList<Any>()
            dataItem.add(it)
            data.add(dataItem)
        }

        filepath = "image_uris.txt"
        inputStream = assets.open(filepath)
        val fileLines = inputStream.bufferedReader().lines().toArray()
        lateinit var defaultDrawable: Drawable

        for (i in 0 until data.size) {
            if (i < fileLines.size) {
                val d: Drawable = Drawable.createFromStream(URL(fileLines[i].toString()).content as InputStream, "src name")
                data[i].add(d)
                defaultDrawable = d

                data[i].add(fileLines[i])
            } else {
                val j = Random.nextInt(0, fileLines.size - 1)
                data[i].add(data[j][1])
                data[i].add(fileLines[j])
            }
        }

        return data
    }

    class CustomAdapter(context: Context, private var values: List<List<Any>>):
            ArrayAdapter<List<Any>>(context, R.layout.list_item_view, R.id.text_list_element, values) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            super.getView(position, convertView, parent)

            val rowView = LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false)

            val button: Button = rowView.findViewById(R.id.button_id)
            button.text = values[position][0] as String

            val image: ImageView = rowView.findViewById(R.id.image_list_element)
            image.setImageDrawable(values[position][1] as Drawable)

            button.setOnClickListener {
                val intent = Intent(context, ReportingActivity::class.java)
                intent.putExtra("selection name", button.text.toString())
                intent.putExtra("selection image uri", values[position][2] as String)
                context.startActivity(intent)
            }

            return rowView
        }
    }

}