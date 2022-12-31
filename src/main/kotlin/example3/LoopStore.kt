package example3

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import example3.LoopStore.Intent
import example3.LoopStore.State

interface LoopStore: Store<Intent, State, Nothing> {
    sealed class Intent : JvmSerializable {
        data class UpdateActualXAndY(val newX: Int, val newY: Int, val state:State) : Intent()
        data class UpdateN(val newN: Int,val state:State): Intent()
        data class UpdateM(val newM: Int,val state:State): Intent()
        data class UpdateXAndYAndNAndM(val newX: Int, val newY: Int, val newN: Int, val newM: Int): Intent()
        data class Start(val newN:Int, val newM:Int,val state:State) : Intent()
    }

    data class State(
        val actualX: Int = 0,
        val actualY: Int = 0,
        val N: Int = 0,
        val M: Int = 0,
        val values: List<List<Int>> = listOf(listOf(1,2),listOf(3,4))
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.
}