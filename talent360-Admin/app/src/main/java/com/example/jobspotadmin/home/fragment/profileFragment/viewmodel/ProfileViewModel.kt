package com.app.talent360.home.fragment.profileFragment.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.talent360.model.Tpo
import com.app.talent360.util.Constants
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_TPO
import com.app.talent360.util.Constants.Companion.TPO_IMAGE_STORAGE_PATH
import com.app.talent360.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "ProfileViewModel"

class ProfileViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var imageUri: Uri? = null

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    private val _currentUser: MutableLiveData<Tpo> = MutableLiveData(Tpo())
    val currentUser: LiveData<Tpo> = _currentUser

    private val _updateStatus : MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val updateStatus : LiveData<UiState> = _updateStatus

    private val _deleteStatus : MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val deleteStatus : LiveData<UiState> = _deleteStatus

    fun fetchUser(uid: String) {
        viewModelScope.launch {
            val userRef = mFirestore.collection(COLLECTION_PATH_TPO).document(uid).get().await()
            val user = userRef.toObject(Tpo::class.java)!!
            Log.d(TAG, "User : $user")
            _currentUser.postValue(user)
        }
    }

    fun updateUser(tpo: Tpo) {
       try {
           viewModelScope.launch(IO) {
               _updateStatus.postValue(UiState.LOADING)
               if (!tpo.imageUri.startsWith("https://firebasestorage.googleapis.com/")) {
                   val editUserRef =
                       mStorage.getReference(TPO_IMAGE_STORAGE_PATH).child(tpo.uid)
                   editUserRef.putFile(Uri.parse(tpo.imageUri)).await()
                   tpo.imageUri = editUserRef.downloadUrl.await().toString()
               }

               val currentUser = mAuth.currentUser!!
               if (
                   tpo.username != currentUser.displayName ||
                   tpo.email != currentUser.email ||
                   tpo.imageUri != currentUser.photoUrl.toString()
               ) {
                   val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(tpo.username)
                       .setPhotoUri(Uri.parse(tpo.imageUri)).build()
                   currentUser.updateProfile(profileUpdate).await()
                   currentUser.updateEmail(tpo.email).await()
               }

               val editUserRef = mFirestore.collection(COLLECTION_PATH_TPO).document(tpo.uid)
               editUserRef.set(tpo).await()

               _updateStatus.postValue(UiState.SUCCESS)
           }
       } catch (e : Exception){
           Log.d(TAG, "Error : ${e.message}")
           _updateStatus.postValue(UiState.FAILURE)
       }
    }

    fun deleteAccount(tpo: Tpo){
        viewModelScope.launch(IO) {
            try {
                _deleteStatus.postValue(UiState.LOADING)
                val tpoId = tpo.uid
                val tpoImagePath = "$TPO_IMAGE_STORAGE_PATH/$tpoId"
                mAuth.currentUser?.delete()?.await()
                mFirestore.collection(COLLECTION_PATH_TPO).document(tpoId).delete().await()
                mFirestore.collection(Constants.COLLECTION_PATH_ROLE).document(tpoId).delete().await()
                mStorage.reference.child(tpoImagePath).delete().await()
                _deleteStatus.postValue(UiState.SUCCESS)
            } catch (error : Exception){
                Log.d(TAG, "deleteAccount Error: ${error.message}")
                _deleteStatus.postValue(UiState.FAILURE)
            }

        }
    }

}