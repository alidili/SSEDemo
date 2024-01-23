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
                        .url(binding.etUrl.toString().trim())
                        .build()
                    val okHttpClient = OkHttpClient.Builder().also {
                        it.connectTimeout(1, TimeUnit.DAYS)
                        it.readTimeout(1, TimeUnit.DAYS)
                    }.build()
                    val realEventSource = RealEventSource(request, object : EventSourceListener() {
                        override fun onOpen(eventSource: EventSource, response: Response) {
                            super.onOpen(eventSource, response)
                            binding.tvMessage.append("已连接 \n")
                        }

                        override fun onEvent(
                            eventSource: EventSource,
                            id: String?,
                            type: String?,
                            data: String
                        ) {
                            super.onEvent(eventSource, id, type, data)
                            binding.tvMessage.append("收到消息: $data \n")
                        }

                        override fun onClosed(eventSource: EventSource) {
                            super.onClosed(eventSource)
                            binding.tvMessage.append("已断开 \n")
                        }

                        override fun onFailure(
                            eventSource: EventSource,
                            t: Throwable?,
                            response: Response?
                        ) {
                            super.onFailure(eventSource, t, response)
                            binding.tvMessage.append("连接失败 ${t?.message} \n")
                        }
                    })

                    realEventSource.connect(okHttpClient)
                }
            }.start()
        }
    }
}