// При наборе текста выводить в лог содержимое EditText всегда, когда пользователь 3 секунды что-то не вводил

/*
Что сделал:
1. Создал EditText.
2. Создал PublishSubject<String> для событий изменения текста.
3. В TextWatcher при каждом вводе отправляю текст через onNext().
4. Через debounce(3, TimeUnit.SECONDS) пропускаю дальше только последнее значение,
   если пользователь 3 секунды ничего не вводил.
5. В subscribe вывожу итоговый текст в Log.
6. В onDestroy очищаю подписку через CompositeDisposable.
*/

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class Task3_4Activity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var editText: EditText

    private val textChangesSubject = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editText = EditText(this).apply {
            hint = "Type something..."
            textSize = 18f
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            addView(editText)
        }

        setContentView(container)

        setupTextWatcher()
        observeTextChanges()
    }

    private fun setupTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                textChangesSubject.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun observeTextChanges() {
        val disposable = textChangesSubject
            .debounce(3, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { text ->
                    Log.d("TASK_3_4", "User stopped typing. Text = $text")
                },
                { error ->
                    Log.e("TASK_3_4", "Error: ${error.message}", error)
                }
            )

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
Лог:
User stopped typing. Text = Кот
*/
