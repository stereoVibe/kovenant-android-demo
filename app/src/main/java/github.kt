package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.ListActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.gson.GsonBuilder
import nl.mplatvoet.komponents.kovenant.android.demo.support.GithubDeserializer
import nl.mplatvoet.komponents.kovenant.android.failUi
import nl.mplatvoet.komponents.kovenant.android.successUi
import nl.mplatvoet.komponents.kovenant.combine.and
import nl.mplatvoet.komponents.kovenant.properties.lazyPromise
import nl.mplatvoet.komponents.kovenant.then
import nl.mplatvoet.komponents.kovenant.thenUse
import org.jetbrains.anko.toast


val url = "https://api.github.com/search/repositories?q=android+language:kotlin&sort=updated&order=desc"

val gsonParser by lazyPromise {
    GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(javaClass<List<String>>(), GithubDeserializer())
            .create()
}

val httpGetService by lazyPromise { HttpGetService() }

public class GithubActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        httpGetService thenUse {
            textUrl(url)
        } and gsonParser then { tuple ->
            val (msg, parser) = tuple
            parser.fromJson(msg, javaClass<List<String>>())
        } successUi {
            val adapter = ArrayAdapter(this, R.layout.list_item, it)
            setListAdapter(adapter)
        } failUi {
            toast("${it.getMessage()}")
        }
    }
}

