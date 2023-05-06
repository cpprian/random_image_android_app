package com.example.random_image_android_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.random_image_android_app.ui.theme.Random_image_android_appTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Random_image_android_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RandomImage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomImage() {
    var imageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("android") }

    val queue: RequestQueue = Volley.newRequestQueue(LocalContext.current)
    val url = "https://api.unsplash.com/photos/random/?query=$query&client_id=hinuiIoG5UFP-Z51gerFYMxkyZ5kyi2pWXkEpZSEk7Y"

    fun loadImage() {
        isLoading = true
        val jsonObjectRequest = JsonObjectRequest(url, null,
            { response ->
                imageUrl = response.getString("urls")
                    .let { urls ->
                        JSONObject(urls).getString("regular")
                    }
                isLoading = false
            },
            { error ->
                isLoading = false
            }
        )

        queue.add(jsonObjectRequest)
    }

    if (!isLoading && imageUrl.isEmpty()) {
        loadImage()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )
        } else {
            if (imageUrl.isNotEmpty()) {
                Image(
                    painter = coilImage(imageUrl = imageUrl,
                        contentDescription = null),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "Failed to load image",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }


            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(text = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions(
                    onDone = {
                        loadImage()
                    }
                ),
            )
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun coilImage(
    imageUrl: String,
    contentDescription: String?,
    fadeIn: Boolean = true
): Painter {
    val animationSpec = TweenSpec<Float>(durationMillis = 300)

    val painter = rememberImagePainter(
        data = imageUrl,
        builder = {
            if (fadeIn) {
                fadeIn(animationSpec)
            }
        }
    )
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
    return painter
}
