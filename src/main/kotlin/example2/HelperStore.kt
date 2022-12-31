package example2

import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.core.store.Store
import example2.HelperStore.State
import example2.HelperStore.Intent


interface HelperStore: Store<Intent, State, Nothing>  {
    sealed class Intent : JvmSerializable {
        data class UpdateRealMean(val realMean: Double) : Intent()
        data class UpdateStudentId(val studentId: String): Intent()
    }

    data class State(
        val studentId: String = "",
        val realMean: Double? = null,
        val state: String = ""
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.
}