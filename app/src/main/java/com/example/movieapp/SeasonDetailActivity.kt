package com.example.movieapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movieapp.ui.theme.Orange80
import com.example.movieapp.TvDetailResponse
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.clickable

class SeasonDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tvShow = intent.getParcelableExtra<TvShowResult>("tvShow")
        setContent {
            MaterialTheme {
                tvShow?.let {
                    SeasonDetailScreen(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun SeasonDetailScreen(tvShow: TvShowResult) {
    val context = LocalContext.current
    val apiKey = stringResource(id = R.string.tmdb_api_key)
    var tvDetail by remember { mutableStateOf<TvDetailResponse?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedSeasonIndex by remember { mutableStateOf(0) }
    var episodes by remember { mutableStateOf<List<EpisodeInfo>>(emptyList()) }
    // Add state for paging
    var loadedCount by remember { mutableStateOf(10) }
    val batchSize = 10
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tvShow.id) {
        coroutineScope.launch {
            try {
                val detail = withContext(Dispatchers.IO) {
                    ApiService.movieApi.getTvDetails(tvShow.id, apiKey)
                }
                tvDetail = detail
            } catch (_: Exception) {}
        }
    }

    // Filter out 'Specials' seasons
    val filteredSeasons = tvDetail?.seasons?.filter { it.name?.equals("Specials", ignoreCase = true) != true } ?: emptyList()

    // Fetch episodes when selected season changes
    LaunchedEffect(tvShow.id, selectedSeasonIndex, tvDetail?.seasons) {
        val seasonNumber = filteredSeasons.getOrNull(selectedSeasonIndex)?.season_number
        if (seasonNumber != null) {
            coroutineScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        ApiService.movieApi.getSeasonEpisodes(tvShow.id, seasonNumber, apiKey)
                    }
                    episodes = response.episodes ?: emptyList()
                    loadedCount = batchSize // Reset loaded count on season change
                } catch (_: Exception) {
                    episodes = emptyList()
                    loadedCount = batchSize
                }
            }
        } else {
            episodes = emptyList()
            loadedCount = batchSize
        }
    }

    // Remove parent Column with verticalScroll, use LazyColumn for all content
    val visibleEpisodes = episodes.take(loadedCount)
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {
        item {
            Box {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500" + (tvShow.poster_path ?: ""),
                    contentDescription = tvShow.name ?: tvShow.original_name ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )
                IconButton(
                    onClick = { (context as? android.app.Activity)?.finish() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Surface(
                    color = Orange80,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "IMDB",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Orange80,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = String.format("%.1f", tvShow.vote_average ?: 0.0),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (!tvShow.first_air_date.isNullOrBlank()) {
                Text(
                    text = "Release Date: ${tvShow.first_air_date}",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Add genres as chips using FlowRow
            val genres = ApiService.mapGenreIdsToNames(tvShow.genre_ids, apiKey)
            if (genres.isNotEmpty()) {
                com.google.accompanist.flowlayout.FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    genres.forEach { genre ->
                        Surface(
                            color = Color(0xFF232323),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = genre,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = tvShow.name ?: tvShow.original_name ?: "",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Restore overview here
            Text(
                text = tvShow.overview ?: "No description available.",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (tvDetail?.seasons != null && filteredSeasons.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .width(180.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = filteredSeasons.getOrNull(selectedSeasonIndex)?.name ?: "Season ${selectedSeasonIndex + 1}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Season", color = Orange80) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Orange80,
                                unfocusedBorderColor = Orange80,
                                focusedLabelColor = Orange80,
                                unfocusedLabelColor = Orange80,
                                cursorColor = Orange80,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.menuAnchor().width(180.dp)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.DarkGray)
                        ) {
                            filteredSeasons.forEachIndexed { index, season ->
                                DropdownMenuItem(
                                    text = { Text(season.name ?: "Season ${season.season_number}", color = Color.White) },
                                    onClick = {
                                        selectedSeasonIndex = index
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        // Episode items
        items(visibleEpisodes) { episode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        // Launch PlayerActivity for TV episode
                        val intent = Intent(context, PlayerActivity::class.java)
                        intent.putExtra("tv_id", tvShow.id)
                        intent.putExtra("season_number", tvDetail?.seasons?.getOrNull(selectedSeasonIndex)?.season_number ?: 1)
                        intent.putExtra("episode_number", episode.episode_number ?: 1)
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w300" + (episode.still_path ?: ""),
                    contentDescription = episode.name,
                    modifier = Modifier
                        .size(width = 140.dp, height = 90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${episode.episode_number}. ${episode.name ?: "No Title"}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!episode.air_date.isNullOrBlank()) {
                        Text(
                            text = "${episode.air_date}",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = episode.overview ?: "No description available.",
                        color = Color.White,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    // Infinite scroll: load more when near end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= visibleEpisodes.size - 3 && loadedCount < episodes.size) {
                    loadedCount = (loadedCount + batchSize).coerceAtMost(episodes.size)
                }
            }
    }
} 