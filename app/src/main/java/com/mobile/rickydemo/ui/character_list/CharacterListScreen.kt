package com.mobile.rickydemo.ui.character_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.rickydemo.domain.model.Character
import com.mobile.rickydemo.ui.character_list.components.CharacterItem
import com.mobile.rickydemo.ui.character_list.components.ShimmerCharacterItem
import com.mobile.rickydemo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    onCharacterClick: (Character) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val speciesFilter by viewModel.speciesFilter.collectAsState()
    
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val loadState = characters.loadState
    val refreshState = loadState.refresh
    val isOffline = refreshState is LoadState.Error

    // Track if we've ever successfully seen a loading state to distinguish from initial idle
    var hasAttemptedLoad by remember { mutableStateOf(false) }
    if (refreshState is LoadState.Loading || refreshState is LoadState.Error) {
        hasAttemptedLoad = true
    }

    Scaffold(
        topBar = {
            Surface(
                color = SurfaceLight,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundLight,
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            placeholder = { Text("Search characters...", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF424242),
                                unfocusedTextColor = Color(0xFF424242),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = TextSecondary
                        )
                    }

                    IconButton(onClick = { characters.refresh() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(horizontal = 10.dp)
        ) {
            if (isOffline && characters.itemCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                OfflineBanner()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Show shimmers if searching (wait/debounce) OR if pager is loading the first page
                    if (isSearching || refreshState is LoadState.Loading) {
                        items(10) {
                            ShimmerCharacterItem()
                        }
                    } else {
                        items(characters.itemCount) { index ->
                            characters[index]?.let { character ->
                                CharacterItem(
                                    character = character,
                                    onClick = { onCharacterClick(character) }
                                )
                            }
                        }

                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item { LoadingIndicator() }
                            }
                            is LoadState.Error -> {
                                item { ErrorRetryItem(onRetry = { characters.retry() }) }
                            }
                            else -> {}
                        }
                    }
                }
                
                // Refined Empty State Handling
                val isFinishedLoading = refreshState is LoadState.NotLoading && 
                                      loadState.append.endOfPaginationReached
                
                if (characters.itemCount == 0 && isFinishedLoading && !isSearching && hasAttemptedLoad) {
                    NoResultsScreen(modifier = Modifier.fillMaxSize())
                }

                // Handling Total initial connection failure
                if (characters.itemCount == 0 && refreshState is LoadState.Error) {
                    ErrorScreen(
                        onRetry = { characters.retry() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceLight
        ) {
            FilterBottomSheetContent(
                statusFilter = statusFilter,
                speciesFilter = speciesFilter,
                onStatusChange = viewModel::onStatusFilterChange,
                onSpeciesChange = viewModel::onSpeciesFilterChange,
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}

@Composable
fun NoResultsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(BackgroundLight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No characters found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We couldn't find any characters matching your search query or filters.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    statusFilter: String,
    speciesFilter: String,
    onStatusChange: (String) -> Unit,
    onSpeciesChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Filters",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Status", 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RickFilterChip(selected = statusFilter == "", label = "All", onClick = { onStatusChange("") })
            listOf("Alive", "Dead", "Unknown").forEach { status ->
                RickFilterChip(
                    selected = statusFilter == status.lowercase(),
                    label = status,
                    onClick = { onStatusChange(status.lowercase()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Species", 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RickFilterChip(selected = speciesFilter == "", label = "All", onClick = { onSpeciesChange("") })
            listOf("Human", "Alien", "Robot", "Cronenberg", "Animal").forEach { species ->
                RickFilterChip(
                    selected = speciesFilter == species.lowercase(),
                    label = species,
                    onClick = { onSpeciesChange(species.lowercase()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RickGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Apply Filters", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun RickFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) RickGreen else Color.Transparent,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun OfflineBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceLight,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = TextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "You're offline — showing cached data",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ErrorScreen(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(BackgroundLight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            color = Color(0xFFF0F0F0)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Network failure", 
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The interdimensional gateway is currently unstable. We couldn't fetch the character data. Please check your connection and try again.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = RickGreen),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry Connection", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp), color = RickGreen)
    }
}

@Composable
fun ErrorRetryItem(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Failed to load more", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = RickGreen)) { 
            Text("Retry")
        }
    }
}
