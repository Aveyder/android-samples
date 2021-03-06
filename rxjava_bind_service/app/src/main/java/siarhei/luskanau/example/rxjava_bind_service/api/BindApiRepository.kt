package siarhei.luskanau.example.rxjava_bind_service.api

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import io.reactivex.Observable
import io.reactivex.Single
import siarhei.luskanau.example.rxjava_bind_service.sevrice.BackgroundService
import siarhei.luskanau.example.rxjava_bind_service.sevrice.RxServiceBindingFactory
import timber.log.Timber

class BindApiRepository(private val context: Context) : ApiRepository {

    override fun getStrings(): Observable<String> = bindService()
            .flatMapObservable { pair: Pair<ApiRepository, ServiceConnection> ->
                pair.first
                        .getStrings()
                        .doFinally {
                            Timber.d("doFinally and unbindService")
                            context.unbindService(pair.second)
                        }
            }

    private fun bindService(): Single<Pair<ApiRepository, ServiceConnection>> =
            RxServiceBindingFactory.bind<BackgroundService.ServiceBinder>(
                    context,
                    Intent(context, BackgroundService::class.java),
                    Context.BIND_AUTO_CREATE
            )
                    .map { pair: Pair<BackgroundService.ServiceBinder, ServiceConnection> ->
                        Pair(pair.first.getApiRepository(), pair.second)
                    }

}