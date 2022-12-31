import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.timetravel.server.TimeTravelServer
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import example1.SampleStore
import example1.SampleStoreFactory
import example2.HelperStore
import example2.HelperStoreFactory
import example2.HypotheticalStoreFactory
import example3.CounterStore
import example3.CounterStoreFactory
import example3.LoopStore
import example3.LoopStoreFactory
import javax.swing.SwingUtilities
import java.util.Scanner

fun main(args: Array<String>) {
    TimeTravelServer(runOnMainThread = { SwingUtilities.invokeLater(it) })
        .start()

    val storeFactoryInstance: StoreFactory = LoggingStoreFactory(delegate = TimeTravelStoreFactory())
    val input_scanner = Scanner(System.`in`)
    println("type the number of the example you want to run (1,2,3): ")
    var input_integer:Int = input_scanner.nextInt()
    when(input_integer){
        1 -> testExample1(storeFactoryInstance)
        2 -> testExample2(storeFactoryInstance)
        3 -> testExample3(storeFactoryInstance)
    }
}

fun testExample1(storeFactoryInstance: StoreFactory){
    val sampleStore =
        SampleStoreFactory(
            storeFactory = storeFactoryInstance,
        ).create()

    println("Time to connect the plugin")
    var input = readLine()
    sampleStore.accept(SampleStore.Intent.UpdateCourseName("Alggebra","1"))
    while(true){

    }
}

fun testExample2(storeFactoryInstance: StoreFactory){
    val hypotheticalStore =
        HypotheticalStoreFactory(
            storeFactory = storeFactoryInstance,
        ).create()

    val helperStore = HelperStoreFactory(storeFactory =  storeFactoryInstance, hypotheticalStore = hypotheticalStore).create()
    println("Time to connect the plugin")
    var input = readLine()
    helperStore.accept(HelperStore.Intent.UpdateStudentId("1"))
    while(true){

    }
}

fun testExample3(storeFactoryInstance: StoreFactory){
    val counterStore =
        CounterStoreFactory(
            storeFactory = storeFactoryInstance,
        ).create()

    val loopStore = LoopStoreFactory(storeFactory =  storeFactoryInstance, counterStore = counterStore).create()
    println("Time to connect the plugin")
    var input = readLine()
    loopStore.accept(LoopStore.Intent.Start(3,3,loopStore.state))
    while(true){

    }
}