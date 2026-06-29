//Сделать ресайклер. По нажатию на элемент передавать его позицию во фрагмент. и во фрагменте этот номер отображать в тосте.

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject

/*
В адаптере я создал PublishSubject и при клике на элемент передаю в него позицию через onNext().
Во фрагменте я подписываюсь на этот поток и показываю полученную позицию в Toast.
*/

class Task3_3Fragment : Fragment() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerView = RecyclerView(requireContext())
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            "Item 0",
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4"
        )

        adapter = ItemsAdapter(items)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val disposable = adapter.itemClicks
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { position ->
                Toast.makeText(
                    requireContext(),
                    "Clicked position: $position",
                    Toast.LENGTH_SHORT
                ).show()
            }

        compositeDisposable.add(disposable)
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    private class ItemsAdapter(
        private val items: List<String>
    ) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

        private val itemClickSubject = PublishSubject.create<Int>()

        val itemClicks: Observable<Int>
            get() = itemClickSubject.hide()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val textView = TextView(parent.context).apply {
                textSize = 20f
                setPadding(32, 32, 32, 32)
            }

            return ItemViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ItemViewHolder(
            private val textView: TextView
        ) : RecyclerView.ViewHolder(textView) {

            init {
                textView.setOnClickListener {
                    val position = bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        itemClickSubject.onNext(position)
                    }
                }
            }

            fun bind(item: String) {
                textView.text = item
            }
        }
    }
}
