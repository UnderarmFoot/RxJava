// Есть 2 сервера на которых лежат скидочные карты. Нужно получить эти данные и вывести в единый список.


/*
Что сделал:
- Создал два моковых серверных запроса.
- Каждый сервер возвращает Single<List<DiscountCard>>.
- Через Single.zip() жду оба результата.
- Объединяю два списка в один.
- Через distinctBy { id } убираю дубликаты карт.
- subscribeOn(IO) - запросы выполняются в фоне.
- observeOn(MainThread) - результат показываю в TextView.
- В onDestroy очищаю подписку через CompositeDisposable.
*/

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Task3_5Activity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultTextView = TextView(this).apply {
            textSize = 18f
            text = "Loading discount cards..."
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            addView(resultTextView)
        }

        setContentView(container)

        loadDiscountCards()
    }

    private fun loadDiscountCards() {
        val disposable = Single.zip(
            getCardsFromFirstServer(),
            getCardsFromSecondServer()
        ) { firstServerCards, secondServerCards ->

            (firstServerCards + secondServerCards)
                .distinctBy { card -> card.id }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { cards ->
                    resultTextView.text = cards.joinToString(separator = "\n") { card ->
                        "${card.id}. ${card.title}"
                    }

                    Log.d("TASK_3_5", "Cards: $cards")
                },
                { error ->
                    resultTextView.text = "Error: ${error.message}"
                    Log.e("TASK_3_5", "Error", error)
                }
            )

        compositeDisposable.add(disposable)
    }

    private fun getCardsFromFirstServer(): Single<List<DiscountCard>> {
        return Single.fromCallable {
            Thread.sleep(1000)

            listOf(
                DiscountCard(id = 1, title = "Lenta card"),
                DiscountCard(id = 2, title = "Magnit card")
            )
        }
    }

    private fun getCardsFromSecondServer(): Single<List<DiscountCard>> {
        return Single.fromCallable {
            Thread.sleep(1500)

            listOf(
                DiscountCard(id = 2, title = "Magnit card"),
                DiscountCard(id = 3, title = "Ozon card")
            )
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}

data class DiscountCard(
    val id: Int,
    val title: String
)
