package org.utl.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.utl.firebase.Persona

class FirestoreViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _personas = MutableStateFlow<List<Persona>>(emptyList())
    val personas: StateFlow<List<Persona>> = _personas

    init {
        fetchPersonas()
    }

    private fun fetchPersonas() {
        viewModelScope.launch {

            db.collection("Usuarios")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Error al detectar cambios en la coleccion: $e")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val personasList = mutableListOf<Persona>()
                        for (document in snapshot.documents) {
                            val persona = document.toObject(Persona::class.java)
                            persona?.let { personasList.add(it) }
                        }
                        _personas.value = personasList
                    } else {
                        println("Snapshot nulo al obtener personas")
                    }
                }
        }
    }
}