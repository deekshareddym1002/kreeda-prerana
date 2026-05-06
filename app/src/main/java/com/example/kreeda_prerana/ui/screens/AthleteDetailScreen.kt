package com.example.kreeda_prerana.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreeda_prerana.data.entity.Athlete
import com.example.kreeda_prerana.data.entity.Trial
import com.example.kreeda_prerana.ui.components.EmptyStateMessage
import com.example.kreeda_prerana.ui.components.SectionHeader
import com.example.kreeda_prerana.ui.components.SportCard
import com.example.kreeda_prerana.ui.theme.BadgeBeginner
import com.example.kreeda_prerana.ui.theme.BadgeDistrictLevel
import com.example.kreeda_prerana.ui.theme.BadgeNationalLevel
import com.example.kreeda_prerana.ui.theme.BadgeNotReady
import com.example.kreeda_prerana.ui.theme.BadgeStateLevel
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteDetailScreen(
    athleteId: Long,
    viewModel: AthleteViewModel,
    onNavigateBack: () -> Unit
) {
    var athlete by remember { mutableStateOf<Athlete?>(null) }
    val trials by viewModel.getTrialsForAthlete(athleteId).collectAsState(initial = emptyList())

    // Discover which test types this athlete has done
    val athleteTestTypes = trials.map { it.testType }.distinct()
    var selectedGraphType by remember { mutableStateOf<String?>(null) }
    var graphDropdownExpanded by remember { mutableStateOf(false) }

    // Auto-select first test type when trials load
    LaunchedEffect(athleteTestTypes) {
        if (selectedGraphType == null && athleteTestTypes.isNotEmpty()) {
            selectedGraphType = athleteTestTypes.first()
        }
    }

    LaunchedEffect(athleteId) { athlete = viewModel.getAthleteById(athleteId) }

    val graphTrials = trials.filter { it.testType == selectedGraphType }.take(10).reversed()
    val graphTestInfo = allTestTypes.find { it.name == selectedGraphType }
    val isTimeGraph = graphTestInfo?.category == TestCategory.TIME

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(athlete?.name ?: "Athlete Detail", fontWeight = FontWeight.SemiBold) },
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
        if (athlete != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)
            ) {
                item {
                    AthleteProfileHeader(athlete!!)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── Talent Curve with event dropdown ──
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SectionHeader(title = "Talent Curve")
                    }
                    // Event selector dropdown for multi-event athletes
                    if (athleteTestTypes.size > 1) {
                        ExposedDropdownMenuBox(
                            expanded = graphDropdownExpanded,
                            onExpandedChange = { graphDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedGraphType ?: "Select event",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Event for graph") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = graphDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = graphDropdownExpanded,
                                onDismissRequest = { graphDropdownExpanded = false }
                            ) {
                                athleteTestTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedGraphType = type
                                            graphDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    TalentCurveGraph(
                        trials = graphTrials,
                        yAxisLabel = if (isTimeGraph) "Time (s)" else "Distance (m)",
                        xAxisLabel = "Trial #",
                        isLowerBetter = isTimeGraph
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Milestone Badges
                item {
                    SectionHeader(title = "Milestone Badges")
                    MilestoneSection(trials)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Performance History
                item {
                    SectionHeader(title = "Performance History")
                    if (trials.isEmpty()) {
                        EmptyStateMessage(
                            icon = Icons.Default.History,
                            title = "No trials recorded",
                            subtitle = "Use the Trial Logger to record performance"
                        )
                    }
                }
                items(trials) { trial -> TrialHistoryItem(trial) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun AthleteProfileHeader(athlete: Athlete) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(athlete.name.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(athlete.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("${athlete.primarySport} • Age ${athlete.age} • ${athlete.gender}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun TalentCurveGraph(
    trials: List<Trial>,
    yAxisLabel: String,
    xAxisLabel: String,
    isLowerBetter: Boolean
) {
    if (trials.isEmpty()) {
        SportCard {
            Box(modifier = Modifier.height(150.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                EmptyStateMessage(icon = Icons.Default.ShowChart, title = "Not enough data",
                    subtitle = "Record trials to see the talent curve")
            }
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 8.dp, bottom = 4.dp)) {
            // Y-axis label at top-left
            Text(
                text = "▲ $yAxisLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp)
            )
            Canvas(modifier = Modifier.height(200.dp).fillMaxWidth().padding(start = 12.dp, end = 8.dp, bottom = 4.dp)) {
                val leftPadding = 50f
                val bottomPadding = 40f
                val graphWidth = size.width - leftPadding
                val graphHeight = size.height - bottomPadding

                val maxVal = trials.maxOf { it.value }.coerceAtLeast(1.0)
                val minVal = trials.minOf { it.value }
                val range = (maxVal - minVal).coerceAtLeast(0.1)

                // Grid lines + Y-axis values
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = graphHeight * i / gridLines
                    drawLine(outlineColor, Offset(leftPadding, y), Offset(size.width, y), 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f)))
                    val labelValue = maxVal - (range * i / gridLines)
                    drawText(textMeasurer, String.format("%.1f", labelValue),
                        Offset(0f, y - 8f), style = TextStyle(fontSize = 9.sp, color = textColor))
                }

                // X-axis trial labels
                trials.forEachIndexed { index, _ ->
                    val x = leftPadding + graphWidth * (index.toFloat() / (trials.size - 1).coerceAtLeast(1))
                    drawText(textMeasurer, "#${index + 1}",
                        Offset(x - 6f, graphHeight + 6f),
                        style = TextStyle(fontSize = 8.sp, color = textColor))
                }

                // Points
                val points = trials.mapIndexed { index, trial ->
                    val x = leftPadding + graphWidth * (index.toFloat() / (trials.size - 1).coerceAtLeast(1))
                    val y = graphHeight - (graphHeight * ((trial.value - minVal) / range).toFloat())
                    Offset(x, y)
                }

                // Gradient fill
                if (points.size >= 2) {
                    val fillPath = Path().apply {
                        moveTo(points[0].x, graphHeight)
                        lineTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
                        lineTo(points.last().x, graphHeight)
                        close()
                    }
                    drawPath(fillPath, Brush.verticalGradient(
                        listOf(primaryColor.copy(alpha = 0.3f), primaryColor.copy(alpha = 0.0f))))
                }

                // Line
                val path = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        points.forEach { lineTo(it.x, it.y) }
                    }
                }
                drawPath(path, primaryColor, style = Stroke(width = 3.dp.toPx()))

                // Dots
                points.forEach { point ->
                    drawCircle(primaryColor, 5.dp.toPx(), point)
                    drawCircle(Color.White, 3.dp.toPx(), point)
                }
            }
            // X-axis label at bottom-right
            Text(
                text = "$xAxisLabel ▶",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End).padding(end = 12.dp, bottom = 4.dp)
            )
        }
    }
}

// ════════════════════════════════════════════════════
//  MILESTONE BADGES — Realistic Indian School Standards
//  References: SAI / Khelo India age-group benchmarks
//  (U-16 / U-18 school-level approximate thresholds)
// ════════════════════════════════════════════════════

data class BadgeInfo(val label: String, val color: Color, val icon: ImageVector)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MilestoneSection(trials: List<Trial>) {
    val badges = mutableListOf<BadgeInfo>()

    // Helper: best value for a test type (lowest for time, highest for distance)
    fun bestForType(keyword: String, isTime: Boolean): Double? {
        val matching = trials.filter { it.testType.contains(keyword, ignoreCase = true) }
        return if (matching.isEmpty()) null
        else if (isTime) matching.minOf { it.value }
        else matching.maxOf { it.value }
    }

    // ── 100m Sprint (time in seconds, lower = better) ──
    bestForType("100m", isTime = true)?.let { best ->
        badges.add(when {
            best < 11.5  -> BadgeInfo("100m National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best < 12.0  -> BadgeInfo("100m State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best < 13.0  -> BadgeInfo("100m District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best < 14.5  -> BadgeInfo("100m Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("100m Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── 200m Sprint ──
    bestForType("200m", isTime = true)?.let { best ->
        badges.add(when {
            best < 23.5  -> BadgeInfo("200m National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best < 25.0  -> BadgeInfo("200m State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best < 27.0  -> BadgeInfo("200m District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best < 30.0  -> BadgeInfo("200m Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("200m Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── 400m Sprint ──
    bestForType("400m", isTime = true)?.let { best ->
        badges.add(when {
            best < 52.0  -> BadgeInfo("400m National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best < 56.0  -> BadgeInfo("400m State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best < 62.0  -> BadgeInfo("400m District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best < 70.0  -> BadgeInfo("400m Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("400m Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── 800m / 1500m Run ──
    bestForType("800m", isTime = true)?.let { best ->
        badges.add(when {
            best < 120.0 -> BadgeInfo("800m State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best < 140.0 -> BadgeInfo("800m District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            else         -> BadgeInfo("800m Beginner", BadgeBeginner, Icons.Default.TrendingUp)
        })
    }

    bestForType("1500m", isTime = true)?.let { best ->
        badges.add(when {
            best < 270.0 -> BadgeInfo("1500m State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best < 320.0 -> BadgeInfo("1500m District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            else         -> BadgeInfo("1500m Beginner", BadgeBeginner, Icons.Default.TrendingUp)
        })
    }

    // ── Long Jump (distance in meters, higher = better) ──
    bestForType("Long Jump", isTime = false)?.let { best ->
        badges.add(when {
            best >= 6.0  -> BadgeInfo("Long Jump National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 5.2  -> BadgeInfo("Long Jump State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 4.5  -> BadgeInfo("Long Jump District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 3.5  -> BadgeInfo("Long Jump Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("Long Jump Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── High Jump ──
    bestForType("High Jump", isTime = false)?.let { best ->
        badges.add(when {
            best >= 1.75 -> BadgeInfo("High Jump National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 1.55 -> BadgeInfo("High Jump State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 1.35 -> BadgeInfo("High Jump District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 1.10 -> BadgeInfo("High Jump Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("High Jump Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── Shot Put (4kg U-16) ──
    bestForType("Shot Put", isTime = false)?.let { best ->
        badges.add(when {
            best >= 14.0 -> BadgeInfo("Shot Put National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 11.0 -> BadgeInfo("Shot Put State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 8.5  -> BadgeInfo("Shot Put District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 6.0  -> BadgeInfo("Shot Put Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("Shot Put Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── Discus Throw ──
    bestForType("Discus", isTime = false)?.let { best ->
        badges.add(when {
            best >= 40.0 -> BadgeInfo("Discus National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 30.0 -> BadgeInfo("Discus State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 22.0 -> BadgeInfo("Discus District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 15.0 -> BadgeInfo("Discus Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("Discus Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── Javelin Throw ──
    bestForType("Javelin", isTime = false)?.let { best ->
        badges.add(when {
            best >= 50.0 -> BadgeInfo("Javelin National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 35.0 -> BadgeInfo("Javelin State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 25.0 -> BadgeInfo("Javelin District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 15.0 -> BadgeInfo("Javelin Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("Javelin Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    // ── Triple Jump ──
    bestForType("Triple Jump", isTime = false)?.let { best ->
        badges.add(when {
            best >= 13.0 -> BadgeInfo("Triple Jump National Prospect", BadgeNationalLevel, Icons.Default.Star)
            best >= 11.0 -> BadgeInfo("Triple Jump State Ready", BadgeStateLevel, Icons.Default.WorkspacePremium)
            best >= 9.0  -> BadgeInfo("Triple Jump District Ready", BadgeDistrictLevel, Icons.Default.MilitaryTech)
            best >= 7.0  -> BadgeInfo("Triple Jump Beginner", BadgeBeginner, Icons.Default.TrendingUp)
            else         -> BadgeInfo("Triple Jump Not Ready", BadgeNotReady, Icons.Default.FitnessCenter)
        })
    }

    if (badges.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SportsScore, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Keep training!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text("Log trials to earn badges across 5 tiers", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    } else {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            badges.forEach { BadgeItem(label = it.label, color = it.color, icon = it.icon) }
        }
    }
}

@Composable
fun BadgeItem(label: String, color: Color, icon: ImageVector) {
    Surface(color = color, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TrialHistoryItem(trial: Trial) {
    SportCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(trial.testType, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(trial.timestamp)),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(String.format("%.2f %s", trial.value, trial.unit),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
