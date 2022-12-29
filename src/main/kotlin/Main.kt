import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.timetravel.server.TimeTravelServer
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import example1.SampleStore
import example1.SampleStoreFactory
import kotlinx.coroutines.Dispatchers
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    println("Hello World!")
    TimeTravelServer(runOnMainThread = { SwingUtilities.invokeLater(it) })
        .start()

    val storeFactoryInstance: StoreFactory = LoggingStoreFactory(delegate = TimeTravelStoreFactory())
    val listStore =
        SampleStoreFactory(
                storeFactory = storeFactoryInstance,
        ).create()

    println("Time to connect the plugin")
    var input = readLine()
    listStore.accept(SampleStore.Intent.UpdateCourseName("Alggebra","1"))

    while(true){

    }
}