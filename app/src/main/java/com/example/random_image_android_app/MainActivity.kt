package com.example.random_image_android_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.random_image_android_app.ui.theme.Random_image_android_appTheme
import org.json.JSONObject
import java.util.logging.Logger

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RandomImage() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var imageUrl by remember { mutableStateOf(listOf("")) }
    var isLoading by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(1) }

    val queue: RequestQueue = Volley.newRequestQueue(LocalContext.current)
    val url = "https://api.unsplash.com/photos/random/?count=$count&query=$query&client_id=hinuiIoG5UFP-Z51gerFYMxkyZ5kyi2pWXkEpZSEk7Y"

    fun loadImage() {
        isLoading = true
        imageUrl = listOf("")
        Log.d("MyTag", "Loading image with URL: $url")
        val jsonArrayRequest = JsonArrayRequest(url,
            { response ->
                val urls = mutableListOf<String>()
                for (i in 0 until response.length()) {
                    val urlsObject = response.getJSONObject(i).getJSONObject("urls")
                    val regularUrl = urlsObject.getString("regular")
                    urls.add(regularUrl)
                }
                imageUrl = urls
                isLoading = false
                Log.d("MyTag", response.toString())
                Log.d("MyTag", "Image loaded successfully")
            },
            { error ->
                isLoading = false
                Log.e("MyTag", "Error loading image: ${error.message}")
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                5000,  // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        queue.add(jsonArrayRequest)
    }


    if (!isLoading && imageUrl.size == 1 && imageUrl[0].isEmpty()) {
        Log.d("MyTag", "Start loading image")
        loadImage()
    }

    Scaffold(
        topBar = {
            Column {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(text = "Search") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Move focus to the next TextField
                            focusRequester.requestFocus()
                        }
                    ),
                )
                TextField(
                    value = count.toString(),
                    onValueChange = { count = it.toIntOrNull() ?: 0 },
                    label = { Text(text = "Number of images") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            loadImage()
                            keyboardController?.hide()
                        }
                    ),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { loadImage() },
                modifier = Modifier
                    .padding(16.dp)) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && imageUrl.size == 1 && imageUrl[0].isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            } else {
                if (imageUrl.isNotEmpty()) {
                    LazyColumn(content = {
                        items(imageUrl.size) { index ->
                            Log.d("MyTag", "Loading image: ${imageUrl[index]}")
                            Image(
                                painter = rememberImagePainter(
                                    data = imageUrl[index],
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )
                        }
                    })
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
}
