package com.app.talent360.home.fragment.homeFragment.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.app.talent360.util.Constants.Companion.COLLECTION_PATH_TPO
import com.app.talent360.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _metaCounts: MutableLiveData<Resource<Counts>> = MutableLiveData()
    val metaCounts: LiveData<Resource<Counts>> = _metaCounts

    private var countListener: ListenerRegistration? = null

    fun fetchCounts() {
        viewModelScope.launch(IO) {
            try {
                _metaCounts.postValue(Resource.loading())
                val tpoCount = getCount(COLLECTION_PATH_TPO)
                val studentCount = getCount(COLLECTION_PATH_STUDENT)
                val jobCount = getCount(COLLECTION_PATH_COMPANY)
                val mockCount = getCount(COLLECTION_PATH_MOCK)
                val notificationCount = getNotificationCount()
                val count = Counts(tpoCount, studentCount, jobCount, mockCount, notificationCount)
                _metaCounts.postValue(Resource.success(count))
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _metaCounts.postValue(Resource.error(errorMessage))
            }
        }
    }

    private suspend fun getNotificationCount(): Int {
        val countDeffered = CompletableDeferred<Int>()
        val notificationRef = mFirestore.collection(COLLECTION_PATH_NOTIFICATION).whereEqualTo("type", "BROADCAST")
        countListener = notificationRef.addSnapshotListener { value, error ->
            if (error != null) {
                countDeffered.completeExceptionally(error)
                return@addSnapshotListener
            }
            val count = value?.documents?.count()!!
            countDeffered.complete(count)
        }
        return countDeffered.await()
    }

    private suspend fun getCount(collectionPath: String): Int {
        val countDeffered = CompletableDeferred<Int>()
        val countRef = mFirestore.collection(collectionPath)
        countListener = countRef
            .addSnapshotListener { value, error ->
                if (error != null) {
                    countDeffered.completeExceptionally(error)
                    return@addSnapshotListener
                }
                val count = value?.documents?.count()!!
                countDeffered.complete(count)
            }
        return countDeffered.await()
    }

    override fun onCleared() {
        countListener?.remove()
        super.onCleared()
    }
}

data class Counts(
    var tpoCount: Int = 0,
    var studentCount: Int = 0,
    var jobCount: Int = 0,
    var mockCount: Int = 0,
    var notificationCount: Int = 0,
    var user: User? = null
)