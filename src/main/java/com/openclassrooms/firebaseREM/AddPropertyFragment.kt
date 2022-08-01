package com.openclassrooms.firebaseREM

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import com.openclassrooms.firebaseREM.Notifications.NotificationsWorker
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel
import androidx.work.WorkManager


class AddPropertyFragment : Fragment() {

    var mMainViewModel: MainViewModel? = null
    lateinit var type: TextView
    lateinit var price: TextView
    lateinit var avatar: TextView
    lateinit var description: TextView
    lateinit var surface: TextView
    lateinit var numberOfBathrooms: Spinner
    lateinit var numberOfBedrooms: Spinner
    lateinit var numberOfRooms: Spinner
    lateinit var city: TextView
    lateinit var address: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        arguments?.let {
            //TODO: mettre un bundle pour la modification
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_property, container, false)

        type = rootView.findViewById(R.id.add_type_property)
        price = rootView.findViewById(R.id.add_price_property)
        avatar = rootView.findViewById(R.id.add_avatar_property)
        description = rootView.findViewById(R.id.add_description_property)
        surface = rootView.findViewById(R.id.add_surface_property)
        numberOfBathrooms = rootView.findViewById(R.id.add_numberOfBathrooms_property)
        numberOfBedrooms = rootView.findViewById(R.id.add_numberOfBedrooms_property)
        city = rootView.findViewById(R.id.add_city_property)
        address = rootView.findViewById(R.id.add_address_property)
        numberOfRooms = rootView.findViewById(R.id.add_numberOfRooms_property)
        val adapterForRooms = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.add_numberOfRooms_property, android.R.layout.simple_spinner_item
            )
        }
        adapterForRooms!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        numberOfRooms.setAdapter(adapterForRooms)
        numberOfBathrooms.setAdapter(adapterForRooms)
        numberOfBedrooms.setAdapter(adapterForRooms)

        rootView.findViewById<Button>(R.id.create).setOnClickListener {

            if (!type.text.toString().equals("") && !price.text.toString().equals("") && !description.text.toString().equals("") && !surface.text.toString().equals(
                    ""
                ) && !city.text.toString().equals("")
            ) {
                mMainViewModel?.createProperty(
                    type.text.toString(),
                    Integer.parseInt(price.text.toString()),
                    avatar.text.toString(),
                    description.text.toString(),
                    Integer.parseInt(surface.text.toString()),
                    Integer.parseInt(numberOfRooms.selectedItem.toString()),
                    Integer.parseInt(numberOfBathrooms.selectedItem.toString()),
                    Integer.parseInt(numberOfBedrooms.selectedItem.toString()),
                    city.text.toString(),
                    address.text.toString()
                )
                val intent = Intent(context, ItemListActivity::class.java).apply {
                }
                context?.startActivity(intent)
            } else {
                val text = "Please complete all the required fields"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
        }

        return rootView
    }

    fun createNotification() {
        val oneTimeWorkRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationsWorker::class.java)
            //.setInitialDelay(getMilliseconds(), TimeUnit.MILLISECONDS)
            .addTag("Notify")
            .build()
        WorkManager.getInstance().enqueue(oneTimeWorkRequest)
    }

}