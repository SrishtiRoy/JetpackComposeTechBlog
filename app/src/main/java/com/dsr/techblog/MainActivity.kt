package com.dsr.techblog


import androidx.compose.ui.tooling.preview.Preview
import com.dsr.techblog.ui.theme.TechBlogTheme

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TechBlogApp()
                }
            }
        }
    }
}



@Composable
fun TechBlogApp() {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "company_list",
            modifier = Modifier
        ) {
            composable("company_list") {
                CompanyListScreen(
                    paddingValues = paddingValues,
                    onCompanyClick = { company ->
                        val encodedUrl = URLEncoder.encode(company.url, StandardCharsets.UTF_8.toString())
                        navController.navigate("webview/${company.name}/$encodedUrl")
                    }
                )
            }

            composable(
                "webview/{name}/{url}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("url") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val url = backStackEntry.arguments?.getString("url") ?: ""
                WebViewScreen(name, url, navController)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListScreen(
    paddingValues: PaddingValues,
    onCompanyClick: (Company) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Top TechBlog") },

            )
        }
    ) {paddingValues->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(companies) { company ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCompanyClick(company) }
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(company.logoUrl),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(name: String, url: String, navController: NavController) {
    val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.padding(padding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl(decodedUrl)
                }
            }
        )
    }
}


