package example2

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import example2.HypotheticalStore.Intent
import example2.HypotheticalStore.State


interface HypotheticalStore: Store<Intent, State, Nothing> {
    sealed class Intent : JvmSerializable {
        data class UpdateRealMean(val realMean: Double) : Intent()
    }

    data class State(
        val answer: String = "",
        val realMean: Double? = null
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.
}