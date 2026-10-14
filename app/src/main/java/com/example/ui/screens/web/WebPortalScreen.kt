package com.example.ui.screens.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ApkDownloadDialog
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldSecondary
import com.example.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen(
  viewModel: MainViewModel,
  onBackToAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val webServerUrl by viewModel.webServerUrl.collectAsStateWithLifecycle()
  val apkDownloadUrl by viewModel.apkDownloadUrl.collectAsStateWithLifecycle()

  var currentUrl by remember { mutableStateOf(webServerUrl) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var pageTitle by remember { mutableStateOf("حلقات هائل سعيد أنعم") }
  var isLoading by remember { mutableStateOf(false) }
  var progressPercentage by remember { mutableIntStateOf(0) }
  var hasLoadError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  var showApkDialog by remember { mutableStateOf(false) }
  var showUrlEditDialog by remember { mutableStateOf(false) }
  var tempUrlInput by remember { mutableStateOf(currentUrl) }

  // Handle hardware / gesture back button:
  BackHandler {
    if (webViewRef?.canGoBack() == true) {
      webViewRef?.goBack()
    } else {
      onBackToAdmin()
    }
  }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Browser Navigation & Action Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = QuranEmeraldDark,
        shadowElevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          // Row 1: Back, Forward, Title / Address, Download APK, Return to Admin
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Return to admin dashboard button
              IconButton(
                onClick = onBackToAdmin,
                modifier = Modifier
                  .size(36.dp)
                  .testTag("portal_back_to_admin_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = "العودة للوحة الإدارة",
                  tint = QuranGoldSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }

              // WebView Back
              IconButton(
                onClick = { webViewRef?.goBack() },
                enabled = canGoBack,
                modifier = Modifier.size(34.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "رجوع",
                  tint = if (canGoBack) Color.White else Color.White.copy(alpha = 0.3f),
                  modifier = Modifier.size(18.dp)
                )
              }

              // WebView Forward
              IconButton(
                onClick = { webViewRef?.goForward() },
                enabled = canGoForward,
                modifier = Modifier.size(34.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "تقدم",
                  tint = if (canGoForward) Color.White else Color.White.copy(alpha = 0.3f),
                  modifier = Modifier.size(18.dp)
                )
              }

              // Reload
              IconButton(
                onClick = {
                  hasLoadError = false
                  webViewRef?.reload()
                },
                modifier = Modifier.size(34.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "إعادة تحميل",
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            // Quick APK Download & Open in Browser Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
              Button(
                onClick = { showApkDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = QuranGoldSecondary),
                shape = RoundedCornerShape(20.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                  .height(32.dp)
                  .testTag("portal_download_apk_chip")
              ) {
                Icon(
                  imageVector = Icons.Default.CloudDownload,
                  contentDescription = null,
                  tint = Color(0xFF382900),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "تحميل APK",
                  color = Color(0xFF382900),
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.width(4.dp))

              IconButton(
                onClick = {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "تعذر فتح المتصفح الخارجي", Toast.LENGTH_SHORT).show()
                  }
                },
                modifier = Modifier.size(34.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.OpenInBrowser,
                  contentDescription = "فتح في المتصفح الخارجي",
                  tint = Color.White.copy(alpha = 0.85f),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          // Row 2: Address pill with current URL & quick server presets
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // URL Display Pill
            Card(
              onClick = {
                tempUrlInput = currentUrl
                showUrlEditDialog = true
              },
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
              modifier = Modifier.weight(1f)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (currentUrl.startsWith("file:")) Icons.Default.StayCurrentPortrait else Icons.Default.Cloud,
                  contentDescription = null,
                  tint = QuranGoldSecondary,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (currentUrl.startsWith("file:")) "النسخة المحلية المدمجة (Offline Asset)" else currentUrl,
                  color = Color.White,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
                )
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "تعديل الرابط",
                  tint = Color.White.copy(alpha = 0.6f),
                  modifier = Modifier.size(13.dp)
                )
              }
            }

            // Quick toggle to Offline Asset / Live Server
            FilterChip(
              selected = currentUrl.startsWith("file:"),
              onClick = {
                hasLoadError = false
                currentUrl = viewModel.defaultOfflineAssetUrl
                viewModel.switchToOfflineAsset()
                webViewRef?.loadUrl(viewModel.defaultOfflineAssetUrl)
              },
              label = { Text("أوفلاين", fontSize = 10.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuranGoldSecondary,
                selectedLabelColor = Color(0xFF382900)
              ),
              modifier = Modifier.height(28.dp)
            )

            FilterChip(
              selected = !currentUrl.startsWith("file:"),
              onClick = {
                hasLoadError = false
                currentUrl = viewModel.defaultLiveServerUrl
                viewModel.switchToLiveServer()
                webViewRef?.loadUrl(viewModel.defaultLiveServerUrl)
              },
              label = { Text("السيرفر", fontSize = 10.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuranGoldSecondary,
                selectedLabelColor = Color(0xFF382900)
              ),
              modifier = Modifier.height(28.dp)
            )
          }

          // Loading Progress Bar
          AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(
              progress = { progressPercentage / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(2.dp),
              color = QuranGoldSecondary,
              trackColor = Color.White.copy(alpha = 0.2f),
            )
          }
        }
      }

      // Main WebView Container with Error Recovery Card
      Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
          modifier = Modifier
            .fillMaxSize()
            .testTag("app_webview_component"),
          factory = { ctx ->
            WebView(ctx).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )
              webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                  super.onPageStarted(view, url, favicon)
                  isLoading = true
                  url?.let { currentUrl = it }
                  canGoBack = canGoBack()
                  canGoForward = canGoForward()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  isLoading = false
                  url?.let { currentUrl = it }
                  pageTitle = view?.title ?: "حلقات هائل سعيد أنعم"
                  canGoBack = canGoBack()
                  canGoForward = canGoForward()
                }

                override fun onReceivedError(
                  view: WebView?,
                  request: WebResourceRequest?,
                  error: WebResourceError?
                ) {
                  super.onReceivedError(view, request, error)
                  if (request?.isForMainFrame == true) {
                    hasLoadError = true
                    errorMessage = error?.description?.toString() ?: "تعذر الوصول إلى السيرفر"
                  }
                }

                override fun shouldOverrideUrlLoading(
                  view: WebView?,
                  request: WebResourceRequest?
                ): Boolean {
                  val url = request?.url?.toString() ?: return false
                  // Keep http/https and file URLs inside WebView, pass other intents (e.g. tel:, mailto:) to system
                  return if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")) {
                    false
                  } else {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                      ctx.startActivity(intent)
                      true
                    } catch (e: Exception) {
                      false
                    }
                  }
                }
              }

              webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                  progressPercentage = newProgress
                  if (newProgress >= 100) {
                    isLoading = false
                  }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                  super.onReceivedTitle(view, title)
                  if (!title.isNullOrBlank()) {
                    pageTitle = title
                  }
                }
              }

              // WebView configuration as requested by user
              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
              }

              webViewRef = this
              loadUrl(currentUrl)
            }
          },
          update = { webView ->
            webViewRef = webView
          }
        )

        // Error / Offline Recovery Overlay
        if (hasLoadError) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.White.copy(alpha = 0.95f))
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF2F2)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(30.dp)
                  )
                }

                Text(
                  text = "تعذر الاتصال بالسيرفر السحابي حالياً",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = QuranEmeraldDark
                )

                Text(
                  text = errorMessage ?: "تأكد من اتصالك بالإنترنت، أو يمكنك التبديل إلى النسخة المحلية المدمجة داخل التطبيق للعمل دون اتصال.",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.Gray,
                  fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                  onClick = {
                    hasLoadError = false
                    currentUrl = viewModel.defaultOfflineAssetUrl
                    viewModel.switchToOfflineAsset()
                    webViewRef?.loadUrl(viewModel.defaultOfflineAssetUrl)
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(imageVector = Icons.Default.StayCurrentPortrait, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("فتح النسخة المحلية المدمجة فوراً (أوفلاين)")
                }

                OutlinedButton(
                  onClick = {
                    hasLoadError = false
                    webViewRef?.reload()
                  },
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("إعادة محاولة الاتصال بالسيرفر")
                }
              }
            }
          }
        }
      }
    }
  }

  // APK Download Dialog
  if (showApkDialog) {
    ApkDownloadDialog(
      downloadUrl = apkDownloadUrl,
      onDismiss = { showApkDialog = false }
    )
  }

  // URL edit dialog
  if (showUrlEditDialog) {
    AlertDialog(
      onDismissRequest = { showUrlEditDialog = false },
      title = { Text("تغيير رابط المنصة أو السيرفر", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("أدخل رابط موقعك أو سيرفر المنصة بعد الرفع:", fontSize = 12.sp, color = Color.Gray)
          OutlinedTextField(
            value = tempUrlInput,
            onValueChange = { tempUrlInput = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = QuranEmeraldPrimary
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val urlToLoad = if (!tempUrlInput.startsWith("http://") && !tempUrlInput.startsWith("https://") && !tempUrlInput.startsWith("file://")) {
              "https://$tempUrlInput"
            } else {
              tempUrlInput
            }
            currentUrl = urlToLoad
            viewModel.updateWebServerUrl(urlToLoad)
            webViewRef?.loadUrl(urlToLoad)
            showUrlEditDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary)
        ) {
          Text("تحميل الرابط")
        }
      },
      dismissButton = {
        TextButton(onClick = { showUrlEditDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  DisposableEffect(Unit) {
    onDispose {
      webViewRef?.destroy()
    }
  }
}
