package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

enum class AudioState {
  IDLE,
  RECORDING,
  PLAYING,
  PAUSED
}

class AudioRecordManager(private val context: Context) {
  private var mediaRecorder: MediaRecorder? = null
  private var mediaPlayer: MediaPlayer? = null
  var currentAudioPath: String? = null
    private set

  var state: AudioState = AudioState.IDLE
    private set

  var onStateChanged: ((AudioState) -> Unit)? = null

  fun startRecording(): String? {
    try {
      stopPlayback()
      val outputDir = context.cacheDir
      val outputFile = File(outputDir, "recitation_${System.currentTimeMillis()}.m4a")
      currentAudioPath = outputFile.absolutePath

      mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)
        setOutputFile(outputFile.absolutePath)
        prepare()
        start()
      }

      state = AudioState.RECORDING
      onStateChanged?.invoke(state)
      return currentAudioPath
    } catch (e: Exception) {
      Log.e("AudioRecordManager", "Error starting recording: ${e.message}")
      releaseRecorder()
      state = AudioState.IDLE
      onStateChanged?.invoke(state)
      return null
    }
  }

  fun stopRecording(): String? {
    return try {
      mediaRecorder?.apply {
        stop()
        release()
      }
      mediaRecorder = null
      state = AudioState.IDLE
      onStateChanged?.invoke(state)
      currentAudioPath
    } catch (e: Exception) {
      Log.e("AudioRecordManager", "Error stopping recording: ${e.message}")
      releaseRecorder()
      state = AudioState.IDLE
      onStateChanged?.invoke(state)
      currentAudioPath
    }
  }

  fun playAudio(pathOrUrl: String, onCompletion: () -> Unit = {}) {
    try {
      stopPlayback()
      mediaPlayer = MediaPlayer().apply {
        setDataSource(pathOrUrl)
        setOnPreparedListener {
          start()
          state = AudioState.PLAYING
          onStateChanged?.invoke(state)
        }
        setOnCompletionListener {
          state = AudioState.IDLE
          onStateChanged?.invoke(state)
          onCompletion()
        }
        setOnErrorListener { _, what, extra ->
          Log.e("AudioRecordManager", "MediaPlayer error: $what, $extra")
          state = AudioState.IDLE
          onStateChanged?.invoke(state)
          true
        }
        prepareAsync()
      }
    } catch (e: Exception) {
      Log.e("AudioRecordManager", "Error playing audio: ${e.message}")
      state = AudioState.IDLE
      onStateChanged?.invoke(state)
    }
  }

  fun stopPlayback() {
    try {
      mediaPlayer?.apply {
        if (isPlaying) stop()
        release()
      }
      mediaPlayer = null
      state = AudioState.IDLE
      onStateChanged?.invoke(state)
    } catch (e: Exception) {
      Log.e("AudioRecordManager", "Error stopping playback: ${e.message}")
    }
  }

  private fun releaseRecorder() {
    try {
      mediaRecorder?.release()
    } catch (_: Exception) {}
    mediaRecorder = null
  }

  fun release() {
    stopRecording()
    stopPlayback()
  }
}
