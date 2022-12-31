package example1

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import example1.SampleStore.State
import example1.SampleStore.Intent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunctionlist.TimeTravelFunctionList
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelparametersignature.TimeTravelParameterSignature
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelfunction.TimeTravelFunction
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SampleStoreFactory(
    private val storeFactory: StoreFactory,
) {

    fun create(): SampleStore {
        val ans = object : SampleStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "SampleStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
            exposedFunctionsSignature = TimeTravelFunctionList(listOf(TimeTravelFunction("updateCourseName","update the course name",listOf(
                TimeTravelParameterSignature("course Name","String"), TimeTravelParameterSignature("student id","String")
            )))),
            exposedFunctions = emptyMap()
        ) {}
        fun updateCourseName(arguments:List<Any>){
            ans.accept(Intent.UpdateCourseName(arguments[0] as String,arguments[1] as String))
        }
        ans.exposedFunctions = mapOf("updateCourseName" to ::updateCourseName)
        return ans
    }

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class UpdateState(val answer: String, val courseName: String, val studentId: String) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.UpdateCourseName -> calculateAnswer(intent.courseName, intent.studentId)
            }.let {}
        }

        private fun calculateGrades(grades:List<Int>):Int{
            var ans:Int = 0
            for(grade in grades){
                ans += grade
            }

            return ans/grades.size
        }

        private val evalStrategy = listOf(::calculateGrades,::calculateGrades,::calculateGrades)

        private fun getDegrees(courseName: String, studentId: String):List<Int>{
            if(courseName == "Algebra" && studentId == "1"){
                return listOf(4,5,5,4)
            }
            return emptyList()
        }

        private fun findStrategy(courseName: String):Int?{
            if (courseName == "XAlgebra") {
                return 0
            } else if (courseName == "Calculus") {
                return 1
            } else if (courseName == "CalculusX") {
                return 1
            } else if (courseName == "Physics") {
                return 2
            } else if (courseName == "Algebra") {
                return 1
            }
            return null
        }

        private fun calculateAnswer(newCourseName: String, newStudentId: String) {
            try {
                val strategyId = findStrategy(newCourseName)
                val evaluations = getDegrees(newCourseName,newStudentId)
                var mean = evalStrategy[strategyId!!](evaluations)
                dispatch(Msg.UpdateState(mean.toString(),newCourseName,newStudentId))
            }catch (e:Exception){
                dispatch(Msg.UpdateState("error",newCourseName,newStudentId))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UpdateState -> copy(answer = msg.answer, courseName = msg.courseName, studentId = msg.studentId)
            }
    }
}