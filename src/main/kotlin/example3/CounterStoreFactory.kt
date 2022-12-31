package example3

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunctionlist.TimeTravelFunctionList
import example3.CounterStore.Intent
import example3.CounterStore.State

class CounterStoreFactory(private val storeFactory: StoreFactory) {
    fun create(): CounterStore {
        val ans = object : CounterStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
            exposedFunctionsSignature = TimeTravelFunctionList(emptyList()),
            exposedFunctions = emptyMap()
        ) {}
        return ans
    }

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class UpdateState(val counter: Int, val state: String) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.AddValueAt -> calculateAnswer(intent.x, intent.y,intent.matrix, intent.state)
            }.let {}
        }

        private fun calculateAnswer(x: Int, y: Int, matrix: List<List<Int>>, state:State) {
            try{
                dispatch(Msg.UpdateState(matrix[x][y] + state.counter,"Successful") )
            }catch(e:Exception){
                dispatch(Msg.UpdateState(0,"Failed"))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UpdateState -> copy(counter = msg.counter, state = msg.state)
            }
    }
}