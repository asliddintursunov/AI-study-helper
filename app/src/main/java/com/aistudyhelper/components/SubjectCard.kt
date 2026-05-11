package com.aistudyhelper.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudyhelper.data.SubjectRepository

@Composable
fun SubjectCard(
    subject: String,
    flashcardCount: Int = SubjectRepository.getFlashcards(subject).size,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = MaterialTheme.shapes.medium,
                color = subjectAccent(subject).copy(alpha = 0.14f),
                contentColor = subjectAccent(subject)
            ) {
                Icon(
                    imageVector = subjectIcon(subject),
                    contentDescription = null,
                    modifier = Modifier.padding(11.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$flashcardCount flashcards",
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun subjectIcon(subject: String): ImageVector {
    return when (subject) {
        "Math" -> Icons.Rounded.Calculate
        "Physics" -> Icons.Rounded.Science
        "Programming" -> Icons.Rounded.Code
        "Computer Networks" -> Icons.Rounded.Wifi
        "German" -> Icons.Rounded.Translate
        else -> Icons.AutoMirrored.Rounded.MenuBook
    }
}

private fun subjectAccent(subject: String): Color {
    return when (subject) {
        "Math" -> Color(0xFF2457D6)
        "Physics" -> Color(0xFF008C7A)
        "Programming" -> Color(0xFF7C4DFF)
        "Computer Networks" -> Color(0xFFE38900)
        "German" -> Color(0xFFB2572D)
        else -> Color(0xFF2457D6)
    }
}
