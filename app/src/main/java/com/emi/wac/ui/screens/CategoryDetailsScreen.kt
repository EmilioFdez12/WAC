package com.emi.wac.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.ui.theme.WACTheme

@Composable
fun CategoryDetailsScreen(
    modifier: Modifier = Modifier,
    category: String
) {
    val backgroundPainter = rememberAsyncImagePainter(model = "file:///android_asset/background.webp")

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Three button component  Overview, Standings, Schedule

        // Card with the info of the leader driver -- portrait
        // Position 1º display and points

        // Same with the first contructor

        // Race weekend card with the schedule of the next grand prix
        // practice 1, 2, 3, sprint, qualifying, race day and time

        // Weather

        // Circuit img 
        
        // Info about the circuit 
    } 
}

@Preview(showBackground = true)
@Composable
fun CategoryDetailsPreview() {
    WACTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoryDetailsScreen(category = "f1")
        }
    }
}