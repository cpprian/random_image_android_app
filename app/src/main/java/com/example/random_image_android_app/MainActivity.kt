package com.example.random_image_android_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun RandomImage() {
    var imageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val queue: RequestQueue = Volley.newRequestQueue(LocalContext.current)
    val url = "https://api.unsplash.com/photos/random/?client_id=hinuiIoG5UFP-Z51gerFYMxkyZ5kyi2pWXkEpZSEk7Y"

    if (!isLoading && imageUrl.isEmpty()) {
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
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun coilImage(
    imageUrl: String,
    contentDescription: String?,
    fadeIn: Boolean = true,
    fadeInDurationMs: Int = 600
): Painter {
    val animationSpec = TweenSpec<Float>(durationMillis = 1000)

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Random_image_android_appTheme {
        RandomImage()
    }
}