package com.example.movieapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.movieapp.ui.theme.MovieAppTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import com.example.movieapp.ui.theme.Orange80
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.NavigationBarItemDefaults

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearch() {
    val context = LocalContext.current
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<MovieResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val apiKey = stringResource(id = R.string.tmdb_api_key)
    val coroutineScope = rememberCoroutineScope()

    Column {
        CenterAlignedTopAppBar(
            title = {
                if (isSearching) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF232323), RoundedCornerShape(24.dp))
                                .border(1.dp, Orange80, RoundedCornerShape(24.dp))
                        ) {
                            SearchTextField(
                                value = searchText,
                                onValueChange = { text ->
                                    searchText = text
                                    if (text.isNotBlank()) {
                                        coroutineScope.launch {
                                            isLoading = true
                                            error = null
                                            try {
                                                val response = withContext(Dispatchers.IO) {
                                                    ApiService.movieApi.searchMovies(apiKey, text)
                                                }
                                                searchResults = response.results
                                                isLoading = false
                                            } catch (e: Exception) {
                                                error = e.localizedMessage
                                                isLoading = false
                                            }
                                        }
                                    } else {
                                        searchResults = emptyList()
                                        error = null
                                    }
                                },
                                onClear = {
                                    searchText = ""
                                    searchResults = emptyList()
                                    error = null
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancel",
                            color = Orange80,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable {
                                    isSearching = false
                                    searchText = ""
                                    searchResults = emptyList()
                                    error = null
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    Text("Movie Villa", color = Orange80)
                }
            },
            actions = {
                if (!isSearching) {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) {
                            searchText = ""
                            searchResults = emptyList()
                            error = null
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Orange80
                        )
                    }
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Black
            )
        )
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                when {
                    isLoading -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
                    error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                    searchResults.isNotEmpty() -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(8.dp, bottom = 80.dp)
                    ) {
                        items(searchResults) { movie ->
                            MovieCard(movie = movie)
                        }
                    }
                    searchText.isNotBlank() -> Text("No results found.", color = Color.White, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            MovieTabs(context)
        }
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(Color.Black, RoundedCornerShape(24.dp))
            .border(1.dp, Orange80, RoundedCornerShape(24.dp))
            .fillMaxWidth()
            .height(48.dp)
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Orange80
                )
            },
            trailingIcon = {
                if (value.isNotEmpty() && onClear != null) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                disabledContainerColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,    // <--- Add this
                unfocusedIndicatorColor = Color.Transparent   // <--- And this
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.Transparent)
        )
    }
}

@Composable
fun MovieTabs(context: Context,modifier: Modifier = Modifier) {
    val tabTitles = listOf("Popular", "Trending", "Top Rated")
    val tabIcons = listOf(
        Icons.Filled.Whatshot,
        Icons.Filled.TrendingUp,
        Icons.Filled.Star
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Black,
                contentColor = Orange80,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Orange80
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = tabTitles[index],
                                    tint = if (selectedTabIndex == index) Orange80 else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    tabTitles[index],
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (selectedTabIndex == index) Orange80 else Color.White
                                )
                            }
                        }
                    )
                }
            }
            // Content for each tab
            when (selectedTabIndex) {
                0 -> PopularMoviesGrid(context, key = "PopularMoviesGrid")
                1 -> TrendingMoviesGrid(context, key = "TrendingMoviesGrid")
                2 -> TopRatedMoviesGrid(context, key = "TopRatedMoviesGrid")
            }
        }
    }
}


@Composable
fun PopularMoviesGrid(context: Context, key: Any = "PopularMoviesGrid") {
    var movies by remember { mutableStateOf<List<MovieResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val apiKey = stringResource(id = com.example.movieapp.R.string.tmdb_api_key)

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getPopularMovies(apiKey, page)
            }
            if (page == 1) {
                movies = response.results
            } else {
                movies = movies + response.results
            }
            endReached = response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == movies.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && movies.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie)
                }
                if (isLoading && movies.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingMoviesGrid(context: Context, key: Any = "TrendingMoviesGrid") {
    var movies by remember { mutableStateOf<List<MovieResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getTrendingMovies(context.getString(R.string.tmdb_api_key), page)
            }
            if (page == 1) {
                movies = response.results
            } else {
                movies = movies + response.results
            }
            endReached = response.results.isEmpty() || response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        isLoading = false
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == movies.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && movies.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie)
                }
                if (isLoading && movies.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopRatedMoviesGrid(context: Context, key: Any = "TopRatedMoviesGrid") {
    var movies by remember { mutableStateOf<List<MovieResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getTopRatedMovies(context.getString(R.string.tmdb_api_key), page)
            }
            if (page == 1) {
                movies = response.results
            } else {
                movies = movies + response.results
            }
            endReached = response.results.isEmpty() || response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        isLoading = false
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == movies.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && movies.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie)
                }
                if (isLoading && movies.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SeasonSection() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Popular", "Trending", "Top Rated")
    val tabIcons = listOf(
        Icons.Filled.Whatshot,
        Icons.Filled.TrendingUp,
        Icons.Filled.Star
    )
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Black,
                contentColor = Orange80,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Orange80
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = tabTitles[index],
                                    tint = if (selectedTabIndex == index) Orange80 else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    tabTitles[index],
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (selectedTabIndex == index) Orange80 else Color.White
                                )
                            }
                        }
                    )
                }
            }
            // Content for each tab
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (selectedTabIndex) {
                    0 -> PopularTvGrid(context, key = "PopularTvGrid")
                    1 -> TrendingTvGrid(context, key = "TrendingTvGrid")
                    2 -> TopRatedTvGrid(context, key = "TopRatedTvGrid")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonTopBar() {
    val context = LocalContext.current
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<TvShowResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val apiKey = stringResource(id = R.string.tmdb_api_key)
    val coroutineScope = rememberCoroutineScope()

    Column {
        CenterAlignedTopAppBar(
            title = {
                if (isSearching) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF232323), RoundedCornerShape(24.dp))
                                .border(1.dp, Orange80, RoundedCornerShape(24.dp))
                        ) {
                            SearchTextField(
                                value = searchText,
                                onValueChange = { text ->
                                    searchText = text
                                    if (text.isNotBlank()) {
                                        coroutineScope.launch {
                                            isLoading = true
                                            error = null
                                            try {
                                                val response = withContext(Dispatchers.IO) {
                                                    ApiService.movieApi.searchTv(apiKey, text)
                                                }
                                                searchResults = response.results
                                                isLoading = false
                                            } catch (e: Exception) {
                                                error = e.localizedMessage
                                                isLoading = false
                                            }
                                        }
                                    } else {
                                        searchResults = emptyList()
                                        error = null
                                    }
                                },
                                onClear = {
                                    searchText = ""
                                    searchResults = emptyList()
                                    error = null
                                }
                            )
                        }
                        Text(
                            text = "Cancel",
                            color = Orange80,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable {
                                    isSearching = false
                                    searchText = ""
                                    searchResults = emptyList()
                                    error = null
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    Text("Season Villa", color = Orange80)
                }
            },
            actions = {
                if (!isSearching) {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) {
                            searchText = ""
                            searchResults = emptyList()
                            error = null
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Orange80
                        )
                    }
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Black
            )
        )
        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                when {
                    isLoading -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
                    error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                    searchResults.isNotEmpty() -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(8.dp, bottom = 80.dp)
                    ) {
                        items(searchResults) { tvShow ->
                            TvShowCard(tvShow = tvShow)
                        }
                    }
                    searchText.isNotBlank() -> Text("No results found.", color = Color.White, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            SeasonSection()
        }
    }
}

