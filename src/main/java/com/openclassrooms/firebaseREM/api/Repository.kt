package com.openclassrooms.firebaseREM.api

import android.util.Log
import androidx.annotation.WorkerThread
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.openclassrooms.firebaseREM.Element
import com.openclassrooms.firebaseREM.model.Agent
import com.openclassrooms.firebaseREM.model.Property
import com.openclassrooms.firebaseREM.model.PropertyRoom

class Repository () {

    private val COLLECTION_USER = "users"
    private val COLLECTION_PROPERTY = "propertys"
    private val COLLECTION_ELEMENT = "elements"

    private fun enablePersistence() {
        // [START rtdb_enable_persistence]
        Firebase.database.setPersistenceEnabled(true)
        // [END rtdb_enable_persistence]
    }

    /* USERS */

    fun getCurrentUser(): Agent {
        FirebaseAuth.getInstance().currentUser
        val currentUser = FirebaseAuth.getInstance().currentUser
        val agent: Agent = Agent(currentUser?.uid, currentUser?.displayName, currentUser?.photoUrl.toString())
        return agent
    }

    fun getUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER)
    }

    fun getUsersCollection(listener: AgentsListener) {
        val collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_USER)
        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onAgentsSuccess(mapAgents(task))
            } else {
                Log.d("AgentsListener", "Error getting documents: ", task.exception)
            }
        }
    }

    private fun mapAgents(task: Task<QuerySnapshot>): List<Agent> {
        val agents: MutableList<Agent> = ArrayList<Agent>()
        for (document in task.result) {
            val agent: Agent = document.toObject<Agent>(Agent::class.java)
            agents.add(agent)
        }
        return agents
    }

    fun createUser(id: String?, avatar: String?, name: String?): Task<Void?> {
        val userToCreate = Agent(id, avatar, name)
        return getUsersCollection().document(id.toString()).set(userToCreate)
    }

    fun getUser(id: String?, listener: OnUserSuccessListener) {
        val task = this.getUsersCollection().document(id!!).get().addOnSuccessListener { documentSnapshot ->
            val currentAgent: Agent? = documentSnapshot.toObject<Agent>(Agent::class.java)
            listener.onUserSuccess(currentAgent)
        }
    }

    interface AgentsListener {
        fun onAgentsSuccess(agents: List<Agent?>?)
    }

    interface OnUserSuccessListener {
        fun onUserSuccess(agent: Agent?)
    }

    fun deleteUser(id: String?) {
        if (id != null) {
            getUsersCollection().document(id).delete()
        }
    }

    fun updateUserName(id: String?, name: String?): Task<Void?> {
        return getUsersCollection().document(id.toString()).update("name", name)
    }

    fun updateUserAvatar(id: String?, avatar: String?): Task<Void?> {
        return getUsersCollection().document(id.toString()).update("avatar", avatar)
    }

    fun updatePropertyId(id: String?, newId: String?): Task<Void?> {
        return getPropertysCollection().document(id.toString()).update("id", newId)
    }

    fun updateElementId(id: String?, newId: String?): Task<Void?> {
        return getElementsCollection().document(id.toString()).update("elementId", newId)
    }

    fun updateElementSelected(id: String?, newSelected: Boolean): Task<Void?> {
        return getElementsCollection().document(id.toString()).update("selected", newSelected)
    }

    /* PROPERTY Firebase*/

    fun getPropertysCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PROPERTY)
    }

    fun getPropertysCollection(listener: PropertysListener) {
        val collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_PROPERTY)
        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onPropertysSuccess(mapPropertys(task))
            } else {
                Log.d("PropertysListener", "Error getting documents: ", task.exception)
            }
        }
    }

    private fun mapPropertys(task: Task<QuerySnapshot>): List<Property> {
        val propertys: MutableList<Property> = ArrayList<Property>()
        for (document in task.result) {
            val property: Property = document.toObject(Property::class.java)
            if (property.id != document.id ) {
                property.id = document.id
                updatePropertyId(document.id, document.id)
            }
            propertys.add(property)
        }
        return propertys
    }

    interface PropertysListener { fun onPropertysSuccess(propertys: List<Property?>?) }

    fun createProperty(type: String, price: Int, avatar1: String, description: String, surface: Int, numberOfRooms: Int, numberOfBathrooms: Int, numberOfBedrooms: Int, city: String, address: String): Task<DocumentReference?> {
        val propertyToCreate = Property(type, price, avatar1, description, surface, numberOfRooms, numberOfBathrooms, numberOfBedrooms, city, address)
        return getPropertysCollection().add(propertyToCreate)
    }

    /* Element */

    fun getElementsCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_ELEMENT)
    }

    fun getElementsCollection(listener: ElementsListener) {
        val collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_ELEMENT)
        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onElementsSuccess(mapElements(task))
            } else {
                Log.d("ElementListener", "Error getting documents: ", task.exception)
            }
        }.addOnFailureListener { exception ->
            Log.d("bug", exception.message.toString())

        }
    }

    private fun mapElements(task: Task<QuerySnapshot>): List<Element> {
        val elements: MutableList<Element> = ArrayList<Element>()
        for (document in task.result) {
            val element: Element = document.toObject(Element::class.java)
            if (element.elementId != document.id ) {
                element.elementId = document.id
                updateElementId(document.id, document.id)
            }
            elements.add(element)
        }
        return elements
    }

    fun createElement(photo: String, propertyId: String?, isSelected: Boolean): Task<DocumentReference?> {
        val elementToCreate = Element(photo, propertyId, isSelected)
        return getElementsCollection().add(elementToCreate)
    }

    fun deleteElement(id: String?, onDeleted: () -> Unit) {
        if (id != null) {
            getElementsCollection().document(id).delete()
                .addOnCompleteListener{
                    onDeleted()
                }
                .addOnFailureListener {onDeleted() /*TODO: chercher dans la bdd room*/}
        }
    }

    interface ElementsListener { fun onElementsSuccess(elements: List<Element?>?) }


}