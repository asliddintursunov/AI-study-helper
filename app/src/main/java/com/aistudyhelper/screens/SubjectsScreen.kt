package com.aistudyhelper.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudyhelper.components.AppScreen
import com.aistudyhelper.components.InfoPill
import com.aistudyhelper.components.ScreenHeader
import com.aistudyhelper.components.SubjectCard
import com.aistudyhelper.data.SubjectRepository
import com.aistudyhelper.viewmodels.SubjectsViewModel

@Composable
fun SubjectsScreen(
    onSubjectClick: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: SubjectsViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    AppScreen {
        ScreenHeader(
            title = "Subjects",
            subtitle = "Pick a deck and build recall one card at a time.",
            action = {
                IconButton(
                    onClick = { showCreateDialog = true },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Create topic")
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoPill(
                text = "${uiState.totalSubjects} subjects",
                icon = Icons.Rounded.School
            )
            InfoPill(
                text = "${uiState.totalFlashcards} cards",
                icon = Icons.Rounded.AutoStories
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    flashcardCount = uiState.flashcardCounts.getOrDefault(subject, 0),
                    onClick = { onSubjectClick(subject) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateTopicDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { topic, question, answer ->
                viewModel.addTopicWithFirstFlashcard(
                    topic = topic,
                    question = question,
                    answer = answer,
                    onCreated = { subject ->
                        showCreateDialog = false
                        onSubjectClick(subject)
                    }
                )
            }
        )
    }
}

@Composable
private fun CreateTopicDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    val canCreate = topic.isNotBlank() && question.isNotBlank() && answer.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New topic",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("First question") },
                    minLines = 2,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    minLines = 2,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(topic, question, answer) },
                enabled = canCreate
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
