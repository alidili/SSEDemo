package tech.yangle.sseclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import tech.yangle.sseclient.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

/**
 * 主页
 * <p>
 * Created by YangLe on 2024/1/22.
 * Website：http://www.yangle.tech
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        binding.btnConnect.setOnClickListener {
            Thread {
                run {
                    val request = Request.Builder()
                        .url(binding.etUrl.text.toString())
                        .build()
                    val okHttpClient = OkHttpClient.Builder().also {
                        it.connectTimeout(1, TimeUnit.DAYS)
                        it.readTimeout(1, TimeUnit.DAYS)
                    }.build()
                    val realEventSource = RealEventSource(request, object : EventSourceListener() {
                        override fun onOpen(eventSource: EventSource, response: Response) {
                            super.onOpen(eventSource, response)
                            showMessage("已连接")
                        }

                        override fun onEvent(
                            eventSource: EventSource,
                            id: String?,
                            type: String?,
                            data: String
                        ) {
                            super.onEvent(eventSource, id, type, data)
                            showMessage(data)
                        }

                        override fun onClosed(eventSource: EventSource) {
                            super.onClosed(eventSource)
                            showMessage("已断开")
                        }

                        override fun onFailure(
                            eventSource: EventSource,
                            t: Throwable?,
                            response: Response?
                        ) {
                            super.onFailure(eventSource, t, response)
                            showMessage("连接失败 ${t?.message}")
                        }
                    })

                    realEventSource.connect(okHttpClient)
                }
            }.start()
        }
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            binding.tvMessage.append("$message \n")
        }
    }
}