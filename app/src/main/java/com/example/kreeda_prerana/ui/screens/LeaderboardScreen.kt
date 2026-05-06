package com.example.kreeda_prerana.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kreeda_prerana.ui.components.EmptyStateMessage
import com.example.kreeda_prerana.ui.theme.BronzeMedal
import com.example.kreeda_prerana.ui.theme.GoldMedal
import com.example.kreeda_prerana.ui.theme.SilverMedal
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(viewModel: AthleteViewModel) {
    var selectedTestType by remember { mutableStateOf("100m Sprint") }
    var searchQuery by remember { mutableStateOf("") }
    var filterDropdownExpanded by remember { mutableStateOf(false) }

    val trials by viewModel.getTrialsByType(selectedTestType).collectAsState(initial = emptyList())
    val athletes by viewModel.allAthletes.collectAsState(initial = emptyList())

    val testTypeNames = allTestTypes.map { it.name }

    // Group trials by athlete and find best for each
    val isTimeEvent = allTestTypes.find { it.name == selectedTestType }?.category == TestCategory.TIME

    val bestTrials = trials.groupBy { it.athleteId }.mapValues { entry ->
        if (isTimeEvent) {
            entry.value.minByOrNull { it.value }
        } else {
            entry.value.maxByOrNull { it.value }
        }
    }.values.filterNotNull().sortedBy {
        if (isTimeEvent) it.value else -it.value
    }

    // Apply search filter
    val filteredTrials = if (searchQuery.isBlank()) {
        bestTrials
    } else {
        bestTrials.filter { trial ->
            val athlete = athletes.find { it.id == trial.athleteId }
            athlete?.name?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = GoldMedal,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "School ranking • ${filteredTrials.size} athletes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Search bar (left) + Filter dropdown (right) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )

            // Filter dropdown for test type
            ExposedDropdownMenuBox(
                expanded = filterDropdownExpanded,
                onExpandedChange = { filterDropdownExpanded = it },
                modifier = Modifier.width(160.dp)
            ) {
                OutlinedTextField(
                    value = selectedTestType,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )
                ExposedDropdownMenu(
                    expanded = filterDropdownExpanded,
                    onDismissRequest = { filterDropdownExpanded = false }
                ) {
                    testTypeNames.forEach { type ->
                        val info = allTestTypes.find { it.name == type }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(type, modifier = Modifier.weight(1f))
                                    Text(
                                        text = if (info?.category == TestCategory.TIME) "⏱" else "📏",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            onClick = {
                                selectedTestType = type
                                filterDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Rankings ──
        if (filteredTrials.isEmpty()) {
            EmptyStateMessage(
                icon = Icons.Default.Leaderboard,
                title = if (searchQuery.isNotBlank()) "No matching athletes" else "No rankings yet",
                subtitle = if (searchQuery.isNotBlank()) "Try a different name" else "Log trials for \"$selectedTestType\" to see rankings"
            )
        } else {
            LazyColumn {
                itemsIndexed(filteredTrials) { index, trial ->
                    val athlete = athletes.find { it.id == trial.athleteId }
                    LeaderboardItem(
                        rank = index + 1,
                        athleteName = athlete?.name ?: "Unknown",
                        sport = athlete?.primarySport ?: "",
                        score = String.format("%.2f %s", trial.value, trial.unit)
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, athleteName: String, sport: String, score: String) {
    val medalColor = when (rank) {
        1 -> GoldMedal
        2 -> SilverMedal
        3 -> BronzeMedal
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank indicator — only this gets medal color
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = medalColor?.copy(alpha = 0.2f)
                    ?: MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (medalColor != null) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = medalColor,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "#$rank",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = athleteName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (rank <= 3) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = sport,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Score badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = score,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
