package com.example.ui.screens.qiraat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IslamicMadhhab
import com.example.data.model.QiraatFiqhImpact
import com.example.data.model.TenQiraah
import com.example.data.model.TenRecitationsData
import com.example.ui.components.HeroQuranBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSurfaceVariant
import com.example.util.AudioRecordManager
import com.example.viewmodel.MainViewModel

@Composable
fun TenRecitationsScreen(
  viewModel: MainViewModel,
  onEvaluateInQiraah: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val audioManager = remember { AudioRecordManager(context) }

  DisposableEffect(Unit) {
    onDispose {
      audioManager.release()
    }
  }

  var selectedTab by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var currentlyPlayingUrl by remember { mutableStateOf<String?>(null) }
  var selectedQiraahForDetails by remember { mutableStateOf<TenQiraah?>(null) }
  var selectedMadhhabForDetails by remember { mutableStateOf<IslamicMadhhab?>(null) }

  val qiraat = TenRecitationsData.qiraatList
  val madhhabs = TenRecitationsData.madhhabsList
  val fiqhExamples = TenRecitationsData.fiqhImpactExamples

  val filteredQiraat = remember(searchQuery, qiraat) {
    if (searchQuery.isBlank()) {
      qiraat
    } else {
      qiraat.filter { q ->
        q.imamName.contains(searchQuery, ignoreCase = true) ||
            q.city.contains(searchQuery, ignoreCase = true) ||
            q.rawi1.name.contains(searchQuery, ignoreCase = true) ||
            q.rawi2.name.contains(searchQuery, ignoreCase = true) ||
            q.madhhabContext.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  val filteredMadhhabs = remember(searchQuery, madhhabs) {
    if (searchQuery.isBlank()) {
      madhhabs
    } else {
      madhhabs.filter { m ->
        m.name.contains(searchQuery, ignoreCase = true) ||
            m.founder.contains(searchQuery, ignoreCase = true) ||
            m.primaryQiraat.any { it.contains(searchQuery, ignoreCase = true) } ||
            m.geographicSpread.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Hero Banner
    item {
      HeroQuranBanner(
        title = "القراءات العشر المتواترة لجميع المذاهب الإسلامية",
        subtitle = "حلقات هائل سعيد أنعم • الأئمة العشرة ورواتهم العشرون، وموقف المذاهب الإسلامية وإجماع الأمة"
      )
    }

    // Quick Stats
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "القراءات المتواترة",
          value = "١٠ قراءات",
          icon = Icons.Default.MenuBook,
          tintColor = QuranEmeraldPrimary,
          bgColor = QuranEmeraldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "الروايات المعتمدة",
          value = "٢٠ رواية",
          icon = Icons.Default.Bookmark,
          tintColor = QuranGoldSecondary,
          bgColor = QuranGoldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "المذاهب الإسلامية",
          value = "٧ مذاهب مجمعة",
          icon = Icons.Default.School,
          tintColor = Color(0xFF2563EB),
          bgColor = Color(0xFFEFF6FF),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Tabs Header
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = QuranSurfaceVariant.copy(alpha = 0.5f),
        contentColor = QuranEmeraldPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = QuranEmeraldPrimary,
            height = 3.dp
          )
        },
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .testTag("qiraat_tabs_row")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              text = "القراءات العشر (١٠)",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "المذاهب الإسلامية (٧)",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              text = "أثر الفقه وإجماع الأمة",
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        )
      }
    }

    // Tab 0: The Ten Recitations
    if (selectedTab == 0) {
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث باسم القارئ، الراوي، البلد أو المذهب...") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = QuranEmeraldPrimary)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
              }
            }
          },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("qiraat_search_field"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = QuranEmeraldPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          )
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "أئمة القراءات العشر ورواتهم العشرون (${filteredQiraat.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "اضغط للاستماع أو عرض الأصول",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      items(filteredQiraat, key = { it.id }) { qiraah ->
        QiraahCard(
          qiraah = qiraah,
          isPlaying = currentlyPlayingUrl == qiraah.audioSampleUrl,
          onPlayAudio = {
            if (currentlyPlayingUrl == qiraah.audioSampleUrl) {
              audioManager.stopPlayback()
              currentlyPlayingUrl = null
            } else {
              currentlyPlayingUrl = qiraah.audioSampleUrl
              audioManager.playAudio(qiraah.audioSampleUrl) {
                currentlyPlayingUrl = null
              }
            }
          },
          onCardClick = { selectedQiraahForDetails = qiraah }
        )
      }
    }

    // Tab 1: All Islamic Madhhabs & Ten Recitations
    if (selectedTab == 1) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(QuranEmeraldPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "موقف جميع المذاهب الإسلامية من القراءات العشر",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldDark
              )
              Text(
                text = "إجماع قطعي بين سائر المذاهب على تواتر القراءات العشر وصحة الصلاة والائتمام بها",
                style = MaterialTheme.typography.bodySmall,
                color = QuranEmeraldDark.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
            }
          }
        }
      }

      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث بالمذهب، المؤسس، القراءة المعتمدة، أو البلد...") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = QuranEmeraldPrimary)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
              }
            }
          },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("madhhab_search_field"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = QuranEmeraldPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          )
        )
      }

      items(filteredMadhhabs, key = { it.id }) { madhhab ->
        MadhhabCard(
          madhhab = madhhab,
          onCardClick = { selectedMadhhabForDetails = madhhab }
        )
      }
    }

    // Tab 2: Fiqh Impact & Islamic Consensus Charter
    if (selectedTab == 2) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = QuranEmeraldPrimary),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(26.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "ميثاق إجماع الأمة الإسلامية على القراءات العشر",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = TenRecitationsData.consensusCharter,
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.95f),
              fontSize = 12.sp,
              lineHeight = 20.sp
            )
          }
        }
      }

      item {
        Text(
          text = "أثر اختلاف القراءات في استنباط الأحكام الفقهية بين المذاهب",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      items(fiqhExamples) { example ->
        FiqhImpactCard(example = example)
      }
    }
  }

  // Details Dialog for Qiraah
  selectedQiraahForDetails?.let { qiraah ->
    QiraahDetailsDialog(
      qiraah = qiraah,
      onDismiss = { selectedQiraahForDetails = null },
      onEvaluate = {
        onEvaluateInQiraah("${qiraah.rawi2.name} عن ${qiraah.imamName}")
        selectedQiraahForDetails = null
      }
    )
  }

  // Details Dialog for Madhhab
  selectedMadhhabForDetails?.let { madhhab ->
    MadhhabDetailsDialog(
      madhhab = madhhab,
      onDismiss = { selectedMadhhabForDetails = null }
    )
  }
}

