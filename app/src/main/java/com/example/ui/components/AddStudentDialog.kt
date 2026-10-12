package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranData
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(
  initialStudent: Student? = null,
  onDismiss: () -> Unit,
  onSave: (Student) -> Unit
) {
  var name by remember { mutableStateOf(initialStudent?.name ?: "") }
  var phone by remember { mutableStateOf(initialStudent?.phone ?: "") }
  var parentName by remember { mutableStateOf(initialStudent?.parentName ?: "") }
  var parentPhone by remember { mutableStateOf(initialStudent?.parentPhone ?: "") }
  var groupName by remember { mutableStateOf(initialStudent?.groupName ?: "حلقة عثمان بن عفان") }
  var level by remember { mutableStateOf(initialStudent?.level ?: "متوسط") }
  var currentSurah by remember { mutableStateOf(initialStudent?.currentSurah ?: "البقرة") }
  var currentAyah by remember { mutableIntStateOf(initialStudent?.currentAyah ?: 1) }
  var memorizedJuz by remember { mutableIntStateOf(initialStudent?.memorizedJuzCount ?: 1) }

  var isSurahExpanded by remember { mutableStateOf(false) }
  var isGroupExpanded by remember { mutableStateOf(false) }
  var isLevelExpanded by remember { mutableStateOf(false) }

  val groups = listOf("حلقة عثمان بن عفان", "حلقة علي بن أبي طالب", "حلقة أبي بكر الصديق", "حلقة عمر بن الخطاب")
  val levels = listOf("مبتدئ", "متوسط", "متقدم", "خاتم")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (initialStudent == null) "إضافة طالب جديد" else "تعديل بيانات الطالب",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم الطالب الرباعي *") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("student_name_input"),
          singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("هاتف الطالب") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = parentPhone,
            onValueChange = { parentPhone = it },
            label = { Text("هاتف ولي الأمر *") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        OutlinedTextField(
          value = parentName,
          onValueChange = { parentName = it },
          label = { Text("اسم ولي الأمر") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // Group Selection
        ExposedDropdownMenuBox(
          expanded = isGroupExpanded,
          onExpandedChange = { isGroupExpanded = !isGroupExpanded }
        ) {
          OutlinedTextField(
            value = groupName,
            onValueChange = {},
            readOnly = true,
            label = { Text("الحلقة القرآنية") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGroupExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isGroupExpanded,
            onDismissRequest = { isGroupExpanded = false }
          ) {
            groups.forEach { group ->
              DropdownMenuItem(
                text = { Text(group) },
                onClick = {
                  groupName = group
                  isGroupExpanded = false
                }
              )
            }
          }
        }

        // Level Selection
        ExposedDropdownMenuBox(
          expanded = isLevelExpanded,
          onExpandedChange = { isLevelExpanded = !isLevelExpanded }
        ) {
          OutlinedTextField(
            value = level,
            onValueChange = {},
            readOnly = true,
            label = { Text("المستوى القرآني") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLevelExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isLevelExpanded,
            onDismissRequest = { isLevelExpanded = false }
          ) {
            levels.forEach { lvl ->
              DropdownMenuItem(
                text = { Text(lvl) },
                onClick = {
                  level = lvl
                  isLevelExpanded = false
                }
              )
            }
          }
        }

        // Current Surah Selection
        ExposedDropdownMenuBox(
          expanded = isSurahExpanded,
          onExpandedChange = { isSurahExpanded = !isSurahExpanded }
        ) {
          OutlinedTextField(
            value = currentSurah,
            onValueChange = {},
            readOnly = true,
            label = { Text("السورة الحالية") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSurahExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = isSurahExpanded,
            onDismissRequest = { isSurahExpanded = false }
          ) {
            QuranData.surahs.forEach { s ->
              DropdownMenuItem(
                text = { Text("${s.number}. سورة ${s.name} (${s.ayahCount} آية)") },
                onClick = {
                  currentSurah = s.name
                  isSurahExpanded = false
                }
              )
            }
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = currentAyah.toString(),
            onValueChange = { currentAyah = it.toIntOrNull() ?: 1 },
            label = { Text("رقم الآية") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = memorizedJuz.toString(),
            onValueChange = { memorizedJuz = it.toIntOrNull() ?: 0 },
            label = { Text("الأجزاء المحفوظة") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val studentToSave = (initialStudent ?: Student(name = name)).copy(
              name = name,
              phone = phone,
              parentName = parentName,
              parentPhone = parentPhone,
              groupName = groupName,
              level = level,
              currentSurah = currentSurah,
              currentAyah = currentAyah,
              memorizedJuzCount = memorizedJuz
            )
            onSave(studentToSave)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_student_btn")
      ) {
        Text("حفظ الطالب", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
