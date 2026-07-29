package com.echojournal.app.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

/**
 * Simple wrapper around MediaRecorder for recording voice notes to an .m4a file.
 */
class AudioRecorderManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTimeMs: Long = 0

    fun start(): File {
        val file = FileUtils.newAudioFile(context)
        outputFile = file

        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        recorder = mediaRecorder
        startTimeMs = System.currentTimeMillis()
        return file
    }

    /** Stops recording and returns the resulting file + duration in ms, or null if nothing was recording. */
    fun stop(): Pair<File, Long>? {
        val mediaRecorder = recorder ?: return null
        return try {
            mediaRecorder.stop()
            mediaRecorder.release()
            recorder = null
            val duration = System.currentTimeMillis() - startTimeMs
            val file = outputFile
            if (file != null) Pair(file, duration) else null
        } catch (e: Exception) {
            mediaRecorder.release()
            recorder = null
            null
        }
    }

    fun cancel() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
            // ignore
        }
        recorder = null
        outputFile?.let { if (it.exists()) it.delete() }
        outputFile = null
    }

    fun isRecording(): Boolean = recorder != null
}
