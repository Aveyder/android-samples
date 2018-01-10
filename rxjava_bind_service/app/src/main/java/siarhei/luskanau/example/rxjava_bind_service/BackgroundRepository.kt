package siarhei.luskanau.example.rxjava_bind_service

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class BackgroundRepository {

    fun getStrings(): Observable<String> = Observable
            .intervalRange(
                    1,
                    5,
                    0,
                    TimeUnit.SECONDS.toMillis(1),
                    TimeUnit.MILLISECONDS
            )
            .map { i -> "string%s".format(i) }

}