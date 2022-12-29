package example1

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import example1.SampleStore.Intent
import example1.SampleStore.State


internal interface SampleStore: Store<Intent,State, Nothing> {
    sealed class Intent : JvmSerializable {
        data class UpdateCourseName(val courseName: String, val studentId: String) : Intent()
    }

    data class State(
        val answer: String = "",
        val courseName: String = "",
        val studentId: String = ""
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.
}