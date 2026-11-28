package com.CampusFind.com.data

import com.CampusFind.com.model.AdminClaimCase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.functions.FirebaseFunctions

class AdminClaimReviewRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val functions = FirebaseFunctions.getInstance("us-east1")

    private fun read(collection: String, id: String) =
        db.collection(collection).document(id).get(Source.SERVER)

    private fun verifyAdmin(
        success: (String) -> Unit,
        error: (String) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null || !user.isEmailVerified ||
            !user.email.orEmpty().endsWith(
                "@uniandes.edu.co", ignoreCase = true
            )
        ) {
            error("Verified administrator access is required.")
            return
        }

        read("users", user.uid)
            .addOnSuccessListener { profile ->
                if (profile.getString("role") == "admin" &&
                    auth.currentUser?.uid == user.uid
                ) {
                    success(user.uid)
                } else {
                    error("Administrator access is required.")
                }
            }
            .addOnFailureListener {
                error(it.localizedMessage ?: "Unable to verify admin.")
            }
    }

    fun loadClaims(
        reportId: String?,
        foundItemId: String?,
        success: (List<AdminClaimCase>) -> Unit,
        error: (String) -> Unit
    ) {
        val report = reportId?.takeIf { it.isNotBlank() }
        val item = foundItemId?.takeIf { it.isNotBlank() }

        if ((report == null && item == null) ||
            report?.contains("/") == true ||
            item?.contains("/") == true
        ) {
            error("A valid report or item ID is required.")
            return
        }

        verifyAdmin({ uid ->
            val query = if (report != null) {
                db.collection("claims").whereEqualTo("reportId", report)
            } else {
                db.collection("claims").whereEqualTo("foundItemId", item)
            }

            query.get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    val claims = snapshot.documents.filter { claim ->
                        claim.getString("status") in
                                listOf("pending", "approved", "delivered") &&
                                listOf(
                                    "reportId", "foundItemId", "claimantUid"
                                ).all { field ->
                                    val value = claim.getString(field)
                                    !value.isNullOrBlank() && !value.contains("/")
                                } &&
                                (item == null ||
                                        claim.getString("foundItemId") == item)
                    }

                    if (claims.isEmpty()) {
                        if (auth.currentUser?.uid == uid) success(emptyList())
                        return@addOnSuccessListener
                    }

                    val tasks = claims.flatMap { claim ->
                        val r = claim.getString("reportId")!!
                        val f = claim.getString("foundItemId")!!
                        val u = claim.getString("claimantUid")!!

                        listOf(
                            read("lostReports", r),
                            read("foundItems", f),
                            read("users", u),
                            read("claimPrivate", claim.id),
                            read("foundItemPrivate", f),
                            read("lostReportPrivate", r)
                        )
                    }

                    Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                        .addOnSuccessListener { documents ->
                            if (auth.currentUser?.uid != uid) {
                                error("Administrator session changed.")
                                return@addOnSuccessListener
                            }

                            val result = claims.mapIndexedNotNull { i, claim ->
                                buildCase(
                                    claim,
                                    documents.subList(i * 6, i * 6 + 6)
                                )
                            }

                            success(result)
                        }
                        .addOnFailureListener {
                            error(it.localizedMessage ?: "Unable to load details.")
                        }
                }
                .addOnFailureListener {
                    error(it.localizedMessage ?: "Unable to load claims.")
                }
        }, error)
    }

    private fun buildCase(
        claim: DocumentSnapshot,
        docs: List<DocumentSnapshot>
    ): AdminClaimCase? {
        val report = docs[0]
        val item = docs[1]
        val user = docs[2]
        val status = claim.getString("status") ?: ""

        val valid = if (status == "delivered") {
            report.getString("status") == "claimed" &&
                    item.getString("status") == "claimed"
        } else {
            report.getString("status") !in listOf("claimed", "closed") &&
                    item.getString("status") == "available"
        }

        if (!report.exists() || !item.exists() || !valid ||
            report.getString("ownerUid") != claim.getString("claimantUid")
        ) return null

        return AdminClaimCase(
            id = claim.id,
            reportId = claim.getString("reportId")!!,
            foundItemId = claim.getString("foundItemId")!!,
            status = status,
            reportTitle = report.getString("title") ?: "Lost report",
            itemTitle = item.getString("title") ?: "Found item",
            claimantName = user.getString("displayName")
                ?: user.getString("name") ?: "Student",
            claimantEmail = user.getString("email") ?: "Email unavailable",
            ownershipAnswer = docs[3].getString("ownershipAnswer"),
            foundPrivateDetail = docs[4].getString("privateCharacteristics"),
            reportPrivateDetail = docs[5].getString("privateVerificationDetail")
        )
    }
    private fun action(
        name: String,
        data: Map<String, Any>,
        success: () -> Unit,
        error: (String) -> Unit
    ) {
        functions.getHttpsCallable(name).call(data)
            .addOnSuccessListener { response ->
                val changed =
                    (response.data as? Map<*, *>)?.get("changed") == true

                if (changed) {
                    success()
                } else {
                    error("No changes made. Refresh the information.")
                }
            }
            .addOnFailureListener {
                error(it.localizedMessage ?: "Action failed.")
            }
    }

    fun review(
        claim: AdminClaimCase,
        approve: Boolean,
        verified: Boolean,
        success: () -> Unit,
        error: (String) -> Unit
    ) = action(
        "adminReviewClaim",
        mapOf(
            "claimId" to claim.id,
            "reportId" to claim.reportId,
            "foundItemId" to claim.foundItemId,
            "decision" to if (approve) "approved" else "rejected",
            "ownershipVerified" to verified
        ),
        success, error
    )

    fun deliver(
        claim: AdminClaimCase,
        success: () -> Unit,
        error: (String) -> Unit
    ) = action(
        "adminDeliverClaim",
        mapOf(
            "claimId" to claim.id,
            "reportId" to claim.reportId,
            "foundItemId" to claim.foundItemId,
            "ownershipVerified" to true,
            "physicalIdVerified" to true
        ),
        success, error
    )

    fun close(
        reportId: String,
        itemId: String,
        success: () -> Unit,
        error: (String) -> Unit
    ) = action(
        "adminCloseReport",
        mapOf(
            "reportId" to reportId,
            "foundItemId" to itemId
        ),
        success, error
    )
}

