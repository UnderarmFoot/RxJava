// Какой результат будет в логе?
val subject = PublishSubject.create<String>()

subject.onNext("1")
subject.onNext("2")
subject.onNext("3")

subject.subscribe { Log.d("TAG", it) }

/*
Лог:
ничего

причина:
PublishSubject не хранит старые значения.
Он отдаёт только тем подписчикам, которые уже были подписаны в момент onNext.
*/

// 2 варианта, чтобы все вывелось

// подписаться до onNext
val subject = PublishSubject.create<String>()

subject.subscribe { Log.d("TAG", it) }

subject.onNext("1")
subject.onNext("2")
subject.onNext("3")

// использовать ReplaySubject
val subject = ReplaySubject.create<String>() // или createWithSize<String>(3)

subject.onNext("1")
subject.onNext("2")
subject.onNext("3")

subject.subscribe { Log.d("TAG", it) }
