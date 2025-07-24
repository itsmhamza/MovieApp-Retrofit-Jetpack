package com.example.movieapp

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.example.movieapp.ui.theme.Orange80
import androidx.compose.ui.res.stringResource
import com.example.movieapp.R
import androidx.compose.ui.res.painterResource

@Composable
fun MovieCard(movie: MovieResult, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val apiKey = stringResource(id = R.string.tmdb_api_key)
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(14.dp))
            .clickable {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movie_result", movie)
                val genreNames = ApiService.mapGenreIdsToNames(movie.genre_ids, apiKey)
                intent.putStringArrayListExtra("genres", ArrayList(genreNames))
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500" + (movie.poster_path ?: ""),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )
            // Vote badge (top left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(34.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black)
                    .border(1.dp, Orange80, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ((movie.vote_average!! * 10).toInt()).toString() + "%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 