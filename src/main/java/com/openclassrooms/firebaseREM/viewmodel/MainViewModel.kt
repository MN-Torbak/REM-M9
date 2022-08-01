package com.openclassrooms.firebaseREM.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.openclassrooms.firebaseREM.api.Manager
import com.openclassrooms.firebaseREM.api.Repository
import com.openclassrooms.firebaseREM.model.Agent
import com.openclassrooms.firebaseREM.model.Property

class MainViewModel : ViewModel() {
    var mManager: Manager
    val Propertys = MutableLiveData<List<Property?>>(listOf())
    fun getLiveLocation(): LiveData<Location?> { return liveLocation }
    private val liveLocation = MutableLiveData<Location>()
    fun updateLocation(location: Location?) { liveLocation.value = location }

    init {
        mManager = Manager()
        getPropertysCollection(object : Repository.PropertysListener {
            override fun onPropertysSuccess(propertys: List<Property?>?) {
                Propertys.value = propertys
            }
        })
    }

    fun getUsersCollection(listener: Repository.AgentsListener?) {
        mManager.getUsersCollection(listener)
    }

    fun getPropertysCollection(listener: Repository.PropertysListener?) {
        mManager.getPropertysCollection(listener)
    }

    fun getElementsCollection(listener: Repository.ElementsListener?) {
        mManager.getElementsCollection(listener)
    }

    fun createUser(id: String?, avatar: String?, name: String?): Task<Void?>? {
        return mManager.createUser(id, avatar, name)
    }

    fun getUser(id: String?, listener: Repository.OnUserSuccessListener?) {
        mManager.getUser(id, listener)
    }

    fun getCurrentUser(): Agent? {
        return mManager.getCurrentUser()
    }

    fun updateUserName(id: String?, name: String?): Task<Void?>? {
        return mManager.updateUserName(id, name)
    }

    fun updateUserAvatar(id: String?, avatar: String?): Task<Void?>? {
        return mManager.updateUserAvatar(id, avatar)
    }

    fun deleteUser(id: String?) {
        mManager.deleteUser(id)
    }

    fun createProperty(
        type: String,
        price: Int,
        avatar1: String,
        description: String,
        surface: Int,
        numberOfRooms: Int,
        numberOfBathrooms: Int,
        numberOfBedrooms: Int,
        city: String,
        address: String
    ): Task<DocumentReference?>? {
        getPropertysCollection(object : Repository.PropertysListener {
            override fun onPropertysSuccess(propertys: List<Property?>?) {
                Propertys.value = propertys
            }
        })
        return mManager.createProperty(
            type,
            price,
            avatar1,
            description,
            surface,
            numberOfRooms,
            numberOfBathrooms,
            numberOfBedrooms,
            city,
            address
        )
    }

    fun createElement(photo: String, propertyId: String?, isSelected: Boolean): Task<DocumentReference?>? {
        return mManager.createElement(photo, propertyId, isSelected)
    }

    fun deleteElement(id: String?, onDeleted: () -> Unit) {
        mManager.deleteElement(id, onDeleted)
    }

    fun updateElementSelected (id: String?, newSelected: Boolean) {
        mManager.updateElementSelected(id, newSelected)
    }
}