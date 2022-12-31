package example2

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunctionlist.TimeTravelFunctionList
import example2.HypotheticalStore.Intent
import example2.HypotheticalStore.State

class HypotheticalStoreFactory(private val storeFactory: StoreFactory) {
    fun create(): HypotheticalStore {
        val ans = object : HypotheticalStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "HypotheticalStore",
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
        data class UpdateState(val answer: String, val realMean: Double?) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.UpdateRealMean -> calculateAnswer(intent.realMean)
            }.let {}
        }

        private fun calculateAnswer(realMean: Double) {
            try{
                if(realMean < 0.2)
                    dispatch(Msg.UpdateState("bad grades",realMean))
                else if(realMean >= 0.2 && realMean < 0.8)
                    dispatch(Msg.UpdateState("average grades",realMean))
                else if(realMean >= 0.8)
                    dispatch(Msg.UpdateState("excellent grades",realMean))
            }catch(e:Exception){
                dispatch(Msg.UpdateState("error",null))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UpdateState -> copy(answer = msg.answer, realMean = msg.realMean)
            }
    }
}