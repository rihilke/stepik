package rihilke.stepik

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.Async.Schedule
import rihilke.stepik.ui.theme.StepikTheme
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
	lateinit var vList: LinearLayout
	lateinit var vListView: ListView
	lateinit var vRecView: RecyclerView
	var request: Disposable? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		Log.v("tag", "onCreate запустился")
//        vList = findViewById(R.id.act1_list)
//        vListView = findViewById(R.id.act1_listView)
		vRecView = findViewById(R.id.act1_recView)
		// url rss, пропущенный через онлайн джейсонатор
		val url_ria_novosti =
			"https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Fria.ru%2Fexport%2Frss2%2Farchive%2Findex.xml"
		val url_rambler =
			"https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Fnews.rambler.ru%2Frss%2Fworld%2F"
		val o = createRequest(url_rambler)
			.map { Gson().fromJson(it, Feed::class.java) }
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
		request = o.subscribe({
//            showLinearLayout(it.items)
//            showListView(it.items)
			showRecView(it.items)
		}, {
			Log.e("test", "", it)
		})
	}

	fun showLinearLayout(feedList: ArrayList<FeedItem>) {
		val inflater = layoutInflater
		for (f in feedList) {
			val view = inflater.inflate(R.layout.list_item, vList, false)
			val vTitle = view.findViewById<TextView>(R.id.item_title)
			vTitle.text = f.title
			vList.addView(view)
		}
	}

	fun showListView(feedList: ArrayList<FeedItem>) {
		vListView.adapter = Adapter(feedList)
	}

	fun showRecView(feedList: ArrayList<FeedItem>) {
		vRecView.adapter = RecAdapter(feedList)
		vRecView.layoutManager = LinearLayoutManager(this)
	}

	class RecAdapter(val items: ArrayList<FeedItem>) : RecyclerView.Adapter<RecHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {
			val inflater = LayoutInflater.from(parent!!.context)
			val view = inflater.inflate(R.layout.list_item, parent, false)
			return RecHolder(view)
		}

		override fun getItemCount(): Int {
			return items.size
		}

		override fun onBindViewHolder(holder: RecHolder, position: Int) {
			val item = items[position] as FeedItem
			holder?.bind(item)
		}

		override fun getItemViewType(position: Int): Int {
			// для бесконечной ленты, например.
			return super.getItemViewType(position)
		}

	}

	class RecHolder(view: View) : RecyclerView.ViewHolder(view) {
		fun bind(item: FeedItem) {
			val vTitle = itemView.findViewById<TextView>(R.id.item_title)
			val vDesc = itemView.findViewById<TextView>(R.id.item_desc)
			val vThumb = itemView.findViewById<ImageView>(R.id.item_thumb)
			vTitle.text = item.title
			vDesc.text = item.description
			Picasso.with(vThumb.context).load(item.enclosure.link).into(vThumb)
			itemView.setOnClickListener{
				val i = Intent(Intent.ACTION_VIEW)
				i.data= Uri.parse(item.link)
				vThumb.context.startActivity(i)
			}
		}
	}

	class Adapter(val items: ArrayList<FeedItem>) : BaseAdapter(

	) {
		override fun getCount(): Int {
			return items.size
		}

		override fun getItem(position: Int): Any {
			return items[position]
		}

		override fun getItemId(position: Int): Long {
			return position.toLong()
		}

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val inflater = LayoutInflater.from(parent!!.context)
			val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
			val vTitle = view.findViewById<TextView>(R.id.item_title)
			val item = getItem(position) as FeedItem
			vTitle.text = item.title
			return vTitle
		}
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onStop() {
		super.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}

/* структура джсона
"items": [
{
"title": "Ученые в США научились выявлять риск появления мыслей о суициде"
"pubDate": "2023-12-16 13:06:00"
"link": "https://ria.ru/20231216/ssha-1916209978.html"
"guid": "https://ria.ru/20231216/ssha-1916209978.html"
"author": ""
"thumbnail": ""
"description": ""
"content": ""
"enclosure": {
    "link": "https://cdnn21.img.ria.ru/images/07e7/0c/08/1914678343_0:0:3012:1695_600x0_80_0_0_c6e950cf8b3eeac767dabdbf36cdd063.jpg.webp"
    "type": "image/jpeg"
}
"categories": [
"Лента новостей"
]
}

 */