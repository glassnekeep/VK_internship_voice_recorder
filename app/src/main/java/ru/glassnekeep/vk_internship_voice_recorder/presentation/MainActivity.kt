package ru.glassnekeep.vk_internship_voice_recorder.presentation

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import ru.glassnekeep.vk_internship_voice_recorder.R
import ru.glassnekeep.vk_internship_voice_recorder.RecordApplication
import ru.glassnekeep.vk_internship_voice_recorder.di.activity.ActivityComponent
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.RecordListFragment

class MainActivity : AppCompatActivity() {

    val activityComponent: ActivityComponent by lazy {
        initActivityComponent()
    }

    private fun initActivityComponent(): ActivityComponent {
        return (application as RecordApplication).appComponent.activityComponentFactory().create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container, RecordListFragment())
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    fun isServiceRunning(): Boolean {
        val  manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("ru.glassnekeep.vk_internship_voice_recorder.data.recording.RecordService" == service.service.className) {
                return true
            }
        }
        return false
    }
}