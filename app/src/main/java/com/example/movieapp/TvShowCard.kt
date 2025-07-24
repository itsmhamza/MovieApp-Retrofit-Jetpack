package com.example.movieapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.movieapp.ui.theme.Orange80
import android.content.Intent

@Composable
fun TvShowCard(tvShow: TvShowResult, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(14.dp))
            .clickable {
                val intent = Intent(context, SeasonDetailActivity::class.java)
                intent.putExtra("tvShow", tvShow)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500" + (tvShow.poster_path ?: ""),
                contentDescription = tvShow.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = painterResource(id = com.example.movieapp.R.drawable.ic_launcher_foreground),
                error = painterResource(id = com.example.movieapp.R.drawable.ic_launcher_foreground)
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
                    text = ((tvShow.vote_average ?: 0.0) * 10).toInt().toString() + "%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 