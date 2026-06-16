package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.AiViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send

@Composable
fun AiAssistantScreen(aiViewModel: AiViewModel) {
    val aiResponse by aiViewModel.aiResponse.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()
    var promptText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Gemini Tailoring Assistant",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Ask for style recommendations or fabric estimates.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                } else if (aiResponse.isNotEmpty()) {
                    Text(text = aiResponse, style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text("How can I help you package today's order or style a suit?", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Gemini...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (promptText.isNotBlank()) {
                        aiViewModel.askGemini(promptText)
                        promptText = ""
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.ime))
    }
}
