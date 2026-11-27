package com.CampusFind.com.data

import com.CampusFind.com.model.AdminDashboardMetrics
import com.CampusFind.com.model.AdminPendingClaim
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.util.Calendar

class AdminDashboardRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMetrics(
        onSuccess: (AdminDashboardMetrics) -> Unit,
        onError: (String) -> Unit
    ) {
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = Timestamp(midnight.time)
        val newFound = countToday("foundItems", startOfToday)
        val matches = countToday("matches", startOfToday)
        val claims = countToday("claims", startOfToday)

        Tasks.whenAll(newFound, matches, claims)
            .addOnSuccessListener {
                onSuccess(AdminDashboardMetrics(newFound.result, matches.result, claims.result))
            }
            .addOnFailureListener { exception ->
                onError(exception.cause?.localizedMessage ?: exception.localizedMessage
                    ?: "Unable to load today's metrics.")
            }
    }

    private fun countToday(collection: String, startOfToday: Timestamp): Task<Long> =
        firestore.collection(collection)
            .whereGreaterThanOrEqualTo("createdAt", startOfToday)
            .count()
            .get(AggregateSource.SERVER)
            .onSuccessTask { snapshot -> Tasks.forResult(snapshot.count) }

    fun getPendingClaims(
        onSuccess: (List<AdminPendingClaim>) -> Unit,
        onError: (String) -> Unit
    ) {
        val pending = firestore.collection("claims")
            .whereEqualTo("status", "pending")
        pending
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .continueWithTask { task ->
                val failure = task.exception as? FirebaseFirestoreException
                if (failure?.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                    pending.get()
                } else {
                    task
                }
            }
            .onSuccessTask { snapshot ->
                val claims = snapshot.documents.map(::readPendingClaim)
                Tasks.whenAll(claims).onSuccessTask {
                    Tasks.forResult(claims.map { it.result }.sortedByDescending { it.createdAtMillis })
                }
            }
            .addOnSuccessListener { claims -> onSuccess(claims) }
            .addOnFailureListener { exception ->
                onError(exception.cause?.localizedMessage ?: exception.localizedMessage
                    ?: "Unable to load pending claims.")
            }
    }

    private fun readPendingClaim(claim: DocumentSnapshot): Task<AdminPendingClaim> {
        val itemId = claim.getString("foundItemId")
        val studentUid = claim.getString("claimantUid")
        val createdAt = claim.getTimestamp("createdAt")
        if (itemId.isNullOrBlank() || '/' in itemId ||
            studentUid.isNullOrBlank() || '/' in studentUid || createdAt == null
        ) {
            return Tasks.forException(IllegalStateException("Invalid pending claim ${claim.id}."))
        }

        val item = firestore.collection("foundItems").document(itemId).get()
        val student = firestore.collection("users").document(studentUid).get()
        return Tasks.whenAll(item, student).onSuccessTask {
            Tasks.forResult(
                AdminPendingClaim(
                    id = claim.id,
                    itemTitle = item.result.getString("title") ?: "Untitled item",
                    studentName = student.result.getString("displayName") ?: "Unknown student",
                    createdAtMillis = createdAt.toDate().time
                )
            )
        }
    }
}
