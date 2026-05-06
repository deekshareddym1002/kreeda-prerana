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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kreeda_prerana.data.entity.Trial
import com.example.kreeda_prerana.ui.components.EmptyStateMessage
import com.example.kreeda_prerana.ui.components.SectionHeader
import com.example.kreeda_prerana.ui.components.SportButton
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchEntryScreen(
    viewModel: AthleteViewModel,
    onNavigateBack: () -> Unit
) {
    val athletes by viewModel.allAthletes.collectAsState(initial = emptyList())
    var selectedTestInfo by remember { mutableStateOf(allTestTypes[0]) }
    var testTypeDropdownExpanded by remember { mutableStateOf(false) }
    val entryValues = remember { mutableStateMapOf<Long, String>() }

    val filledCount = entryValues.values.count { it.isNotBlank() && it.toDoubleOrNull() != null }
    val isTimeEvent = selectedTestInfo.category == TestCategory.TIME
    val unitLabel = if (isTimeEvent) "seconds (s)" else "meters (m)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Batch Entry",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Enter data for up to ${athletes.size} athletes at once. $filledCount entries filled.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Test Type Dropdown ──
            SectionHeader(title = "Test Type")
            ExposedDropdownMenuBox(
                expanded = testTypeDropdownExpanded,
                onExpandedChange = { testTypeDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedTestInfo.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Event") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = testTypeDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )
                ExposedDropdownMenu(
                    expanded = testTypeDropdownExpanded,
                    onDismissRequest = { testTypeDropdownExpanded = false }
                ) {
                    allTestTypes.forEach { info ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(info.name, modifier = Modifier.weight(1f))
                                    Text(
                                        text = if (info.category == TestCategory.TIME) "⏱ Time" else "📏 Distance",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                selectedTestInfo = info
                                entryValues.clear()
                                testTypeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Unit indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTimeEvent)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Unit: $unitLabel  •  Enter values in the fields below",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTimeEvent)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (athletes.isEmpty()) {
                EmptyStateMessage(
                    icon = Icons.Default.Groups,
                    title = "No athletes to enter",
                    subtitle = "Add athletes first to use batch entry"
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(athletes) { athlete ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = athlete.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = athlete.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = athlete.primarySport,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedTextField(
                                    value = entryValues[athlete.id] ?: "",
                                    onValueChange = { entryValues[athlete.id] = it },
                                    modifier = Modifier.width(110.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = {
                                        Text(
                                            if (isTimeEvent) "sec" else "m",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SportButton(
                text = "Save All Entries ($filledCount)",
                onClick = {
                    val trials = entryValues.mapNotNull { (id, value) ->
                        val doubleVal = value.toDoubleOrNull() ?: return@mapNotNull null
                        Trial(
                            athleteId = id,
                            testType = selectedTestInfo.name,
                            value = doubleVal,
                            unit = if (isTimeEvent) "s" else "m"
                        )
                    }
                    viewModel.addBatchTrials(trials)
                    onNavigateBack()
                },
                enabled = filledCount > 0
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
