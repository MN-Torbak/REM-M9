package com.openclassrooms.firebaseREM

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.firebaseREM.model.Property
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel


class AddElementFragment : Fragment() {

    var mMainViewModel: MainViewModel? = null
    private var item: Property? = null
    lateinit var photoUrl1: TextView
    lateinit var photoUrl2: TextView
    lateinit var photoUrl3: TextView
    lateinit var photoUrl4: TextView
    lateinit var photoUrl5: TextView
    lateinit var photoUrl6: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        arguments?.let { bundle ->
            if (bundle.containsKey(ItemDetailFragment.ARG_ITEM_ID)) {
                item = bundle.getSerializable(ItemDetailFragment.ARG_ITEM_ID) as? Property
                //TODO: mettre un bundle pour la modification
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_element, container, false)
        photoUrl1 = rootView.findViewById(R.id.add_photo1_element)
        photoUrl2 = rootView.findViewById(R.id.add_photo2_element)
        photoUrl3 = rootView.findViewById(R.id.add_photo3_element)
        photoUrl4 = rootView.findViewById(R.id.add_photo4_element)
        photoUrl5 = rootView.findViewById(R.id.add_photo5_element)
        photoUrl6 = rootView.findViewById(R.id.add_photo6_element)

        rootView.findViewById<Button>(R.id.create_elements).setOnClickListener {

            if (!photoUrl1.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl1.text.toString(), item?.id, false)
            }
            if (!photoUrl2.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl2.text.toString(), item?.id, false)
            }
            if (!photoUrl3.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl3.text.toString(), item?.id, false)
            }
            if (!photoUrl4.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl4.text.toString(), item?.id, false)
            }
            if (!photoUrl5.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl5.text.toString(), item?.id, false)
            }
            if (!photoUrl6.text.toString().equals("")) {
                mMainViewModel?.createElement(photoUrl6.text.toString(), item?.id, false)
            }

            val intent = Intent(context, ItemListActivity::class.java).apply {
            }
            context?.startActivity(intent)
        }


        return rootView
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}