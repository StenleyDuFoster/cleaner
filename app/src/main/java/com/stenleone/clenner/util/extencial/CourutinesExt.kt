package com.stenleone.stanleysfilm.util.extencial

import android.view.View
import androidx.annotation.CheckResult
import com.stenleone.clenner.util.constans.BindingConstan.SMALL_THROTTLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

fun <T> Flow<T>.throttleLatest(periodMillis: Long): Flow<T> {
    return channelFlow {
        var lastValue: T?
        var timer: Timer? = null
        onCompletion { timer?.cancel() }
        collect { value ->
            lastValue = value

            if (timer == null) {
                timer = Timer()
                timer?.scheduleAtFixedRate(
                    object : TimerTask() {
                        override fun run() {
                            val value = lastValue
                            lastValue = null
                            if (value != null) {
                                launch {
                                    send(value as T)
                                }
                            } else {
                                timer?.cancel()
                                timer = null
                            }
                        }
                    },
                    0,
                    periodMillis
                )
            }
        }
    }
}

fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
    require(periodMillis > 0) { "period should be positive" }
    return flow {
        var lastTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis) {
                lastTime = currentTime
                emit(value)
            }
        }
    }
}

@CheckResult
fun View.clicks(): Flow<Unit> = channelFlow {
    setOnClickListener(listener(this, ::offer))
    awaitClose { setOnClickListener(null) }
}

@CheckResult
private fun listener(
    scope: CoroutineScope,
    emitter: (Unit) -> Boolean
) = View.OnClickListener {
    if (scope.isActive) { emitter(Unit) }
}

fun View.throttleClicks(onEach: () -> Unit, scope: CoroutineScope, periodMillis: Long = SMALL_THROTTLE) {
    this.clicks()
        .throttleFirst(periodMillis)
        .onEach {
            onEach.invoke()
        }
        .launchIn(scope)
}
