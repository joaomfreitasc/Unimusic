package com.example.unimusicapp

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.util.Calendar

val DeepBlack = Color(0xFF121212)
val AlmostBlack = Color(0xFF1E1E1E)
val SurfaceGray = Color(0xFF2A2A2A)
val PrimaryGreen = Color(0xFF1DB954)
val SecondaryPurple = Color(0xFFBB86FC)
val WhiteTransparent = Color(0x99FFFFFF)

val AppGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF330033), DeepBlack),
    startY = 0f,
    endY = 800f
)

@Composable
fun UnimusicApp(viewModel: UnimusicViewModel = viewModel()) {
    val user by viewModel.currentUser.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initPlayer(context)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppGradient)
        ) {
            Crossfade(targetState = user != null, label = "AuthTransition") { isLoggedIn ->
                if (isLoggedIn) {
                    MainAppScaffold(viewModel)
                } else {
                    AuthScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(viewModel: UnimusicViewModel) {
    var isRegistering by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = UnimusicIcon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(size = 128.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Unimusic",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "Sua música, sua vibe :)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(Modifier.height(40.dp))

            StyledTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuário",
                icon = Icons.Default.Person
            )

            if (isRegistering) {
                Spacer(Modifier.height(16.dp))
                StyledTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
            }

            Spacer(Modifier.height(16.dp))
            StyledTextField(
                value = password,
                onValueChange = { password = it },
                label = "Senha",
                icon = Icons.Default.Lock,
                isPassword = true,
                onDone = {
                    keyboardController?.hide()
                    if (isRegistering) viewModel.register(username, email, password)
                    else viewModel.login(username, password)
                }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    if (isRegistering) viewModel.register(username, email, password)
                    else viewModel.login(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isRegistering) "Cadastre-se" else "Entrar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    text = if (isRegistering) "Já tem uma conta? Entrar" else "Não tem uma conta? Cadastre-se",
                    color = PrimaryGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Color.Gray) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (onDone != null) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = SurfaceGray,
            focusedContainerColor = SurfaceGray.copy(alpha = 0.3f),
            unfocusedContainerColor = SurfaceGray.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: UnimusicViewModel) {
    var currentScreen by remember { mutableIntStateOf(0) }
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    var showFullPlayer by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column {
                if (currentSong != null) {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        coverUrl = viewModel.getCoverUrl(currentSong),
                        onTogglePlay = { viewModel.togglePlayPause() },
                        onClick = { showFullPlayer = true },
                        onNext = { viewModel.playNext() }
                    )
                }

                NavigationBar(
                    containerColor = AlmostBlack.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") },
                        selected = currentScreen == 0,
                        onClick = { currentScreen = 0 },  // TODO: Translate "Home" if needed, but it's a common term
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            indicatorColor = PrimaryGreen.copy(alpha = 0.2f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryMusic, null) },
                        label = { Text("Playlists") },
                        selected = currentScreen == 1,
                        onClick = { currentScreen = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            indicatorColor = PrimaryGreen.copy(alpha = 0.2f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Logout, null) },
                        label = { Text("Sair") },
                        selected = false,
                        onClick = { viewModel.logout() },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Crossfade(targetState = currentScreen, label = "ScreenFade") { screen ->
                when (screen) {
                    0 -> MusicListScreen(viewModel)
                    1 -> PlaylistListScreen(viewModel)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showFullPlayer,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        currentSong?.let { song ->
            FullScreenPlayer(
                song = song,
                isPlaying = isPlaying,
                coverUrl = viewModel.getCoverUrl(song),
                onDismiss = { showFullPlayer = false },
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.playNext() },
                onPrev = { viewModel.playPrevious() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(viewModel: UnimusicViewModel) {
    val musicas by viewModel.musicas.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showAddToPlaylistSheet by remember { mutableStateOf<MusicaDTO?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredMusicas = remember(musicas, searchText) {
        if (searchText.isBlank()) musicas
        else musicas.filter {
            it.titulo.contains(searchText, ignoreCase = true) ||
                    it.getSafeArtist().contains(searchText, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            GreetingHeader()
            Spacer(Modifier.height(16.dp))
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = {},
                active = false,
                onActiveChange = {},
                placeholder = { Text("Busque músicas, artistas...") }, // Already in Portuguese
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = SearchBarDefaults.colors(containerColor = SurfaceGray)
            ) {}
        }

        if (isLoading && musicas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        "Todas as músicas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(filteredMusicas) { musica ->
                    MusicListItem(
                        song = musica,
                        coverUrl = viewModel.getCoverUrl(musica),
                        onClick = { viewModel.playSong(musica) },
                        onLongClick = { showAddToPlaylistSheet = musica }
                    )
                }
                if (filteredMusicas.isEmpty() && !isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(50.dp), contentAlignment = Alignment.Center) {
                            Text("Nenhuma música encontrada.", color = Color.Gray) // Already in Portuguese
                        }
                    }
                }
            }
        }
    }

    if (showAddToPlaylistSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddToPlaylistSheet = null },
            containerColor = AlmostBlack,
            contentColor = Color.White
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Adicionar na playlist", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) // Already in Portuguese
                Spacer(Modifier.height(16.dp))

                if (playlists.isEmpty()) {
                    Text("Nenhuma playlist criada.", color = Color.Gray, modifier = Modifier.padding(8.dp)) // Already in Portuguese
                    Button(
                        onClick = { showAddToPlaylistSheet = null },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceGray)
                    ) { Text("Fechar") }
                } else {
                    LazyColumn {
                        items(playlists) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addSongToPlaylist(playlist.id, showAddToPlaylistSheet!!)
                                        showAddToPlaylistSheet = null
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(SurfaceGray, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QueueMusic, null, tint = PrimaryGreen)
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(playlist.nome.orEmpty(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            Divider(color = SurfaceGray.copy(alpha = 0.5f))
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GreetingHeader() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Bom Dia :)"
        in 12..17 -> "Boa Tarde :)"
        else -> "Boa Noite :)"
    }

    Text(
        text = greeting,
        style = MaterialTheme.typography.headlineMedium,
        color = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistListScreen(viewModel: UnimusicViewModel) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var selectedPlaylist by remember { mutableStateOf<PlaylistDTO?>(null) }

    BackHandler(enabled = selectedPlaylist != null) {
        selectedPlaylist = null
    }

    if (selectedPlaylist != null) {
        PlaylistDetailView(
            playlist = selectedPlaylist!!,
            viewModel = viewModel,
            onBack = { selectedPlaylist = null }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = PrimaryGreen,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Criar")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(
                    "Playlists", // Common term, leaving as is
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(24.dp)
                )

                LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                    if (playlists.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.LibraryMusic, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                                    Spacer(Modifier.height(16.dp))
                                    Text("Crie sua playlist", color = Color.Gray) // Already in Portuguese
                                }
                            }
                        }
                    }
                    items(playlists) { playlist ->
                        PlaylistItem(playlist) { selectedPlaylist = playlist }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = SurfaceGray,
                title = { Text("Nova playlist", color = Color.White) }, // Already in Portuguese
                text = {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Nome da Playlist") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                viewModel.createPlaylist(newPlaylistName)
                                newPlaylistName = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) { Text("Criar", color = Color.Black) }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar", color = PrimaryGreen) } // Already in Portuguese
                }
            )
        }
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistDTO, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF333333), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.QueueMusic, null, tint = Color.Gray)
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(playlist.nome.orEmpty(), style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                "${playlist.musicas?.size ?: 0} músicas",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

@Composable
fun PlaylistDetailView(
    playlist: PlaylistDTO,
    viewModel: UnimusicViewModel,
    onBack: () -> Unit
) {
    val songs = playlist.musicas ?: emptyList()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AlmostBlack)
                .padding(top = 32.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                playlist.nome.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = {
                viewModel.deletePlaylist(playlist.id)
                onBack()
            }) {
                Icon(Icons.Outlined.Delete, null, tint = Color.Red.copy(alpha = 0.7f))
            }
        }

        if (songs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Playlist vazia.", color = Color.Gray) // Already in Portuguese
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                items(songs) { musica ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            MusicListItem(
                                song = musica,
                                coverUrl = viewModel.getCoverUrl(musica),
                                onClick = { viewModel.playSong(musica, songs) },
                                onLongClick = {}
                            )
                        }
                        IconButton(onClick = {
                            viewModel.removeSongFromPlaylist(playlist.id, musica.id)
                        }) {
                            Icon(Icons.Default.RemoveCircleOutline, null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicListItem(
    song: MusicaDTO,
    coverUrl: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .crossfade(true)
                .error(android.R.drawable.ic_menu_gallery)
                .build(),
            contentDescription = "Capa",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.titulo.trim(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.getSafeArtist(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MiniPlayer(
    song: MusicaDTO,
    isPlaying: Boolean,
    coverUrl: String,
    onTogglePlay: () -> Unit,
    onClick: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceGray,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Capa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(song.titulo, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, fontSize = 14.sp)
                Text(song.getSafeArtist(), color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }

            IconButton(onClick = onTogglePlay) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Tocar",
                    tint = Color.White
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, null, tint = Color.White)
            }
        }
    }
}


@Composable
fun FullScreenPlayer(
    song: MusicaDTO,
    isPlaying: Boolean,
    coverUrl: String,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(top = 32.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f)
                .background(Color.Black)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.KeyboardArrowDown, "Fechar", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.weight(1f))
                Text("Tocando agora", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(32.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Capa Grande",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(20.dp, RoundedCornerShape(24.dp))
            )

            Spacer(Modifier.height(40.dp))

            Text(
                song.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.getSafeArtist(),
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen.copy(alpha = 0.8f),
                modifier = Modifier.alpha(0.8f)
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrev, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipPrevious, "Anterior", tint = Color.White, modifier = Modifier.size(36.dp))
                }

                Button(
                    onClick = onPlayPause,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.size(80.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        "Tocar/Pausar",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipNext, "Próxima", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(64.dp))
        }
    }
}