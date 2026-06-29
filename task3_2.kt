// TextView которая раз в секунду меняется

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class Task3_2Activity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var timerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timerTextView = TextView(this).apply {
            text = "Timer: 0"
            textSize = 24f
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            addView(timerTextView)
        }

        setContentView(container)

        startTimer()
    }

    private fun startTimer() {
        val disposable = Observable
            .interval(0, 1, TimeUnit.SECONDS)
            // 0  -> отдается сразу
            // 1s -> дальше каждую секунду

            .observeOn(AndroidSchedulers.mainThread())
            // interval по умолчанию работает на computation thread
            // TextView можно менять только на main thread

            .subscribe(
                { seconds ->
                    timerTextView.text = "Timer: $seconds"
                },
                { error ->
                    timerTextView.text = "Error: ${error.message}"
                }
            )

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