@Composable
fun QiraahCard(
  qiraah: TenQiraah,
  isPlaying: Boolean,
  onPlayAudio: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick)
      .testTag("qiraah_card_${qiraah.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(qiraah.colorHex).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${qiraah.id}",
              color = Color(qiraah.colorHex),
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = qiraah.imamName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${qiraah.city} (ت ${qiraah.deathYear})",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Audio sample playback button
        IconButton(
          onClick = onPlayAudio,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (isPlaying) QuranEmeraldPrimary else QuranEmeraldLight)
            .testTag("audio_play_qiraah_${qiraah.id}")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
            contentDescription = "استماع للقراءة",
            tint = if (isPlaying) Color.White else QuranEmeraldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // The Two Rawis (الراويان)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(QuranEmeraldLight)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "الراوي الأول: ${qiraah.rawi1.name}",
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark,
              fontSize = 12.sp
            )
            Text(
              text = qiraah.rawi1.famousUsul,
              color = QuranEmeraldDark.copy(alpha = 0.85f),
              fontSize = 10.sp,
              maxLines = 2
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(QuranGoldLight)
            .padding(8.dp)
        ) {
          Column {
            Text(
              text = "الراوي الثاني: ${qiraah.rawi2.name}",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF6B4E00),
              fontSize = 12.sp
            )
            Text(
              text = qiraah.rawi2.famousUsul,
              color = Color(0xFF6B4E00).copy(alpha = 0.85f),
              fontSize = 10.sp,
              maxLines = 2
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Geographic & Madhhab context
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(QuranSurfaceVariant.copy(alpha = 0.5f))
          .padding(10.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Text(
            text = "🌍 الانتشار: ${qiraah.geographicSpread}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
          )
          Text(
            text = "⚖️ في المذاهب: ${qiraah.madhhabContext}",
            style = MaterialTheme.typography.bodySmall,
            color = QuranEmeraldPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun MadhhabCard(
  madhhab: IslamicMadhhab,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick)
      .testTag("madhhab_card_${madhhab.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(madhhab.colorHex).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = Color(madhhab.colorHex),
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = madhhab.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = madhhab.founder,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(madhhab.colorHex).copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = madhhab.originCity,
            color = Color(madhhab.colorHex),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Primary Qira'at chips
      Text(
        text = "القراءات الأكثر ارتباطاً بالمذهب:",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        madhhab.primaryQiraat.take(2).forEach { q ->
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(QuranEmeraldLight)
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Text(
              text = q,
              color = QuranEmeraldDark,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Stance & Prayer Ruling summary
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(QuranSurfaceVariant.copy(alpha = 0.5f))
          .padding(10.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "📜 الموقف: ${madhhab.stanceOnTenQiraat}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
          )
          Text(
            text = "🕌 حكم الصلاة: ${madhhab.prayerValidity}",
            style = MaterialTheme.typography.bodySmall,
            color = QuranEmeraldPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "«${madhhab.famousQuote}»",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun FiqhImpactCard(example: QiraatFiqhImpact) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = example.ayahText,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = QuranEmeraldPrimary,
          fontSize = 14.sp
        )
        Text(
          text = example.surahAndAyah,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(QuranGoldLight)
          .padding(8.dp)
      ) {
        Text(
          text = "وجوه القراءات: ${example.qiraatVariation}",
          fontSize = 11.sp,
          color = Color(0xFF6B4E00),
          fontWeight = FontWeight.Medium
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "🔹 دلالة الحكم (الحنفية): ${example.fiqhRulingHanafi}",
          fontSize = 11.sp
        )
        Text(
          text = "🔹 دلالة الحكم (الجمهور والمالكية والشافعية): ${example.fiqhRulingShafiiMaliki}",
          fontSize = 11.sp
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(QuranEmeraldLight)
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = QuranEmeraldPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "الحكمة التشريعية: ${example.lesson}",
            fontSize = 11.sp,
            color = QuranEmeraldDark,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun QiraahDetailsDialog(
  qiraah: TenQiraah,
  onDismiss: () -> Unit,
  onEvaluate: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.MenuBook,
          contentDescription = null,
          tint = QuranEmeraldPrimary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = qiraah.imamName,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = qiraah.imamTitle,
          color = QuranEmeraldPrimary,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(QuranEmeraldLight)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "🔹 الراوي الأول: ${qiraah.rawi1.fullName} (ت ${qiraah.rawi1.deathYear})",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(text = "الأصول: ${qiraah.rawi1.famousUsul}", fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "🔹 الراوي الثاني: ${qiraah.rawi2.fullName} (ت ${qiraah.rawi2.deathYear})",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(text = "الأصول: ${qiraah.rawi2.famousUsul}", fontSize = 11.sp)
          }
        }

        Text(text = "أبرز أصول وقواعد القراءة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        qiraah.keyRules.forEach { rule ->
          Text(text = "• $rule", fontSize = 12.sp)
        }

        Text(
          text = "العلاقة بالمذاهب الإسلامية والتوزيع الجغرافي:",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Text(text = qiraah.madhhabContext, fontSize = 12.sp)
        Text(text = qiraah.geographicSpread, fontSize = 12.sp)
      }
    },
    confirmButton = {
      Button(
        onClick = onEvaluate,
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("تقييم تسميع بهذه القراءة", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إغلاق")
      }
    }
  )
}

@Composable
fun MadhhabDetailsDialog(
  madhhab: IslamicMadhhab,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.School,
          contentDescription = null,
          tint = Color(madhhab.colorHex),
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${madhhab.name} والقراءات العشر",
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "${madhhab.title} • ${madhhab.originCity} (${madhhab.era})",
          color = Color(madhhab.colorHex),
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(QuranEmeraldLight)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "📜 موقف المذهب من القراءات العشر:",
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark,
              fontSize = 12.sp
            )
            Text(
              text = madhhab.stanceOnTenQiraat,
              color = QuranEmeraldDark,
              fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "🕌 حكم الصلاة بها:",
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark,
              fontSize = 12.sp
            )
            Text(
              text = madhhab.prayerValidity,
              color = QuranEmeraldDark,
              fontSize = 11.sp
            )
          }
        }

        Text(
          text = "كبار أئمة القراءات المنتسبين للمذهب:",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
        madhhab.prominentQiraatScholars.forEach { scholar ->
          Text(text = "• $scholar", fontSize = 11.sp)
        }

        Text(
          text = "مأثور الإمام:",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
        Text(
          text = madhhab.famousQuote,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
          text = "أماكن الانتشار الجغرافي:",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
        Text(
          text = madhhab.geographicSpread,
          fontSize = 11.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("حسناً", fontWeight = FontWeight.Bold)
      }
    }
  )
}