@Composable
fun MainScreen() {
    var selectedBottomTab by remember { mutableStateOf(0) } // 0: Movies, 1: Season
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            when (selectedBottomTab) {
                0 -> { TopBarWithSearch() }
                1 -> { SeasonTopBar() }
                else -> {}
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = selectedBottomTab == 0,
                    onClick = { selectedBottomTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Movie,
                            contentDescription = "Movies",
                            tint = if (selectedBottomTab == 0) Orange80 else Color.White
                        )
                    },
                    label = {
                        Text(
                            "Movies",
                            color = if (selectedBottomTab == 0) Orange80 else Color.White
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Orange80,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Orange80,
                        unselectedTextColor = Color.White
                    )
                )
                NavigationBarItem(
                    selected = selectedBottomTab == 1,
                    onClick = { selectedBottomTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Tv,
                            contentDescription = "Season",
                            tint = if (selectedBottomTab == 1) Orange80 else Color.White
                        )
                    },
                    label = {
                        Text(
                            "Season",
                            color = if (selectedBottomTab == 1) Orange80 else Color.White
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Orange80,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Orange80,
                        unselectedTextColor = Color.White
                    )
                )
            }
        }
    ) { innerPadding ->
        if (selectedBottomTab == 0) {
            MovieTabs(LocalContext.current, modifier = Modifier.padding(innerPadding))
        } else {
            Box(modifier = Modifier.padding(innerPadding)) {
                SeasonSection()
            }
        }
    }
}


@Composable
fun PopularTvGrid(context: Context, key: Any = "PopularTvGrid") {
    var tvShows by remember { mutableStateOf<List<TvShowResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val apiKey = stringResource(id = com.example.movieapp.R.string.tmdb_api_key)

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getPopularTv(apiKey, page)
            }
            if (page == 1) {
                tvShows = response.results
            } else {
                tvShows = tvShows + response.results
            }
            endReached = response.results.isEmpty() || response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        isLoading = false
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == tvShows.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && tvShows.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(tvShows) { tvShow ->
                    TvShowCard(tvShow = tvShow)
                }
                if (isLoading && tvShows.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingTvGrid(context: Context, key: Any = "TrendingTvGrid") {
    var tvShows by remember { mutableStateOf<List<TvShowResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val apiKey = stringResource(id = com.example.movieapp.R.string.tmdb_api_key)

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getTrendingTv(apiKey, page)
            }
            if (page == 1) {
                tvShows = response.results
            } else {
                tvShows = tvShows + response.results
            }
            endReached = response.results.isEmpty() || response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        isLoading = false
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == tvShows.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && tvShows.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(tvShows) { tvShow ->
                    TvShowCard(tvShow = tvShow)
                }
                if (isLoading && tvShows.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopRatedTvGrid(context: Context, key: Any = "TopRatedTvGrid") {
    var tvShows by remember { mutableStateOf<List<TvShowResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val apiKey = stringResource(id = com.example.movieapp.R.string.tmdb_api_key)

    LaunchedEffect(key, page) {
        if (endReached) return@LaunchedEffect
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                ApiService.movieApi.getTopRatedTv(apiKey, page)
            }
            if (page == 1) {
                tvShows = response.results
            } else {
                tvShows = tvShows + response.results
            }
            endReached = response.results.isEmpty() || response.results.size < 20
            error = null
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        isLoading = false
    }

    LaunchedEffect(key, gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && !endReached && lastVisibleItemIndex == tvShows.lastIndex) {
                    page++
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && tvShows.isEmpty() -> Text("Loading...", color = Color.White, modifier = Modifier.padding(16.dp))
            error != null -> Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = gridState
            ) {
                items(tvShows) { tvShow ->
                    TvShowCard(tvShow = tvShow)
                }
                if (isLoading && tvShows.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Loading more...", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
