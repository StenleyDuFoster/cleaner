package com.stenleone.stanleysfilm.util.extencial

import android.view.View
import androidx.annotation.CheckResult
import com.stenleone.clenner.util.constans.BindingConstant.SMALL_THROTTLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive

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
