package com.example.ui.screens.circles

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.MockHalaqatData
import com.example.data.model.QuranHalaqah
import com.example.ui.components.HeroQuranBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.QuranEmeraldDark
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSurfaceVariant
import com.example.util.ShareHelper
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UserRole

@Composable
fun CirclesScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedTimeFilter by remember { mutableStateOf("الكل") }
  var selectedHalaqahForDetails by remember { mutableStateOf<QuranHalaqah?>(null) }

  val timeFilters = listOf("الكل", "بعد العصر", "بعد المغرب", "بعد الفجر", "بعد العشاء")

  // Mock list of halaqat
  val allHalaqat = MockHalaqatData.halaqatList

  val filteredHalaqat = allHalaqat.filter { halaqah ->
    val matchesSearch = halaqah.name.contains(searchQuery, ignoreCase = true) ||
        halaqah.teacherName.contains(searchQuery, ignoreCase = true) ||
        halaqah.location.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (selectedTimeFilter) {
      "الكل" -> true
      "بعد العصر" -> halaqah.timeSlot.contains("العصر")
      "بعد المغرب" -> halaqah.timeSlot.contains("المغرب")
      "بعد الفجر" -> halaqah.timeSlot.contains("الفجر")
      "بعد العشاء" -> halaqah.timeSlot.contains("العشاء")
      else -> true
    }
    matchesSearch && matchesFilter
  }

  val totalStudents = allHalaqat.sumOf { it.studentCount }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Hero Banner
    item {
      HeroQuranBanner(
        title = "دليل حلقات تحفيظ القرآن الكريم",
        subtitle = "قائمة الحلقات القرآنية المعتمدة والمعلمين المجازين"
      )
    }

    // Quick Stats Row
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "الحلقات النشطة",
          value = "${allHalaqat.size}",
          icon = Icons.Default.MenuBook,
          tintColor = QuranEmeraldPrimary,
          bgColor = QuranEmeraldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "المعلمين المجازين",
          value = "${allHalaqat.map { it.teacherName }.distinct().size}",
          icon = Icons.Default.School,
          tintColor = QuranGoldSecondary,
          bgColor = QuranGoldLight,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "إجمالي الحفاظ",
          value = "$totalStudents",
          icon = Icons.Default.People,
          tintColor = Color(0xFF2563EB),
          bgColor = Color(0xFFEFF6FF),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Search Box
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث باسم الحلقة أو اسم المعلم أو الجامع...") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = QuranEmeraldPrimary)
          },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("halaqat_search_field"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = QuranEmeraldPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          )
        )

        // Time Filters Row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(timeFilters) { filter ->
            val isSelected = selectedTimeFilter == filter
            FilterChip(
              selected = isSelected,
              onClick = { selectedTimeFilter = filter },
              label = { Text(filter, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuranEmeraldPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // Halaqat List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "حلقات القرآن والمعلمين (${filteredHalaqat.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Halaqat Items List
    if (filteredHalaqat.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "لا توجد حلقات تطابق بحثك",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "جرب البحث باسم معلم آخر أو اختر وقتاً مختلفاً",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(filteredHalaqat, key = { it.id }) { halaqah ->
        HalaqahCard(
          halaqah = halaqah,
          onContactTeacher = {
            val message = "السلام عليكم ورحمة الله، أود الاستفسار عن التسجيل في ${halaqah.name} تحت إشراف ${halaqah.teacherName}."
            ShareHelper.shareViaWhatsApp(context, halaqah.teacherPhone, message)
          },
          onViewStudents = {
            viewModel.setSelectedGroup(halaqah.name)
            viewModel.setRole(UserRole.TEACHER)
          },
          onCardClick = {
            selectedHalaqahForDetails = halaqah
          }
        )
      }
    }
  }

  // Details Dialog
  selectedHalaqahForDetails?.let { halaqah ->
    HalaqahDetailsDialog(
      halaqah = halaqah,
      onDismiss = { selectedHalaqahForDetails = null },
      onContact = {
        val message = "السلام عليكم ورحمة الله وبركاته يا فضيلة ${halaqah.teacherName}، أود الاستفسار عن الانضمام إلى ${halaqah.name}."
        ShareHelper.shareViaWhatsApp(context, halaqah.teacherPhone, message)
        selectedHalaqahForDetails = null
      }
    )
  }
}

@Composable
fun HalaqahCard(
  halaqah: QuranHalaqah,
  onContactTeacher: () -> Unit,
  onViewStudents: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick)
      .testTag("halaqah_card_${halaqah.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Circle name & Target Level Badge
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
              .background(Color(halaqah.colorHex).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = Color(halaqah.colorHex),
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = halaqah.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = QuranEmeraldPrimary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = halaqah.teacherName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = QuranEmeraldPrimary,
                fontSize = 13.sp
              )
            }
          }
        }

        // Target Level Tag
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = QuranGoldLight)
        ) {
          Text(
            text = halaqah.targetLevel,
            color = Color(0xFF6B4E00),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Location & Time Slot Details Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(QuranSurfaceVariant.copy(alpha = 0.5f))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = halaqah.location,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 12.sp
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccessTime,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${halaqah.timeSlot} • ${halaqah.days}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 12.sp
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = null,
              tint = QuranEmeraldPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "المنهج: ${halaqah.currentCurriculum}",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium,
              color = QuranEmeraldDark,
              fontSize = 12.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Student capacity progress
      val capacityProgress = (halaqah.studentCount.toFloat() / halaqah.capacity.toFloat()).coerceIn(0f, 1f)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "المقاعد المسجلة بالحلقة:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
        Text(
          text = "${halaqah.studentCount} / ${halaqah.capacity} طالب",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = if (halaqah.studentCount >= halaqah.capacity) Color(0xFFDC2626) else QuranEmeraldPrimary,
          fontSize = 12.sp
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { capacityProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (halaqah.studentCount >= halaqah.capacity) Color(0xFFDC2626) else QuranEmeraldPrimary,
        trackColor = QuranEmeraldLight
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onContactTeacher,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("contact_teacher_btn_${halaqah.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "تواصل مع المعلم", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onViewStudents,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("view_students_btn_${halaqah.id}"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(imageVector = Icons.Default.People, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "طلاب الحلقة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun HalaqahDetailsDialog(
  halaqah: QuranHalaqah,
  onDismiss: () -> Unit,
  onContact: () -> Unit
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
          text = halaqah.name,
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
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(QuranEmeraldLight)
            .padding(12.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "المعلم والمحفظ: ${halaqah.teacherName}",
              fontWeight = FontWeight.Bold,
              color = QuranEmeraldDark,
              fontSize = 14.sp
            )
            Text(
              text = halaqah.teacherTitle,
              color = QuranEmeraldDark.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
            Text(
              text = "رقم التواصل: ${halaqah.teacherPhone}",
              color = QuranEmeraldPrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(text = "📍 المكان: ${halaqah.location}", fontSize = 13.sp)
          Text(text = "⏰ الأوقات: ${halaqah.timeSlot}", fontSize = 13.sp)
          Text(text = "📅 الأيام: ${halaqah.days}", fontSize = 13.sp)
          Text(text = "🎯 المستوى المستهدف: ${halaqah.targetLevel}", fontSize = 13.sp)
          Text(text = "📖 المنهج المعتمد: ${halaqah.currentCurriculum}", fontSize = 13.sp)
          Text(text = "👥 سعة الحلقة: ${halaqah.studentCount} / ${halaqah.capacity} طالب", fontSize = 13.sp)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onContact,
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "مراسلة عبر واتساب", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إغلاق")
      }
    }
  )
}
