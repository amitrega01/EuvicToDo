package com.insudev.euvictodo.models

import com.google.firebase.firestore.*
import io.reactivex.Single
import java.lang.Exception

inline fun <reified FirebaseObject : FirebaseIdentifiableModel> FirebaseFirestore.getSimpleFirebaseList(
    path: String
): Single<List<FirebaseObject>> = Single.create<List<FirebaseObject>> { emitter ->
    val listenerRegistration: ListenerRegistration = collection(path).addSnapshotListener { querySnapshot, exception ->
        querySnapshot?.run {
            emitter.onSuccess(toFirebaseObjectList())
        } ?: exception?.run {
            emitter.onError(this)
        } ?: kotlin.run { emitter.onError(Exception()) }
    }
    emitter.setCancellable { listenerRegistration.remove() }
}

inline fun <reified FirebaseObject : FirebaseIdentifiableModel> QuerySnapshot.toFirebaseObjectList(): List<FirebaseObject> =
    this.documents.map { documentSnapshot ->
        documentSnapshot.toObject(FirebaseObject::class.java)?.apply {
            id = documentSnapshot.id
        } as FirebaseObject
    }

abstract class FirebaseIdentifiableModel(
    @Exclude var id: String = ""
)