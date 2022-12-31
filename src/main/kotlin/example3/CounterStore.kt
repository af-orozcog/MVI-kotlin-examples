package example3

import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import example3.CounterStore.Intent
import example3.CounterStore.State
import com.arkivanov.mvikotlin.core.store.Store

interface CounterStore: Store<Intent, State, Nothing> {
    sealed class Intent : JvmSerializable {
        data class AddValueAt(val x:Int, val y:Int, val matrix: List<List<Int>>, val state:State): Intent()
    }

    data class State(
        val counter: Int = 0,
        val state: String = ""
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.
}