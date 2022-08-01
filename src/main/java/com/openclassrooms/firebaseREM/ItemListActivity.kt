package com.openclassrooms.firebaseREM

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.openclassrooms.firebaseREM.model.Property
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel


class ItemListActivity : AppCompatActivity() {

    private var twoPane: Boolean = false
    var mMainViewModel: MainViewModel? = null
    lateinit var recyclerView: RecyclerView


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        setupAutoCompleteTextView(propertyListAddress)
        createNotificationChannel()
        enablePersistence()
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        recyclerView = findViewById(R.id.item_list)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        findViewById<ImageButton>(R.id.add_property_button).setOnClickListener {
            createProperty()
        }

        findViewById<ImageButton>(R.id.go_to_map).setOnClickListener {
            goToMapFragment()
        }

        findViewById<ImageButton>(R.id.go_to_list).setOnClickListener {
            goToListFragment()
        }

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            twoPane = true
            findViewById<ImageButton>(R.id.go_to_list).visibility = View.GONE
        }

        setupRecyclerView(recyclerView)

        mMainViewModel?.Propertys?.observe(this) {
            if (it != null) {
                propertyList.addAll(it)
                propertyList.addAll(it)
                for (Property in it) {
                    propertyListAddress.add(Property?.address.toString())
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    val propertyList: MutableList<Property?> = ArrayList()
    val propertyListAddress: MutableList<String> = ArrayList()

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, propertyList, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private val propertyList: List<Property?>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        var selected = -1

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = propertyList[position]
            Glide.with(holder.avatarView.getContext())
                .load(item?.avatar1)
                .centerCrop()
                .into(holder.avatarView)
            holder.typeView.text = item?.type
            holder.cityView.text = item?.city
            holder.priceView.text = item?.price.toString() + " €"
            holder.itemView.setBackgroundColor(if (position == selected) {Color.parseColor("#C6E5F3") } else {Color.parseColor("#FFFFFF") })
            holder.itemView.setOnClickListener {v ->
                notifyItemChanged(selected)
                selected = position
                notifyItemChanged(selected)
                val item = v.tag as Property
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable(ItemDetailFragment.ARG_ITEM_ID, item)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item)
                    }
                    v.context.startActivity(intent)
                }
            }


            with(holder.itemView) {
                tag = item
                //holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }

        override fun getItemCount() = propertyList.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val avatarView: ImageView = view.findViewById(R.id.avatar_property)
            val typeView: TextView = view.findViewById(R.id.type_property)
            val cityView: TextView = view.findViewById(R.id.city_property)
            val priceView: TextView = view.findViewById(R.id.price_property)
        }
    }

    fun createProperty() {
        val fragment = AddPropertyFragment().apply {
            arguments = Bundle().apply {
            }
        }

        if (twoPane) {
            supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment)
                .commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(this.toString())
                .commit()
        }
    }

    fun goToMapFragment() {
        val fragment = Map().apply {
            arguments = Bundle().apply {
            }
        }

        if (twoPane) {
            supportFragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment)
               .commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(this.toString())
                .commit()
        }
    }

    fun goToListFragment() {
        val intent = Intent(this, ItemListActivity::class.java)
        startActivity(intent)
    }

    private fun setupAutoCompleteTextView(listOfAddress: MutableList<String>) {
        val autoTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.select_dialog_item, listOfAddress)
        autoTextView.threshold = 1
        autoTextView.setAdapter(adapter)
        autoTextView.setDropDownBackgroundResource(R.color.colorWhite)
        autoTextView.setDropDownWidth(1000)
        autoTextView.setOnItemClickListener { parent, view, position, id ->
            val item = autoTextView.text
            if (twoPane) {
                val fragment = ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ItemDetailFragment.ARG_ITEM_ID_BY_STRING, item.toString())
                    }
                }
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(this, ItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailFragment.ARG_ITEM_ID_BY_STRING, item.toString())
                }
                startActivity(intent)
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("k", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun enablePersistence() {
        // [START rtdb_enable_persistence]
        Firebase.database.setPersistenceEnabled(true)
        // [END rtdb_enable_persistence]
    }

}
