package com.openclassrooms.firebaseREM.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.openclassrooms.firebaseREM.model.Agent

class Manager () {

    var mRepository: Repository? = null
    init { mRepository = Repository() }

    // --- COLLECTION REFERENCE ---

    fun getUsersCollection(listener: Repository.AgentsListener?) { if (listener != null) { mRepository?.getUsersCollection(listener) } }

    fun getPropertysCollection(listener: Repository.PropertysListener?) { if (listener != null) {
        mRepository?.getPropertysCollection(listener) } }

    fun getElementsCollection(listener: Repository.ElementsListener?) { if (listener != null) {
        mRepository?.getElementsCollection(listener) } }

    // --- CREATE FIREBASE ---

    fun createUser(id: String?, avatar: String?, name: String?): Task<Void?>? { return mRepository?.createUser(id, avatar, name) }

    fun createProperty(type: String, price: Int, avatar1: String, description: String, surface: Int, numberOfRooms: Int, numberOfBathrooms: Int, numberOfBedrooms: Int, city: String, address: String): Task<DocumentReference?>? {
        return mRepository?.createProperty(type, price, avatar1, description, surface, numberOfRooms, numberOfBathrooms, numberOfBedrooms, city, address) }

    fun createElement(photo: String, propertyId: String?, isSelected: Boolean): Task<DocumentReference?>? {
        return mRepository?.createElement(photo, propertyId, isSelected)
    }

    // --- GET FIREBASE ---

    fun getUser(id: String?, listener: Repository.OnUserSuccessListener?) { if (listener != null) { mRepository?.getUser(id, listener) } }

    fun getCurrentUser() : Agent? { return mRepository?.getCurrentUser()}

    // --- UPDATE FIREBASE ---

    fun updateUserName(id: String?, name: String?): Task<Void?>? { return mRepository?.updateUserName(id, name) }

    fun updateUserAvatar(id: String?, avatar: String?): Task<Void?>? { return mRepository?.updateUserAvatar(id, avatar) }

    fun updateElementSelected(id: String?, newSelected: Boolean): Task<Void?>? { return mRepository?.updateElementSelected(id, newSelected) }

    // --- DELETE FIREBASE ---

    fun deleteUser(id: String?) { mRepository?.deleteUser(id) }

    fun deleteElement(id: String?, onDeleted: () -> Unit) { mRepository?.deleteElement(id, onDeleted)}
}