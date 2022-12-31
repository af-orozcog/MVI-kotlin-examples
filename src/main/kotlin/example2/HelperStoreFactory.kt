package example2

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunction.TimeTravelFunction
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunctionlist.TimeTravelFunctionList
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelparametersignature.TimeTravelParameterSignature
import example2.HelperStore.State
import example2.HelperStore.Intent
import example2.HypotheticalStore


class HelperStoreFactory(private val storeFactory: StoreFactory, private val hypotheticalStore: HypotheticalStore) {
    fun create(): HelperStore {
        val ans = object : HelperStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "SampleStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
            exposedFunctionsSignature = TimeTravelFunctionList(listOf(
                TimeTravelFunction("updateTheMean","update the mean of the student",listOf(
                TimeTravelParameterSignature("the new Mean","String")))
            )),
            exposedFunctions = emptyMap()
        ) {}
        fun updateMean(arguments:List<Any>){
            ans.accept(Intent.UpdateRealMean((arguments[0] as String).toDouble()))
        }
        ans.exposedFunctions = mapOf("updateTheMean" to ::updateMean)
        return ans
    }

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class UpdateState(val studentId: String, val realMean:Double?, val state:String) : Msg()
        data class UpdateRealMean(val realMean:Double): Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.UpdateStudentId -> calculateAnswer(intent.studentId)
                is Intent.UpdateRealMean -> updateRealMean(intent.realMean)
            }.let {}
        }

        private fun updateRealMean(realMean: Double){
            dispatch(Msg.UpdateRealMean(realMean))
            hypotheticalStore.accept(HypotheticalStore.Intent.UpdateRealMean(realMean))
        }

        private fun calculateAnswer(studentId: String) {
            try{
                if(studentId == "1") {
                    dispatch(Msg.UpdateState(studentId, 0.8, "Succesful"))
                    hypotheticalStore.accept(HypotheticalStore.Intent.UpdateRealMean(0.8))
                }
                else if(studentId == "2") {
                    dispatch(Msg.UpdateState(studentId, 0.1, "Succesful"))
                    hypotheticalStore.accept(HypotheticalStore.Intent.UpdateRealMean(0.1))
                }
                else if(studentId == "3") {
                    dispatch(Msg.UpdateState(studentId, 0.3, "Succesful"))
                    hypotheticalStore.accept(HypotheticalStore.Intent.UpdateRealMean(0.3))
                }
                else if(studentId == "4") {
                    dispatch(Msg.UpdateState(studentId, 0.5, "Succesful"))
                    hypotheticalStore.accept(HypotheticalStore.Intent.UpdateRealMean(0.5))
                }
            }catch(e:Exception){
                dispatch(Msg.UpdateState("", null, "Failed"))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UpdateState -> copy(studentId = msg.studentId, realMean = msg.realMean, state = msg.state)
                is Msg.UpdateRealMean -> copy(realMean = msg.realMean)
            }
    }
}