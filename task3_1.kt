// Сделать сетевой запрос и отобразить результат на экране

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Task3_1Activity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var resultTextView: TextView
    private lateinit var loadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultTextView = TextView(this).apply {
            text = "Result will be here"
            textSize = 18f
        }

        loadButton = Button(this).apply {
            text = "Load data"
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            addView(loadButton)
            addView(resultTextView)
        }

        setContentView(container)

        loadButton.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        resultTextView.text = "Loading..."

        // запрос в сеть на IO
        val disposable = networkRequest()
            .subscribeOn(Schedulers.io())
            
            // обновление TextView на MainThread
            .observeOn(AndroidSchedulers.mainThread())
            

            .subscribe(
                { result ->
                    resultTextView.text = result
                },
                { error ->
                    resultTextView.text = "Error: ${error.message}"
                }
            )

        compositeDisposable.add(disposable)
    }

    private fun networkRequest(): Single<String> {
        return Single.fromCallable {
            Thread.sleep(1500)

            """
            Network result:
            
            id: 1
            title: RxJava task
            body: network response
            """.trimIndent()
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
