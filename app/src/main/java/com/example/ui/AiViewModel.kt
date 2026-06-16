package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.BuildConfig

class AiViewModel : ViewModel() {
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun askGemini(prompt: String) {
        if (BuildConfig.GEMINI_API_KEY.isEmpty() || BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY") {
             _aiResponse.value = "Error: Please set your Gemini API Key in the AI Studio Secrets panel."
             return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _aiResponse.value = ""
            try {
                val fullSystemPrompt = "You are a professional tailoring assistant for Mehedi Tailors and Fabrics. Guide customers on styles, measurements, and fabric choices. Keep answers concise, professional and helpful. User asks: $prompt"
                val response = generativeModel.generateContent(fullSystemPrompt)
                _aiResponse.value = response.text ?: "No response from AI."
            } catch (e: Exception) {
                _aiResponse.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
