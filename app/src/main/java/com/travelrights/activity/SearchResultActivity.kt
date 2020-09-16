package com.travelrights.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.TextHttpResponseHandler
import com.travelrights.R
import com.travelrights.utils.currencypicker.CurrencyPicker
import com.travelrights.model.*
import com.travelrights.utils.PrefManager
import com.travelrights.viewmodel.Api
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.item_search_result.view.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchResultActivity : AppCompatActivity() {
    internal var pricelist = ArrayList<NewItem>()
    internal var segment_list = ArrayList<SegmentItem>()
    internal var gateinfo_list = ArrayList<Gatesmember>()
    var curency_symbol:String?=null
    var curency_code:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        val search_id=intent.getStringExtra("search_id")
        progressbar.visibility= View.VISIBLE

        linr_menu.setOnClickListener {

           super.onBackPressed()
        }
        setUpInitDay()
        linr_curency.setOnClickListener {
            val picker: CurrencyPicker = CurrencyPicker.newInstance("Select Currency") // dialog title

            picker.setListener { name, code, symbol, flagDrawableResID ->
                curency_txt.text=symbol
                flag.setImageResource(flagDrawableResID)
                curency_symbol=curency_txt.text.toString()
                curency_code=code.toLowerCase()
                PrefManager.getInstance(applicationContext).putSharedString("code",code)
                PrefManager.getInstance(applicationContext).putSharedString("symbol",symbol)
                PrefManager.getInstance(applicationContext).putSharedInteger("flagDrawableResID",flagDrawableResID)
                picker.dismiss()
                val androidClient = AsyncHttpClient()
                progressbar.visibility= View.VISIBLE
                androidClient.get("http://yasen.aviasales.ru/adaptors/currency.json", object : TextHttpResponseHandler() {
                    override fun onFailure(statusCode: Int, headers: Array<Header>, responseString: String, throwable: Throwable) {

                    }
                    override fun onSuccess(statusCode: Int, headers: Array<Header>, responseToken: String) {
                        progressbar.visibility= View.GONE
                        val jsonob = JSONObject(responseToken)
                        val price=jsonob.getDouble(curency_code)
                        val adapter= PriceListAdapter(applicationContext,pricelist,curency_symbol!!,price,object :PriceListAdapter.OnItemClickListener{
                            override fun onItemClick(url: String,label: String, view: View) {
                                when(view.id){
                                    R.id.card_lay->
                                    {
                                        val apiService = Api.getclient().create(Api::class.java)
                                        progressbar.visibility= View.VISIBLE
                                        val url1 = Api.BASE_URL +"flight_searches/$search_id/clicks/${url}.json"
                                        val call1 = apiService.urlresult(url1)
                                        call1.enqueue(object : Callback<GeturlResponse> {
                                            override fun onResponse(call: Call<GeturlResponse>, response: Response<GeturlResponse>) {
                                                progressbar.visibility= View.GONE
                                                if (response.isSuccessful) {

                                                    val intent= Intent(applicationContext,WebviewAtivity::class.java)
                                                    intent.putExtra("url",response.body()!!.url)
                                                    intent.putExtra("title",label)
                                                    startActivity(intent)
                                                }
                                                else{

                                                    Toast.makeText(application,"Search Results are outdated", Toast.LENGTH_SHORT).show()
                                                    finish()
                                                }

                                            }

                                            override fun onFailure(call: Call<GeturlResponse>, t: Throwable) {
                                                val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                                                toast.setGravity(Gravity.CENTER, 0, 0)
                                                toast.show()

                                            }
                                        })
                                    }

                                }
                            }


                        })
                        recyclerview.adapter = adapter
                    }
                })

            }
            picker.show(supportFragmentManager, "CURRENCY_PICKER")

        }
        curency_symbol=curency_txt.text.toString()


        val androidClient = AsyncHttpClient()
        progressbar.visibility= View.VISIBLE
        androidClient.get("http://yasen.aviasales.ru/adaptors/currency.json", object : TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseString: String, throwable: Throwable) {
                val toast=Toast.makeText(application,responseString, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseToken: String) {

                val jsonob = JSONObject(responseToken)
                val price=jsonob.getDouble(curency_code)
                flight_search_result(search_id!!,price)
            }
        })


    }
    private fun setUpInitDay() {
        val symbol= PrefManager.getInstance(applicationContext).getSharedString("symbol",null)
        val code= PrefManager.getInstance(applicationContext).getSharedString("code",null)
        val flagDrawableResID= PrefManager.getInstance(applicationContext).getSharedInteger("flagDrawableResID",0)
        if(symbol!=null){
            curency_txt.text=symbol
            curency_symbol=curency_txt.text.toString()
            curency_code=code.toLowerCase()
            flag.setImageResource(flagDrawableResID)
        }
        else{
            curency_code="usd"
        }
        val date:Date
        val ret_date:Date
        val day:String
        val day1:String
        val jsondata= PrefManager.getInstance(applicationContext).getSharedString(PrefManager.PREFERENCES,null)
        if(jsondata!="{}"){

            val jobj=JSONObject(jsondata)
            val segments=jobj.getString("segments")
            val trip_class=jobj.getString("trip_class")

            if(trip_class=="Y"){
                class_type.text="Economy"
            }
            else{
                class_type.text="Buisness"
            }
            val count=jobj.getJSONObject("passengers").getInt("adults")+jobj.getJSONObject("passengers").getInt("children")+
                    jobj.getJSONObject("passengers").getInt("infants")
            p_count.text=count.toString()
            val jsonary= JSONArray(segments)
            println("jsondata9******${jsonary.length()}")
            if(jsonary.length()>1){
                val date2=jsonary.getJSONObject(1).getString("date")
                val date1=jsonary.getJSONObject(0).getString("date")
                val origin=jsonary.getJSONObject(0).getString("origin")
                val destination=jsonary.getJSONObject(0).getString("destination")
                place.text="$origin-$destination"
                date= SimpleDateFormat("yyyy-MM-dd").parse(date1) as Date
                ret_date= SimpleDateFormat("yyyy-MM-dd").parse(date2) as Date

                day = SimpleDateFormat("dd MMM").format(date)
                day1 = SimpleDateFormat("dd MMM").format(ret_date)
                date_txt.text="$day-$day1"
            }
            else{
                val date1=jsonary.getJSONObject(0).getString("date")
                val origin=jsonary.getJSONObject(0).getString("origin")
                val destination=jsonary.getJSONObject(0).getString("destination")
                place.text="$origin-$destination"
                date= SimpleDateFormat("yyyy-MM-dd").parse(date1) as Date

                day = SimpleDateFormat("dd MMM yyyy").format(date)
                date_txt.text=day
            }



        }

    }

    fun flight_search_result(uuid: String, c_price: Double) {

        val apiService = Api.getclient().create(Api::class.java)
        val url= Api.BASE_URL +"flight_search_results?uuid=$uuid"
        // val url1 ="https://moreapi123.000webhostapp.com/portfolio.php"
        println("********$url")
        val call = apiService.flight_search_result(url)
        call.enqueue(object : Callback<List<SearchResultResponse>> {
            override fun onResponse(call: Call<List<SearchResultResponse>>, response: Response<List<SearchResultResponse>>) {
                progressbar.visibility= View.GONE
                if (response.isSuccessful) {
                    println("response********${response.body()!!}")
                    if(response.body()!!.toString()!="[SearchResultResponse(proposals=null, meta=null, gates_info=null)]"){
                        pricelist.clear()
                        gateinfo_list.clear()
                        segment_list.clear()
                        for(i  in response.body()!!.indices) {
                            if(response.body()!![i].proposals!=null){
                                for (j in response.body()!![i].proposals!!.indices) {
                                    val item=NewItem()
                                    val resultMap:Map<String,Terms> = response.body()!![i].proposals?.get(j)!!.result!!
                                    segment_list= response.body()!![i].proposals!![j].segment!!
                                    item.dep= segment_list[0].flight!![0]!!.departure
                                    item.dep_time= segment_list[0].flight!![0]!!.departureTimestamp
                                    item.arrivl= segment_list[0].flight!![segment_list[0].flight!!.size-1]!!.arrival
                                    item.arrivl_time= segment_list[0].flight!![segment_list[0].flight!!.size-1]!!.arrivalTimestamp
                                    item.stop=segment_list[0].flight!!.size-1
                                    item.operater=segment_list[0].flight!![0]!!.operatedBy
                                    item.size=segment_list.size
                                    item.totl_time= response.body()!![i].proposals!![j].segmentDurations!![0]
                                    if(segment_list.size==2){
                                        item.dep1= segment_list[1].flight!![0]!!.departure
                                        item.dep_time1= segment_list[1].flight!![0]!!.departureTimestamp
                                        item.arrivl1= segment_list[1].flight!![segment_list[1].flight!!.size-1]!!.arrival
                                        item.arrivl_time1= segment_list[1].flight!![segment_list[1].flight!!.size-1]!!.arrivalTimestamp
                                        item.stop1=segment_list[1].flight!!.size-1
                                        item.totl_time1= response.body()!![i].proposals!![j].segmentDurations!![1]
                                        item.operater1=segment_list[1].flight!![0]!!.operatedBy
                                    }
                                    for ((key, value) in resultMap.entries) {

                                        item.price= value.price
                                        item.currency=value.currency
                                        item.url=value.url

                                        val resultMap1:Map<String, Gatesmember> = response.body()!![i].gates_info!!
                                        for ((key1, value1) in resultMap1.entries) {
                                            gateinfo_list.add(value1)
                                            item.via=value1.label
                                        }

                                    }
                                    pricelist.add(item)
                                    pricelist.sortWith(Comparator {
                                            o1, o2 -> o1.price!!.compareTo(o2.price!!)
                                    })
                                    search_no.visibility=View.VISIBLE
                                    linr_curency.visibility=View.VISIBLE
                                    search_no.text= pricelist.size.toString()+" Search Results"
                                    recyclerview.setHasFixedSize(true)
                                    recyclerview.layoutManager = LinearLayoutManager(applicationContext)
                                    val adapter= PriceListAdapter(applicationContext,pricelist,curency_symbol!!,c_price,object :PriceListAdapter.OnItemClickListener{
                                        override fun onItemClick(url: String,label: String, view: View) {
                                            when(view.id){
                                                R.id.card_lay->
                                                {
                                                    progressbar.visibility= View.VISIBLE
                                                    val url1 = Api.BASE_URL +"flight_searches/$uuid/clicks/${url}.json"
                                                    val call1 = apiService.urlresult(url1)
                                                    call1.enqueue(object : Callback<GeturlResponse> {
                                                        override fun onResponse(call: Call<GeturlResponse>, response: Response<GeturlResponse>) {
                                                            progressbar.visibility= View.GONE
                                                            if (response.isSuccessful) {

                                                                val intent= Intent(applicationContext,WebviewAtivity::class.java)
                                                                intent.putExtra("url",response.body()!!.url)
                                                                intent.putExtra("title",label)
                                                                startActivity(intent)
                                                            }
                                                            else{
                                                                val toast=Toast.makeText(application,"Search Results are outdated", Toast.LENGTH_SHORT)
                                                                toast.setGravity(Gravity.CENTER, 0, 0)
                                                                toast.show()
                                                                finish()
                                                            }

                                                        }

                                                        override fun onFailure(call: Call<GeturlResponse>, t: Throwable) {
                                                            val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                                                            toast.setGravity(Gravity.CENTER, 0, 0)
                                                            toast.show()
                                                        }
                                                    })
                                                }

                                            }
                                        }


                                    })
                                    recyclerview.adapter = adapter
                                    flight_search_result1(uuid,c_price)
                                }

                            }

                        }
                        recyclerview.visibility=View.VISIBLE
                        error_img.visibility=View.GONE
                    }
                    else{
                        recyclerview.visibility=View.GONE
                        error_img.visibility=View.VISIBLE
                        change.setOnClickListener {
                            finish()
                        }
                    }

                }
                else{
                    progressbar.visibility= View.GONE
                    Toast.makeText(application,""+response.message(), Toast.LENGTH_SHORT).show()
                    println("********${response.errorBody()}")
                }

            }
            override fun onFailure(call: Call<List<SearchResultResponse>>, t: Throwable) {
                println("********${t.message}")
                progressbar.visibility= View.GONE
                val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        })


    }

    fun flight_search_result1(uuid: String, c_price: Double) {
        progressbar.visibility= View.VISIBLE
        val apiService = Api.getclient().create(Api::class.java)
        val url= Api.BASE_URL +"flight_search_results?uuid=$uuid"
        // val url1 ="https://moreapi123.000webhostapp.com/portfolio.php"
        println("********$url")
        val call = apiService.flight_search_result(url)
        call.enqueue(object : Callback<List<SearchResultResponse>> {
            override fun onResponse(call: Call<List<SearchResultResponse>>, response: Response<List<SearchResultResponse>>) {
                progressbar.visibility= View.GONE
                if (response.isSuccessful) {
                    println("response********${response.body()!!}")
                    if(response.body()!!.toString()!="[SearchResultResponse(proposals=null, meta=null, gates_info=null)]"){
                        for(i  in response.body()!!.indices) {
                            if(response.body()!![i].proposals!=null){
                                for (j in response.body()!![i].proposals!!.indices) {
                                    val item=NewItem()
                                    val resultMap:Map<String,Terms> = response.body()!![i].proposals?.get(j)!!.result!!
                                    segment_list= response.body()!![i].proposals!![j].segment!!
                                    item.dep= segment_list[0].flight!![0]!!.departure
                                    item.dep_time= segment_list[0].flight!![0]!!.departureTimestamp
                                    item.arrivl= segment_list[0].flight!![segment_list[0].flight!!.size-1]!!.arrival
                                    item.arrivl_time= segment_list[0].flight!![segment_list[0].flight!!.size-1]!!.arrivalTimestamp
                                    item.stop=segment_list[0].flight!!.size-1
                                    item.operater=segment_list[0].flight!![0]!!.operatedBy
                                    item.size=segment_list.size
                                    item.totl_time= response.body()!![i].proposals!![j].segmentDurations!![0]
                                    if(segment_list.size==2){
                                        item.dep1= segment_list[1].flight!![0]!!.departure
                                        item.dep_time1= segment_list[1].flight!![0]!!.departureTimestamp
                                        item.arrivl1= segment_list[1].flight!![segment_list[1].flight!!.size-1]!!.arrival
                                        item.arrivl_time1= segment_list[1].flight!![segment_list[1].flight!!.size-1]!!.arrivalTimestamp
                                        item.stop1=segment_list[1].flight!!.size-1
                                        item.totl_time1= response.body()!![i].proposals!![j].segmentDurations!![1]
                                        item.operater1=segment_list[1].flight!![0]!!.operatedBy
                                    }
                                    for ((key, value) in resultMap.entries) {

                                        item.price= value.price
                                        item.currency=value.currency
                                        item.url=value.url

                                        val resultMap1:Map<String, Gatesmember> = response.body()!![i].gates_info!!
                                        for ((key1, value1) in resultMap1.entries) {
                                            gateinfo_list.add(value1)
                                            item.via=value1.label
                                        }

                                    }
                                    pricelist.add(item)
                                    pricelist.sortWith(Comparator {
                                            o1, o2 -> o1.price!!.compareTo(o2.price!!)
                                    })
                                    search_no.text= pricelist.size.toString()+" Search Results"
                                    recyclerview.setHasFixedSize(true)
                                    recyclerview.layoutManager = LinearLayoutManager(applicationContext)
                                    val adapter= PriceListAdapter(applicationContext,pricelist,curency_symbol!!,c_price,object :PriceListAdapter.OnItemClickListener{
                                        override fun onItemClick(url: String,label: String, view: View) {
                                            when(view.id){
                                                R.id.card_lay->
                                                {
                                                    progressbar.visibility= View.VISIBLE
                                                    val url1 = Api.BASE_URL +"flight_searches/$uuid/clicks/${url}.json"
                                                    val call1 = apiService.urlresult(url1)
                                                    call1.enqueue(object : Callback<GeturlResponse> {
                                                        override fun onResponse(call: Call<GeturlResponse>, response: Response<GeturlResponse>) {
                                                            progressbar.visibility= View.GONE
                                                            if (response.isSuccessful) {

                                                                val intent= Intent(applicationContext,WebviewAtivity::class.java)
                                                                intent.putExtra("url",response.body()!!.url)
                                                                intent.putExtra("title",label)
                                                                startActivity(intent)
                                                            }
                                                            else{
                                                                val toast=Toast.makeText(application,"Search Results are outdated", Toast.LENGTH_SHORT)
                                                                toast.setGravity(Gravity.CENTER, 0, 0)
                                                                toast.show()
                                                                finish()
                                                            }

                                                        }

                                                        override fun onFailure(call: Call<GeturlResponse>, t: Throwable) {
                                                            val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                                                            toast.setGravity(Gravity.CENTER, 0, 0)
                                                            toast.show()

                                                        }
                                                    })
                                                }

                                            }
                                        }


                                    })
                                    recyclerview.adapter = adapter

                                }

                            }

                        }
                    }
                }
                else{
                    progressbar.visibility= View.GONE
                    Toast.makeText(application,""+response.message(), Toast.LENGTH_SHORT).show()
                    println("********${response.errorBody()}")
                }

            }
            override fun onFailure(call: Call<List<SearchResultResponse>>, t: Throwable) {
                println("********${t.message}")
                progressbar.visibility= View.GONE
               /* val toast=Toast.makeText(application,"Server error please try again later", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()*/
            }
        })


    }


}
    class PriceListAdapter(val applicationContext: Context?,val pricelist: ArrayList<NewItem>,val curency_symbol:String,val c_price:Double,val listener:OnItemClickListener) :
        RecyclerView.Adapter<PriceListAdapter.PriceListHolder>() {

        interface OnItemClickListener {
            fun onItemClick(url: String,label: String, view: View)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceListHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)

            return PriceListHolder(view)
        }

        override fun getItemCount(): Int {

            return  pricelist.size
        }

        override fun onBindViewHolder(holder:PriceListHolder, position: Int) {
            holder.bind(pricelist[position].url.toString(), pricelist[position].via.toString(),listener)
            holder.via_text.text="via\n"+ pricelist[position].via
            holder.currency.text="$curency_symbol ${(pricelist[position].price?.div(c_price)!!.toInt())}"
            if( pricelist[position].size==1){
                holder.two_way.visibility=View.GONE
                holder.dep_txt.text=pricelist[position].dep
                holder.arrivl_txt.text=  pricelist[position].arrivl
                holder.one_way_stop.text="${pricelist[position].stop}-stop"
                holder.totaltime.text="${pricelist[position].totl_time?.div(60)}h${pricelist[position].totl_time?.rem(60)}m"
                val calendar = Calendar.getInstance(Locale.ENGLISH)
                calendar.timeInMillis = pricelist[position].dep_time!!.toLong()*1000L
                val date = DateFormat.format("hh:mm a",calendar).toString()
                holder.time1.text=date
                val calendar1 = Calendar.getInstance(Locale.ENGLISH)
                calendar1.timeInMillis = pricelist[position].arrivl_time!!.toLong()*1000L
                val date1 = DateFormat.format("hh:mm a",calendar1).toString()
                holder.time2.text=date1
                Glide.with(applicationContext!!).load("https://pics.avs.io/180/80/${pricelist[position].operater}.png").into(holder.operator)
                //  Glide.with(applicationContext!!).load("https://content.airhex.com/content/logos/airlines_${pricelist[position].operater}_100_100_s.png?proportions=keep").into(holder.operator)

            }
            if(pricelist[position].size==2){
                holder.via_text.visibility=View.GONE
                holder.two_way.visibility=View.VISIBLE
                holder.dep_txt.text=pricelist[position].dep
                holder.arrivl_txt.text=  pricelist[position].arrivl
                holder.one_way_stop.text="${pricelist[position].stop}-stop"
                holder.totaltime.text="${pricelist[position].totl_time?.div(60)}h${pricelist[position].totl_time?.rem(60)}m"

                val calendar = Calendar.getInstance(Locale.ENGLISH)
                calendar.timeInMillis = pricelist[position].dep_time!!.toLong()*1000L
                val date = DateFormat.format("hh:mm a",calendar).toString()
                holder.time1.text=date
                val calendar1 = Calendar.getInstance(Locale.ENGLISH)
                calendar1.timeInMillis = pricelist[position].arrivl_time!!.toLong()*1000L
                val date1 = DateFormat.format("hh:mm a",calendar1).toString()
                holder.time2.text=date1
                Glide.with(applicationContext!!).load("https://pics.avs.io/180/80/${pricelist[position].operater}.png").into(holder.operator)

                holder.dep_txt1.text=pricelist[position].dep1
                holder.arrivl_txt1.text=  pricelist[position].arrivl1
                holder.two_way_stop.text="${pricelist[position].stop1}-stop"
                holder.totaltime1.text="${pricelist[position].totl_time1?.div(60)}h${pricelist[position].totl_time1?.rem(60)}m"
                val calendar3 = Calendar.getInstance(Locale.ENGLISH)
                calendar3.timeInMillis =pricelist[position].dep_time1!!.toLong()*1000L
                val date3 = DateFormat.format("hh:mm a",calendar3).toString()
                holder.time3.text=date3
                val calendar4 = Calendar.getInstance(Locale.ENGLISH)
                calendar4.timeInMillis =pricelist[position].arrivl_time1!!.toLong()*1000L
                val date4 = DateFormat.format("hh:mm a",calendar4).toString()
                holder.time4.text=date4

                Glide.with(applicationContext).load("https://pics.avs.io/180/80/${pricelist[position].operater1}.png").into(holder.operator1)
            }
            println("*******segmentItem${pricelist[position].size}*********${pricelist.size}")
        }

        class PriceListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var via_text=itemView.via_text
            var card_lay=itemView.card_lay
            var currency=itemView.currency
            var dep_txt=itemView.dep_txt
            var arrivl_txt=itemView.arrivl_txt
            var two_way=itemView.two_way
            var dep_txt1=itemView.dep_txt1
            var arrivl_txt1=itemView.arrivl_txt1
            var one_way_stop=itemView.one_way_stop
            var two_way_stop=itemView.two_way_stop
            var operator=itemView.operator
            var operator1=itemView.operator1
            var time1=itemView.time1
            var time2=itemView.time2
            var time3=itemView.time3
            var time4=itemView.time4
            var totaltime=itemView.totaltime
            var totaltime1=itemView.totaltime1
            fun bind(url: String,label: String,listener: OnItemClickListener) {
                card_lay.setOnClickListener(View.OnClickListener { v -> listener.onItemClick(url,label, v) })
            }

        }

    }
