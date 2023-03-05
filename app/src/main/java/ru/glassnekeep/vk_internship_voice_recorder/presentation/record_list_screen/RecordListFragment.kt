package ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.glassnekeep.vk_internship_voice_recorder.R
import ru.glassnekeep.vk_internship_voice_recorder.data.Record
import ru.glassnekeep.vk_internship_voice_recorder.data.recording.RecordService
import ru.glassnekeep.vk_internship_voice_recorder.databinding.FragmentRecordBinding
import ru.glassnekeep.vk_internship_voice_recorder.presentation.MainActivity
import ru.glassnekeep.vk_internship_voice_recorder.presentation.record_list_screen.di.RecordListComponent
import java.io.File
import javax.inject.Inject

class RecordListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var recordAdapter: RecordAdapter

    private var _binding: FragmentRecordBinding? = null

    private val binding get() = _binding!!

    private val PERMISSION_RECORD_AUDIO = 123

    private val viewModel by viewModels<RecordListViewModel> { viewModelFactory }

    private val recordListComponent: RecordListComponent by lazy {
        initRecordListComponent()
    }

    private fun initRecordListComponent(): RecordListComponent {
        return (requireActivity() as MainActivity).activityComponent.recordListComponentFactory().create(requireContext()) { record -> recordClickListener(record)}
    }

    private fun recordClickListener(record: Record) {
        //TODO Тут нужно описать код вопсроизведения аудиозаписи
        viewModel.filePath = record.filePath
        viewModel.play()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        recordListComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!(activity as MainActivity).isServiceRunning()) {
            viewModel.resetTimer()
        } else {
            binding.record.setImageResource(R.drawable.ic_baseline_stop_32)
        }
        binding.record.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), PERMISSION_RECORD_AUDIO)
            } else {
                if ((activity as MainActivity).isServiceRunning()) {
                    record(false)
                    viewModel.stopTimer()
                } else {
                    record(true)
                    viewModel.startTimer()
                }
            }
        }
        binding.records.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = recordAdapter
        }
        viewModel.records.observe(viewLifecycleOwner) { list ->
            val newList = list.map { Pair(it, false) }
            val diffUtilCallback = RecordDiffUtilCallback(recordAdapter.recordList, newList)
            val differences = DiffUtil.calculateDiff(diffUtilCallback)
            recordAdapter.recordList = newList
            differences.dispatchUpdatesTo(recordAdapter)
        }
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun record(start: Boolean) {
        val intent = Intent(requireActivity(), RecordService::class.java)
        if (start) {
            binding.record.setImageResource(R.drawable.ic_baseline_stop_32)
            Snackbar.make(binding.root, getString(R.string.recording_started), Snackbar.LENGTH_SHORT).show()
            val folder = File(activity?.getExternalFilesDir(null)?.absolutePath.toString() + "/Recorder")
            if (!folder.exists()) {
                folder.mkdir()
            }
            activity?.startService(intent)
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.record.setImageResource(R.drawable.ic_baseline_mic_none_32)
            activity?.stopService(intent)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    record(true)
                    viewModel.startTimer()
                } else {
                    Snackbar.make(binding.root, getString(R.string.permission_required_for_recording), Snackbar.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}