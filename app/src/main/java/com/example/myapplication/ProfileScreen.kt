package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileForm(
    state: ProfileUiState,
    viewModel: ProfileViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "My Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.contactNumber,
            onValueChange = { viewModel.onContactChange(it) },
            label = { Text("Contact number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.address,
            onValueChange = { viewModel.onAddressChange(it) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Skills", fontWeight = FontWeight.Bold)

        // Add Skill Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.newSkill,
                onValueChange = { viewModel.onNewSkillChange(it) },
                label = { Text("Add a skill") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.addSkill() }) {
                Text("Add")
            }
        }

        // Skills List
        state.skills.forEach { skill ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "• $skill", modifier = Modifier.weight(1f))
                TextButton(onClick = { viewModel.removeSkill(skill) }) {
                    Text("Remove")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { viewModel.showPreview() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Preview")
        }
    }
}

@Composable
fun ProfilePreview(
    state: ProfileUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Profile Preview",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Name: ${state.name}")
        Text(text = "Email: ${state.email}")
        Text(text = "Contact: ${state.contactNumber}")
        Text(text = "Address: ${state.address}")
        Text(text = "Username: ${state.username}")

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Skills:", fontWeight = FontWeight.Bold)

        if (state.skills.isEmpty()) {
            Text(text = "No skills added yet.")
        } else {
            state.skills.forEach { skill ->
                Text(text = "• $skill")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(onClick = onBack) {
            Text("Back to edit")
        }
    }
}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isPreview) {
        ProfilePreview(
            state = state,
        ) {
            viewModel.backToEdit()
        }
    } else {
        ProfileForm(
            state = state,
            viewModel = viewModel
        )
    }
}