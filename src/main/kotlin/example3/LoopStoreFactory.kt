package example3

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunction.TimeTravelFunction
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunctionlist.TimeTravelFunctionList
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelparametersignature.TimeTravelParameterSignature
import example3.LoopStore.State
import example3.LoopStore.Intent

class LoopStoreFactory(val storeFactory: StoreFactory, private val counterStore: CounterStore) {
    var nextIteration: ((x:Int,y:Int) -> Unit)? = null
    fun create(): LoopStore {
        val ans = object : LoopStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "LoopStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
            exposedFunctionsSignature = TimeTravelFunctionList(listOf(
                TimeTravelFunction("updateN","update the N range of the loop",listOf(TimeTravelParameterSignature("The new N","String"))),
                TimeTravelFunction("updateM","update the M range of the loop",listOf(TimeTravelParameterSignature("The new M", "String"))),
                TimeTravelFunction("updateNAndM","update the N and M range of the loop",listOf(TimeTravelParameterSignature("The new N","String"),TimeTravelParameterSignature("The new M", "String")))
            )),
            exposedFunctions = emptyMap()
        ) {}

        fun triggerNextStep(x:Int, y:Int){
            ans.accept(Intent.UpdateActualXAndY(x,y,ans.state))
        }
        nextIteration = ::triggerNextStep

        fun updateN(arguments:List<Any>){
            ans.accept(Intent.UpdateN((arguments[0] as String).toInt(),ans.state))
        }

        fun updateM(arguments:List<Any>){
            ans.accept(Intent.UpdateM((arguments[0] as String).toInt(),ans.state))
        }

        fun updateNAndM(arguments:List<Any>){
            ans.accept(Intent.UpdateNAndM((arguments[0] as String).toInt(),(arguments[1] as String).toInt(),ans.state))
        }

        ans.exposedFunctions = mapOf("updateN" to ::updateN, "updateM" to ::updateM, "updateNAndM" to ::updateNAndM)
        return ans
    }

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class UpdateActualXandActualY(val newX: Int, val newY: Int) : Msg()
        data class UpdateN(val newN: Int) : Msg()
        data class UpdateM(val newM: Int) : Msg()
        data class UpdateNAndM(val newN: Int, val newM:Int) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.UpdateN -> updateN(intent.newN, intent.state)
                is Intent.UpdateM -> updateM(intent.newM,intent.state)
                is Intent.Start -> start(intent.newN, intent.newM, intent.state)
                is Intent.UpdateNAndM -> start(intent.newN, intent.newM, intent.state)
                is Intent.UpdateActualXAndY -> updateActualXAndY(intent.newX, intent.newY, intent.state)
            }.let {}
        }

        private fun updateN(newN: Int, state: State){
            dispatch(Msg.UpdateN(newN))
            nextIteration?.let { it(state.actualX, state.actualY) }
        }

        private fun updateM(newM: Int, state: State){
            dispatch(Msg.UpdateM(newM))
            nextIteration?.let { it(state.actualX, state.actualY) }
        }

        private fun start(newN: Int, newM: Int, state: State){
            dispatch(Msg.UpdateNAndM(newN,newM))
            nextIteration?.let { it(state.actualX, state.actualY) }
        }

        private fun updateActualXAndY(newX: Int, newY: Int, state: State){
            counterStore.accept(CounterStore.Intent.AddValueAt(newX,newY,state.values,counterStore.state))
            dispatch(Msg.UpdateActualXandActualY(newX,newY))
            var newNewX = newX
            var newNewY = newY + 1
            if(newNewY >= state.M){
                newNewX += 1
                newNewY = 0
            }
            if(newNewX < state.N && newNewY < state.M){
                nextIteration?.let { it(newNewX, newNewY) }
            }
        }

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UpdateActualXandActualY -> {
                    copy(actualX = msg.newX, actualY = msg.newY)
                }

                is Msg.UpdateN -> {
                    copy(N = msg.newN)
                }

                is Msg.UpdateM -> {
                    copy(M = msg.newM)
                }

                is Msg.UpdateNAndM -> {
                    copy(N = msg.newN, M = msg.newM)
                }
            }
    }
}