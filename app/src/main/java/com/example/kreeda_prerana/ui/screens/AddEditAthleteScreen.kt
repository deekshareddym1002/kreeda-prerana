package com.example.kreeda_prerana.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kreeda_prerana.ui.components.SectionHeader
import com.example.kreeda_prerana.ui.components.SportButton
import com.example.kreeda_prerana.ui.components.SportTextField
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditAthleteScreen(
    viewModel: AthleteViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Not Specified") }
    val genderOptions = listOf("Male", "Female", "Not Specified")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Athlete",
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
            SportTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name"
            )
            SportTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                label = "Age"
            )

            // Primary Sport
            SportTextField(
                value = sport,
                onValueChange = { sport = it },
                label = "Primary Sport",
                placeholder = "e.g., Athletics, Swimming"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gender selection
            SectionHeader(title = "Gender")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                genderOptions.forEach { option ->
                    FilterChip(
                        selected = gender == option,
                        onClick = { gender = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            SportButton(
                text = "Save Athlete",
                onClick = {
                    if (name.isNotBlank() && age.isNotBlank() && sport.isNotBlank()) {
                        viewModel.addAthlete(name, age.toInt(), sport, gender)
                        onNavigateBack()
                    }
                },
                enabled = name.isNotBlank() && age.isNotBlank() && sport.isNotBlank()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
