package com.aistudyhelper.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import com.aistudyhelper.components.EmptyState
import com.aistudyhelper.components.InfoPill
import com.aistudyhelper.components.ResultCard
import com.aistudyhelper.components.ScreenHeader
import com.aistudyhelper.models.StudyResult
import com.aistudyhelper.viewmodels.ResultsViewModel

@Composable
fun ResultsScreen() {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: ResultsViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )
    val results by viewModel.results.collectAsState()
    var selectedResult by remember { mutableStateOf<StudyResult?>(null) }

    AppScreen {
        ScreenHeader(
            title = "Results",
            subtitle = "Your saved sessions, scores, and study momentum."
        )

        if (results.isEmpty()) {
            EmptyState(
                title = "No results yet",
                message = "Completed flashcard sessions will appear here.",
                modifier = Modifier.weight(1f)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoPill(
                    text = "${results.size} sessions",
                    icon = Icons.Rounded.Timeline
                )
                InfoPill(
                    text = "${results.map { it.score }.average().toInt()}% avg",
                    icon = Icons.Rounded.BarChart
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(results) { result ->
                    ResultCard(
                        result = result,
                        onClick = { selectedResult = result }
                    )
                }
            }
        }
    }

    selectedResult?.let { result ->
        ResultDetailsDialog(
            result = result,
            onDismiss = { selectedResult = null },
            onDelete = {
                viewModel.deleteResult(result)
                selectedResult = null
            }
        )
    }
}

@Composable
private fun ResultDetailsDialog(
    result: StudyResult,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = result.subject,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Correct answers: ${result.correct}")
                Text("Incorrect answers: ${result.incorrect}")
                Text("Score: ${result.score}%")
                Text("Completed: ${result.completedAt}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
