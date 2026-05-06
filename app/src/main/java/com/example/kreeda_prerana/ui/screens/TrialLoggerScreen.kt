package com.example.kreeda_prerana.ui.screens

import android.os.SystemClock
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreeda_prerana.ui.components.EmptyStateMessage
import com.example.kreeda_prerana.ui.components.SectionHeader
import com.example.kreeda_prerana.ui.components.SportButton
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel
import kotlinx.coroutines.delay

// ──────── Test type classification ────────
// Time-based events: only stopwatch, no manual distance
// Distance-based events: only manual entry, no stopwatch
enum class TestCategory { TIME, DISTANCE }

data class TestTypeInfo(
    val name: String,
    val category: TestCategory,
    val defaultUnit: String,
    val availableUnits: List<String>
)

val allTestTypes = listOf(
    TestTypeInfo("100m Sprint", TestCategory.TIME, "s", listOf("s")),
    TestTypeInfo("200m Sprint", TestCategory.TIME, "s", listOf("s")),
    TestTypeInfo("400m Sprint", TestCategory.TIME, "s", listOf("s")),
    TestTypeInfo("800m Run", TestCategory.TIME, "s", listOf("s", "min")),
    TestTypeInfo("1500m Run", TestCategory.TIME, "s", listOf("s", "min")),
    TestTypeInfo("Long Jump", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
    TestTypeInfo("High Jump", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
    TestTypeInfo("Triple Jump", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
    TestTypeInfo("Shot Put", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
    TestTypeInfo("Discus Throw", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
    TestTypeInfo("Javelin Throw", TestCategory.DISTANCE, "m", listOf("m", "cm", "ft")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialLoggerScreen(
    viewModel: AthleteViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedAthleteId by remember { mutableStateOf<Long?>(null) }
    val athletes by viewModel.allAthletes.collectAsState(initial = emptyList())

    var selectedTestInfo by remember { mutableStateOf(allTestTypes[0]) }
    var selectedUnit by remember { mutableStateOf(allTestTypes[0].defaultUnit) }

    // Dropdown expanded states
    var athleteDropdownExpanded by remember { mutableStateOf(false) }
    var testTypeDropdownExpanded by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    // Timer State
    var timeElapsed by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }

    // Distance State
    var distanceInput by remember { mutableStateOf("") }

    val isTimerMode = selectedTestInfo.category == TestCategory.TIME

    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = SystemClock.elapsedRealtime() - timeElapsed
            while (isRunning) {
                timeElapsed = SystemClock.elapsedRealtime() - startTime
                delay(10)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trial Logger",
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Athlete Selector Dropdown ──
            SectionHeader(title = "Select Athlete")

            if (athletes.isEmpty()) {
                EmptyStateMessage(
                    icon = Icons.Default.Person,
                    title = "No athletes registered",
                    subtitle = "Add athletes first to start logging trials"
                )
            } else {
                val selectedAthlete = athletes.find { it.id == selectedAthleteId }
                ExposedDropdownMenuBox(
                    expanded = athleteDropdownExpanded,
                    onExpandedChange = { athleteDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedAthlete?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Athlete") },
                        placeholder = { Text("Choose an athlete") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = athleteDropdownExpanded) },
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
                        expanded = athleteDropdownExpanded,
                        onDismissRequest = { athleteDropdownExpanded = false }
                    ) {
                        athletes.forEach { athlete ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(athlete.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${athlete.primarySport} • Age ${athlete.age}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedAthleteId = athlete.id
                                    athleteDropdownExpanded = false
                                }
                            )
                        }
                    }
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
                                selectedUnit = info.defaultUnit
                                // Reset timer / input on switch
                                isRunning = false
                                timeElapsed = 0L
                                distanceInput = ""
                                testTypeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Category indicator
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTimerMode)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = if (isTimerMode)
                        "⏱  Time-based event — use the stopwatch below"
                    else
                        "📏  Distance-based event — enter measurement below",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTimerMode)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── INPUT SECTION (auto-selected based on test type) ──
            if (isTimerMode) {
                // ── STOPWATCH ──
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimerDisplay(timeElapsed)
                        Text(
                            text = selectedTestInfo.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledTonalButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isRunning)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isRunning)
                                MaterialTheme.colorScheme.onErrorContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRunning) "Stop" else "Start",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            isRunning = false
                            timeElapsed = 0L
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset")
                    }
                }
            } else {
                // ── DISTANCE / MANUAL ENTRY ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Value input
                    OutlinedTextField(
                        value = distanceInput,
                        onValueChange = { distanceInput = it },
                        label = { Text("Value") },
                        placeholder = { Text("e.g., 5.45") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        )
                    )

                    // Unit dropdown
                    ExposedDropdownMenuBox(
                        expanded = unitDropdownExpanded,
                        onExpandedChange = { unitDropdownExpanded = it },
                        modifier = Modifier.width(120.dp)
                    ) {
                        OutlinedTextField(
                            value = when (selectedUnit) {
                                "m" -> "Meters (m)"
                                "cm" -> "cm"
                                "ft" -> "Feet (ft)"
                                "s" -> "Seconds (s)"
                                "min" -> "Minutes"
                                else -> selectedUnit
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = unitDropdownExpanded,
                            onDismissRequest = { unitDropdownExpanded = false }
                        ) {
                            selectedTestInfo.availableUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (unit) {
                                                "m" -> "Meters (m)"
                                                "cm" -> "Centimeters (cm)"
                                                "ft" -> "Feet (ft)"
                                                "s" -> "Seconds (s)"
                                                "min" -> "Minutes (min)"
                                                else -> unit
                                            }
                                        )
                                    },
                                    onClick = {
                                        selectedUnit = unit
                                        unitDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SportButton(
                text = "Save Trial",
                onClick = {
                    val athleteId = selectedAthleteId ?: return@SportButton
                    val rawValue: Double
                    val saveUnit: String

                    if (isTimerMode) {
                        // Round to 2 decimal places
                        rawValue = Math.round(timeElapsed / 10.0) / 100.0
                        saveUnit = "s"
                    } else {
                        val inputVal = distanceInput.toDoubleOrNull() ?: return@SportButton
                        // Convert to standard unit (meters) for consistent storage
                        rawValue = when (selectedUnit) {
                            "cm" -> inputVal / 100.0
                            "ft" -> inputVal * 0.3048
                            else -> inputVal
                        }
                        saveUnit = "m"
                    }
                    viewModel.addTrial(athleteId, selectedTestInfo.name, rawValue, saveUnit)
                    onNavigateBack()
                },
                enabled = selectedAthleteId != null && (isTimerMode || distanceInput.isNotBlank())
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimerDisplay(timeInMillis: Long) {
    val seconds = timeInMillis / 1000
    val millis = (timeInMillis % 1000) / 10
    Text(
        text = String.format("%02d.%02d", seconds, millis),
        fontSize = 72.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        letterSpacing = 4.sp
    )
}
