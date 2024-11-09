package com.leoh.youtubetranscript

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.leoh.transcript.OkHttpYoutubeClient
import io.github.thoroldvix.api.YoutubeClient
import io.github.thoroldvix.api.YoutubeTranscriptApi
import io.github.thoroldvix.internal.TranscriptApiFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
		btnGetTranscript = findViewById(R.id.btnGetTranscript)
		edtYoutubeLink.setText("https://www.youtube.com/watch?v=hBtxSzKhM44")
		edtYoutubeLink.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				val inputMethodManager = getSystemService(InputMethodManager::class.java)
				inputMethodManager.hideSoftInputFromWindow(
					edtYoutubeLink.windowToken,
					InputMethodManager.HIDE_NOT_ALWAYS,
				)
				edtYoutubeLink.clearFocus()
				btnGetTranscript.performClick()
				return@setOnEditorActionListener true
			} else {
				return@setOnEditorActionListener false
			}
		}
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
		val youtubeClient: YoutubeClient = OkHttpYoutubeClient(getOkhttpClient())

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

	private fun getOkhttpClient(): OkHttpClient {
		val cookieJar: ClearableCookieJar =
			PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(this))
		val loggingInterceptor =
			HttpLoggingInterceptor { message ->
				Log.d("OkhttpClient", message.take(1000))
			}.setLevel(HttpLoggingInterceptor.Level.BODY)
		return OkHttpClient
			.Builder()
			.cookieJar(cookieJar)
			.addInterceptor(loggingInterceptor)
			.build()
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
