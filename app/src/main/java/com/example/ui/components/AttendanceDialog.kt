package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SampleDataProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.Student
import com.example.ui.theme.QuranEmeraldLight
import com.example.ui.theme.QuranEmeraldPrimary
import com.example.ui.theme.QuranError
import com.example.ui.theme.QuranErrorLight
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranGoldSecondary
import com.example.ui.theme.QuranSuccess
import com.example.ui.theme.QuranSuccessLight
import com.example.ui.theme.QuranWarning
import com.example.ui.theme.QuranWarningLight

@Composable
fun AttendanceDialog(
  student: Student,
  onDismiss: () -> Unit,
  onSave: (AttendanceRecord) -> Unit
) {
  var selectedStatus by remember { mutableStateOf("حاضر") }
  var notes by remember { mutableStateOf("") }

  val statuses = listOf(
    "حاضر" to (QuranSuccessLight to QuranSuccess),
    "متأخر" to (QuranWarningLight to QuranWarning),
    "مستأذن" to (QuranGoldLight to QuranGoldSecondary),
    "غائب" to (QuranErrorLight to QuranError)
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = "تسجيل الحضور والغياب",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "الطالب: ${student.name}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "اختر الحالة لليوم (${SampleDataProvider.todayDate}):",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          statuses.forEach { (status, colors) ->
            val isSelected = selectedStatus == status
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) colors.second else colors.first)
                .clickable { selectedStatus = status }
                .padding(vertical = 10.dp)
                .testTag("status_option_$status"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = status,
                color = if (isSelected) Color.White else colors.second,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("ملاحظات الحضور (اختياري)") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val record = AttendanceRecord(
            studentId = student.id,
            studentName = student.name,
            date = SampleDataProvider.todayDate,
            status = selectedStatus,
            notes = notes
          )
          onSave(record)
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuranEmeraldPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_attendance_btn")
      ) {
        Text("تأكيد الحضور", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
