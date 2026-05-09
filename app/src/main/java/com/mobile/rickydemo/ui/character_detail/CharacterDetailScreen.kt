package com.mobile.rickydemo.ui.character_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.domain.model.Episode
import com.mobile.rickydemo.ui.character_list.components.getStatusColor
import com.mobile.rickydemo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    onBackClick: () -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rick and Morty", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            when (val currentState = state) {
                is CharacterDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RickGreen)
                }
                is CharacterDetailState.Success -> {
                    CharacterDetailContent(
                        character = currentState.character,
                        episodes = currentState.episodes,
                        padding = padding
                    )
                }
                is CharacterDetailState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterDetailContent(
    character: Character,
    episodes: List<Episode>,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header with image and status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BackgroundLight.copy(alpha = 0.9f)),
                            startY = 600f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(getStatusColor(character.status), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${character.status.uppercase()} • ${character.species.uppercase()}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
        
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Character Dossier Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Character Dossier",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DossierItem(label = "SPECIES", value = character.species)
                    DossierItem(label = "GENDER", value = character.gender)
                    DossierItem(label = "ORIGIN", value = character.origin, icon = Icons.Default.Public)
                    DossierItem(label = "LAST KNOWN LOCATION", value = character.location, icon = Icons.Default.Place)
                    
                    if (character.type.isNotEmpty()) {
                        DossierItem(label = "TYPE", value = character.type)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unmatched Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = RickGreen
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Icon(Icons.Outlined.Security, contentDescription = null, tint = Color.Black.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unmatched Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Currently verified as active across all tracked timelines.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(RickGreenDark, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ACTIVE IN SYSTEM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Episodes Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "First Appeared Episodes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The chronological debut of this character in the multiverse.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                TextButton(onClick = { /* TODO */ }) {
                    Text("VIEW ALL", color = RickGreenDark, fontWeight = FontWeight.Bold)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = RickGreenDark)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            episodes.take(3).forEach { episode ->
                EpisodeCard(episode = episode)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Location
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = RickGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${character.location.uppercase()} • SECTOR 7-G",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DossierItem(label: String, value: String, icon: ImageVector? = null) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = RickGreen)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun EpisodeCard(episode: Episode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFF0F5F1),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = episode.episode.take(3), // e.g. S01
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${episode.episode} • ${episode.name.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = episode.airDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
