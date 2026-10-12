package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppTopBar
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.circles.CirclesScreen
import com.example.ui.screens.developer.DeveloperScreen
import com.example.ui.screens.parent.ParentScreen
import com.example.ui.screens.qiraat.TenRecitationsScreen
import com.example.ui.screens.student.StudentScreen
import com.example.ui.screens.supervisor.GeneralSupervisorScreen
import com.example.ui.screens.teacher.TeacherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UserRole

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          val viewModel: MainViewModel = viewModel()
          val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
          val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
          val authenticatedRole by viewModel.authenticatedRole.collectAsStateWithLifecycle()
          val syncState by viewModel.syncState.collectAsStateWithLifecycle()

          Crossfade(
            targetState = isLoggedIn,
            label = "auth_screen_transition"
          ) { loggedIn ->
            if (!loggedIn) {
              LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {}
              )
            } else {
              Scaffold(
                topBar = {
                  AppTopBar(
                    currentRole = currentRole,
                    authenticatedRole = authenticatedRole,
                    syncState = syncState,
                    onSyncClick = { viewModel.performSync() },
                    onRoleSelected = { viewModel.setRole(it) },
                    onLogoutClick = { viewModel.logout() }
                  )
                },
                modifier = Modifier.fillMaxSize()
              ) { innerPadding ->
                Crossfade(
                  targetState = currentRole,
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                  label = "role_screen_transition"
                ) { role ->
                  when (role) {
                    UserRole.SUPERVISOR -> GeneralSupervisorScreen(
                      viewModel = viewModel,
                      onInspectRole = { viewModel.setRole(it) }
                    )
                    UserRole.TEACHER -> TeacherScreen(viewModel = viewModel)
                    UserRole.CIRCLES -> CirclesScreen(viewModel = viewModel)
                    UserRole.QIRAAT -> TenRecitationsScreen(
                      viewModel = viewModel,
                      onEvaluateInQiraah = {
                        viewModel.setRole(UserRole.TEACHER)
                      }
                    )
                    UserRole.STUDENT -> StudentScreen(viewModel = viewModel)
                    UserRole.PARENT -> ParentScreen(viewModel = viewModel)
                    UserRole.DEVELOPER -> DeveloperScreen(viewModel = viewModel)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
