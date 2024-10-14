package com.leoh.youtubetranscript

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.leoh.transcript.OkHttpYoutubeClient
import io.github.thoroldvix.api.YoutubeClient
import io.github.thoroldvix.api.YoutubeTranscriptApi
import io.github.thoroldvix.internal.TranscriptApiFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var edtYoutubeLink: EditText
    private lateinit var btnGetTranscript: Button
    private lateinit var tvContent: TextView
    private lateinit var loadingView: View
    private var executor: Executor = Executors.newCachedThreadPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edtYoutubeLink = findViewById(R.id.edtLink)
        edtYoutubeLink.setText("https://www.youtube.com/watch?v=hBtxSzKhM44")
        btnGetTranscript = findViewById(R.id.btnGetTranscript)
        tvContent = findViewById(R.id.tvContent)
        loadingView = findViewById(R.id.loadingView)
        btnGetTranscript.setOnClickListener {
            executor.execute {
                getTranscripts()
            }
        }
    }

    private fun getTranscripts() {
        runOnUiThread {
            loadingView.visibility = View.VISIBLE
        }
        val youtubeClient: YoutubeClient = OkHttpYoutubeClient()

        val youtubeTranscriptApi: YoutubeTranscriptApi =
            TranscriptApiFactory.createWithClient(youtubeClient)
        val transcriptList = youtubeTranscriptApi.listTranscripts(getVideoId()).toList()
        if (transcriptList.isNotEmpty()) {
            val transcript = transcriptList.first()
            tvContent.post {
                tvContent.text = "${transcript.language}:"
            }
            val content = transcript.fetch()
            tvContent.post {
                tvContent.text = "${transcript.language}:\n$content"
            }
        }
        tvContent.post {
            loadingView.visibility = View.GONE
        }
    }

    private fun getVideoId(): String {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(edtYoutubeLink.text)
        return if (matcher.find()) {
            matcher.group()
        } else {
            return edtYoutubeLink.text.toString()
        }
    }
}
