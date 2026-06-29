/*
Ответ:
onSubscribeThread = RxComputationThreadPool
mapThread         = RxNewThreadScheduler
flatMapThread     = RxSingleScheduler
subscribeThread   = RxCachedThreadScheduler
*/

// Какой результат будет в логе?
Observable.timer(10, TimeUnit.MILLISECONDS, Schedulers.newThread())
    .subscribeOn(Schedulers.io())
    .map {
        Log.d("HAHAHA", "mapThread = ${Thread.currentThread().name}")
    }
    .doOnSubscribe {
        Log.d("HAHAHA", "onSubscribeThread = ${Thread.currentThread().name}")
    }
    .subscribeOn(Schedulers.computation())
    .observeOn(Schedulers.single())
    .flatMap {
        Log.d("HAHAHA", "flatMapThread = ${Thread.currentThread().name}")

        Observable.just(it)
            .subscribeOn(Schedulers.io())
    }
    .subscribe {
        Log.d("HAHAHA", "subscribeThread = ${Thread.currentThread().name}")
    }

