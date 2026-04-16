package com.example.logistic_app.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logistic_app.data.model.EmergencyLocation
import com.example.logistic_app.data.model.EmergencyReport
import com.example.logistic_app.utils.CloudinaryHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class EmergencyStep { SELECTION, DETAILS, SUCCESS }

class EmergencyViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var currentStep by mutableStateOf(EmergencyStep.SELECTION)
        private set

    var selectedType by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var selectedImageUri by mutableStateOf<Uri?>(null)
    
    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Location state - using 0.0 as sentinel to detect if location was ever set
    var selectedLat by mutableDoubleStateOf(0.0)
    var selectedLng by mutableDoubleStateOf(0.0)
    var selectedLabel by mutableStateOf("Detecting Location...")
    
    // Live location from map to be used if no manual pin exists
    private var liveLat by mutableDoubleStateOf(0.0)
    private var liveLng by mutableDoubleStateOf(0.0)

    fun onTypeSelected(type: String) {
        selectedType = type
        currentStep = EmergencyStep.DETAILS
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
    }

    /**
     * Updates the "Live" location coming from the GPS loop in the Map component.
     * This ensures we always have the latest coordinates even if the user hasn't pinned anything.
     */
    fun updateLiveLocation(lat: Double, lng: Double) {
        liveLat = lat
        liveLng = lng
        
        // If the user hasn't manually pinned a location yet, update the selected location automatically
        if (selectedLat == 0.0) {
            selectedLat = lat
            selectedLng = lng
            selectedLabel = "Auto-Detected Location"
        }
    }

    /**
     * Called when the user manually long-presses or clicks the map to set a specific pinpoint.
     */
    fun onLocationConfirmed(lat: Double, lng: Double, label: String) {
        selectedLat = lat
        selectedLng = lng
        selectedLabel = label
    }

    fun onBack() {
        if (currentStep == EmergencyStep.DETAILS) {
            currentStep = EmergencyStep.SELECTION
            error = null
        }
    }

    fun transmitEmergency(dispatchId: String, userId: String, userName: String) {
        // Validation logic
        if (selectedType != "T.I.C.") {
            if (description.isBlank()) {
                error = "Description is required for this emergency type."
                return
            }
            if (selectedImageUri == null) {
                error = "Photo evidence is required for this emergency type."
                return
            }
        }

        // Final safety check: if selectedLat is still 0.0 but we have live data, use live data
        if (selectedLat == 0.0 && liveLat != 0.0) {
            selectedLat = liveLat
            selectedLng = liveLng
            selectedLabel = "Detected Location"
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                // 1. Upload Image if exists
                var uploadedImageUrl = ""
                selectedImageUri?.let {
                    uploadedImageUrl = CloudinaryHelper.uploadImage(it)
                }

                val report = EmergencyReport(
                    dispatchId = dispatchId,
                    type = selectedType,
                    description = description,
                    imageUrl = uploadedImageUrl,
                    location = EmergencyLocation(selectedLat, selectedLng, selectedLabel),
                    reportedBy = userName,
                    timestamp = Timestamp.now()
                )

                // 3. Save to EmergencyReports collection
                db.collection("EmergencyReports").add(report).await()

                // 4. Send summary to Support Chat
                val summaryText = """
                    🚨 EMERGENCY REPORTED: $selectedType
                    Location: $selectedLabel ($selectedLat, $selectedLng)
                    Description: ${if (description.isBlank()) "No details provided." else description}
                """.trimIndent()

                val chatMessageData = hashMapOf(
                    "senderId" to userId,
                    "senderName" to userName,
                    "text" to summaryText,
                    "imageUrl" to uploadedImageUrl, // Attach the same image to the chat
                    "timestamp" to FieldValue.serverTimestamp(),
                    "isAdmin" to false
                )

                db.collection("dispatches")
                    .document(dispatchId)
                    .collection("messages")
                    .add(chatMessageData)
                    .await()

                currentStep = EmergencyStep.SUCCESS
            } catch (e: Exception) {
                Log.e("EmergencyViewModel", "Failed to transmit emergency", e)
                error = "Transmission failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun reset() {
        currentStep = EmergencyStep.SELECTION
        selectedType = ""
        description = ""
        selectedImageUri = null
        error = null
        isLoading = false
        // Reset to sentinel 0.0 to trigger auto-detection on next open
        selectedLat = 0.0
        selectedLng = 0.0
        selectedLabel = "Detecting Location..."
    }
}
