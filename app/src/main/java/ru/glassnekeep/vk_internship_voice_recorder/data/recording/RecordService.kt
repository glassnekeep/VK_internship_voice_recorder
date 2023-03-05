package ru.glassnekeep.vk_internship_voice_recorder.data.recording

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import ru.glassnekeep.vk_internship_voice_recorder.R
import ru.glassnekeep.vk_internship_voice_recorder.RecordApplication
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.data.RecordRepository
import ru.glassnekeep.vk_internship_voice_recorder.presentation.MainActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecordService: Service() {

    private var fileName: String? = null
    private var filePath: String? = null

    private var mediaRecorder: MediaRecorder? = null

    private var startingTimeMillis: Long = 0
    private var ellapsedTimeMillis: Long = 0

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    @Inject
    lateinit var repository: RecordRepository;

    private val recordServiceComponent: RecordServiceComponent by lazy {
        initRecordServiceComponent()
    }

    private fun initRecordServiceComponent(): RecordServiceComponent {
        return (application as RecordApplication).appComponent.recordServiceFactory().create()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        recordServiceComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_NOT_STICKY
    }

    private fun startRecording() {
        setFileNameAndPath()
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setOutputFile(filePath)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioChannels(1)
        mediaRecorder?.setAudioEncodingBitRate(192000)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: IOException) {
            Log.e("RECORD_SERVICE", "Не удалось подготовить запись")
        }
    }

    private fun createNotification(): Notification? {
        val mBuilder: NotificationCompat.Builder
                = NotificationCompat.Builder(applicationContext, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_baseline_mic_none_32)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_recording))
            .setOngoing(true)
        mBuilder.setContentIntent(
            PendingIntent.getActivities(
                applicationContext, 0, arrayOf(
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                ), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        return mBuilder.build()
    }

    private fun setFileNameAndPath() {
        var count = 0
        var file: File
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(System.currentTimeMillis())
        do {
            fileName = getString(R.string.default_file_name) + "_" + dateTime + count + ".mp4"
            filePath = application.getExternalFilesDir(null)?.absolutePath + "$fileName"
            count++
            file = File(filePath)
        } while (file.exists() && !file.isDirectory)
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        ellapsedTimeMillis = System.currentTimeMillis() - startingTimeMillis
        mediaRecorder?.release()
        Toast.makeText(this,
            getString(R.string.recording_finished),
            Toast.LENGTH_SHORT
        ).show()
        val record = Record(id = 0, name = fileName.toString(), length = ellapsedTimeMillis, time = System.currentTimeMillis(), filePath = filePath.toString())
        mediaRecorder = null
        try {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    repository.insertRecord(record)
                }
            }
        } catch (exception: Exception) {
            Log.e("RECORD_SERVICE", "error adding new record", exception)
        }
    }

    override fun onDestroy() {
        if (mediaRecorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }

}