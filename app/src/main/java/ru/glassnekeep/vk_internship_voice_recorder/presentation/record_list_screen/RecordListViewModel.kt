package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.data.RecordRepository
import ru.glassnekeep.vk_internship_voice_recorder.di.app.AppContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecordListViewModel @Inject constructor(
    @AppContext context: Context,
    private val preferences: SharedPreferences,
    private val repository: RecordRepository
): AndroidViewModel(context as Application), DefaultLifecycleObserver {

    private val TRIGGER_TIME = "TRIGGER_AT"
    private val second: Long = 1_000L
    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> get() = _elapsedTime

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> get() = _player

    private val _records = MutableLiveData<List<Record>>()
    val records: LiveData<List<Record>> get() = _records

    private lateinit var timer: CountDownTimer
    private var contentPosition = 0L
    private var playWhenReady = true
    var filePath: String? = null

    init {
        getRecords()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createTimer()
    }

    override fun onStart(owner: LifecycleOwner) {
        if (filePath != null) setUpPlayer()
    }

    override fun onStop(owner: LifecycleOwner) {
        releasePlayer()
    }

    private fun getRecords() {
        viewModelScope.launch {
            repository.getRecords().collect {
                _records.value = it
            }
        }
    }

    private fun timeFormatter(time: Long): String {
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time)%60,
            TimeUnit.MILLISECONDS.toMinutes(time)%60,
            TimeUnit.MILLISECONDS.toSeconds(time)%60)
    }

    fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        resetTimer()
    }

    fun startTimer() {
        val triggerTime = SystemClock.elapsedRealtime()

        viewModelScope.launch {
            saveTime(triggerTime)
            createTimer()
        }
    }

    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = timeFormatter(SystemClock.elapsedRealtime() - triggerTime)
                }
                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }

    fun resetTimer() {
        _elapsedTime.value = timeFormatter(0)
        viewModelScope.launch { saveTime(0) }
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            preferences.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            preferences.getLong(TRIGGER_TIME,0)
        }

    fun play() {
        setUpPlayer()
    }

    private fun setUpPlayer() {
        if (player.value != null && player.value!!.playbackState != Player.STATE_ENDED) player.value!!.stop()
        val dataSourceFactory = DefaultDataSourceFactory(
            getApplication(),
            Util.getUserAgent(getApplication(), "recorder")
        )

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(filePath)))

        val player = ExoPlayer.Builder(getApplication()).build()
        player.prepare(mediaSource)
        player.playWhenReady = playWhenReady
        player.seekTo(contentPosition)
        val listener = object: Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _records.value = _records.value
                }
            }
        }
        player.addListener(listener)
        _player.value = player
    }

    private fun releasePlayer() {
        val player = _player.value ?: return
        this._player.value = null
        contentPosition = player.contentPosition
        playWhenReady = player.playWhenReady
        player.release()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }
}