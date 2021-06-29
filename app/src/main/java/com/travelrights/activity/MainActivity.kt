package com.travelrights.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travelrights.R
import com.travelrights.adapter.AirportAdapter
import com.travelrights.model.AirportResponse
import com.travelrights.model.Flight_searchResponse
import com.travelrights.room.DatabaseClient
import com.travelrights.utils.PrefManager
import com.travelrights.utils.PrefManager.PREFERENCES
import com.travelrights.utils.calanderpicker.CalendarPickerView
import com.travelrights.viewmodel.Api
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_traveller.*
import kotlinx.android.synthetic.main.bottom_traveller.view.*
import kotlinx.android.synthetic.main.popup_calander.view.*
import kotlinx.android.synthetic.main.popup_search_flight.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var back_pressed: Long = 0
    var airportList = ArrayList<AirportResponse>()
    lateinit var mPopupWindow1: PopupWindow
    var origin = ""
    var adapter: AirportAdapter?=null
    var fromAirPort = AirportResponse()
    var toAirPort = AirportResponse()

    val jsonObj = JsonObject()
    var adultCount = 1
    var childCount = 0
    var infantCount = 0


    var depDate = ""
    var retDate = ""

    var classType = "Y"
    var twoWay = false
    var appUpdateManager: AppUpdateManager?=null
    private val TAG = "MainActivity"
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpInitDay()
        appUpdateManager= com.google.android.play.core.appupdate.AppUpdateManagerFactory.create(applicationContext)
        checkUpdate()
        tvRetOnTitle.isEnabled = false
        tvRetDateDay.isEnabled = false
        tvRetMonthYr.isEnabled = false
        tvRetDateWeek.isEnabled = false

        tvRetDateDay.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
        tvRetMonthYr.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
        tvRetDateWeek.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))

        tvScrollFrom.setOnClickListener {
            origin = "From"
            displaySearchPopup("Origin", "Search Origin", origin)
        }

        tvScrollTo.setOnClickListener {
            origin = "To"
            displaySearchPopup("Destination", "Search Destination", origin)
        }

        chip_way.setOnCheckedChangeListener { group: ChipGroup?, checkedId: Int ->
            val chip = group?.findViewById<Chip>(checkedId)
            chip.let {


                twoWay = it?.chipText.toString() != "  One Way  "

                if(twoWay){
                    twoWay = true

                    tvRetOnTitle.isEnabled = true
                    tvRetDateDay.isEnabled = true
                    tvRetMonthYr.isEnabled = true
                    tvRetDateWeek.isEnabled = true

                    tvRetDateDay.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000))
                    tvRetMonthYr.setTextColor(ContextCompat.getColor(this,R.color.md_grey_800))
                    tvRetDateWeek.setTextColor(ContextCompat.getColor(this,R.color.md_grey_800))
                }else{

                    twoWay = false
                    tvRetOnTitle.isEnabled = false
                    tvRetDateDay.isEnabled = false
                    tvRetMonthYr.isEnabled = false
                    tvRetDateWeek.isEnabled = false

                    tvRetDateDay.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
                    tvRetMonthYr.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
                    tvRetDateWeek.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
                }
            }
        }
        imageView.setOnClickListener {
            swapAirports(fromAirPort,toAirPort)
        }


        passenger.setOnClickListener {
            openTravellerSheet()
        }

        dep_date.setOnClickListener {
            calander_popup("dep",depDate)
        }

        ret_date.setOnClickListener {

            calander_popup("return",retDate)
        }

        chipGroup.setOnCheckedChangeListener { group: ChipGroup?, checkedId: Int ->
            val chip = group?.findViewById<Chip>(checkedId)
            chip.let {

                if(it?.chipText.toString()=="  Economy  "){
                    classType="Y"
                }
                else{
                    classType="C"
                }
            }
        }


        findFlight.setOnClickListener {

            Log.d("DATE_CHECK","$depDate  $retDate")

            val departDate = SimpleDateFormat("dd MMMM yyyy").parse(depDate) as Date
            val returnDate = SimpleDateFormat("dd MMMM yyyy").parse(retDate) as Date
            val departdate= SimpleDateFormat("yyyy-MM-dd").format(departDate)
            val returndate= SimpleDateFormat("yyyy-MM-dd").format(returnDate)
            if (fromAirPort.code == toAirPort.code) {

                Toast.makeText(this, "Origin & Destination cannot be same", Toast.LENGTH_SHORT)
                    .show()

            }
            else if(twoWay && returnDate.before(departDate)){
                    Toast.makeText(this, "Please choose correct Date", Toast.LENGTH_SHORT).show()
            } else {

                if(twoWay){

                    val jsonObj1 = JsonObject()
                    val jsonObj2= JsonObject()
                    val jsonObj3= JsonObject()
                    val jsonArray = JsonArray()
                    jsonObj.addProperty("signature", md5("14689e9bfd9fb54b0b35e4cce8c1596e:beta.aviasales.ru:en:264711:$adultCount:$childCount:" +
                            "$infantCount:$departdate:${toAirPort.code}:${fromAirPort.code}:$returndate:${fromAirPort.code}:${toAirPort.code}:$classType:127.0.0.1"))
                    jsonObj.addProperty("marker","264711" )
                    jsonObj.addProperty("host", "beta.aviasales.ru")
                    jsonObj.addProperty("user_ip", "127.0.0.1")
                    jsonObj.addProperty("locale", "en")
                    jsonObj.addProperty("trip_class", classType)
                    jsonObj1.addProperty("adults", adultCount)
                    jsonObj1.addProperty("children", childCount)
                    jsonObj1.addProperty("infants", infantCount)
                    jsonObj.add("passengers", jsonObj1)
                    jsonObj2.addProperty("origin", fromAirPort.code)
                    jsonObj2.addProperty("destination", toAirPort.code)
                    jsonObj2.addProperty("date",departdate)

                    jsonObj3.addProperty("origin", toAirPort.code)
                    jsonObj3.addProperty("destination", fromAirPort.code)
                    jsonObj3.addProperty("date",returndate)

                    jsonArray.add(jsonObj2)
                    jsonArray.add(jsonObj3)
                    jsonObj.add("segments",jsonArray)

                }else{

                    val jsonObj1 = JsonObject()
                    val jsonObj2= JsonObject()
                    val jsonArray = JsonArray()
                    jsonObj.addProperty("signature", md5("14689e9bfd9fb54b0b35e4cce8c1596e:beta.aviasales.ru:en:264711:$adultCount:$childCount:" +
                            "$infantCount:$departdate:${toAirPort.code}:${ fromAirPort.code}:$classType:127.0.0.1"))
                    jsonObj.addProperty("marker","264711" )
                    jsonObj.addProperty("host", "beta.aviasales.ru")
                    jsonObj.addProperty("user_ip", "127.0.0.1")
                    jsonObj.addProperty("locale", "en")
                    jsonObj.addProperty("trip_class", classType)
                    jsonObj1.addProperty("adults", adultCount)
                    jsonObj1.addProperty("children", childCount)
                    jsonObj1.addProperty("infants", infantCount)
                    jsonObj.add("passengers", jsonObj1)
                    jsonObj2.addProperty("origin", fromAirPort.code)
                    jsonObj2.addProperty("destination", toAirPort.code)
                    jsonObj2.addProperty("date",departdate)
                    jsonArray.add(jsonObj2)
                    jsonObj.add("segments",jsonArray)

                }
                println("********$jsonObj")
                progress_circular.visibility=View.VISIBLE
                flight_search(jsonObj)
                PrefManager.getInstance(applicationContext).putSharedString(PREFERENCES, jsonObj.toString())
                PrefManager.getInstance(applicationContext).putSharedString("from",tvAirport.text.toString())
                PrefManager.getInstance(applicationContext).putSharedString("to",tvToAirport.text.toString())
                PrefManager.getInstance(applicationContext).putSharedString("from_code",fromAirPort.code)
                PrefManager.getInstance(applicationContext).putSharedString("to_code",toAirPort.code)

            }
        }

    }
    private fun checkUpdate() {
        // Returns an intent object that you use to check for an update.
        appUpdateManager!!.registerListener(listener!!)
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        Log.d(TAG, "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.
                Log.d(TAG, "Update available")
                appUpdateManager!!.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    1)
            } else {
                Log.d(TAG, "No Update available")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager!!.unregisterListener(listener!!)
    }

    private val listener: InstallStateUpdatedListener? = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            Log.d(TAG, "An update has been downloaded")
            notiy()
        }

    }

    private fun notiy() {
        appUpdateManager!!.completeUpdate()
        appUpdateManager!!.unregisterListener(listener!!)
    }
    override fun onResume() {
        super.onResume()
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                notiy()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
       // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(TAG, "" + "Result Ok")
                    //  handle user's approval }
                }
                Activity.RESULT_CANCELED -> {
                    {
//if you want to request the update again just call checkUpdate()
                    }
                    Log.d(TAG, "" + "Result Cancelled")
                    //  handle user's rejection  }
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call
                     checkUpdate()
                    Log.d(TAG, "" + "Update Failure")
                    //  handle update failure
                }
            }
        }
    }
    fun flight_search(jobj: JsonObject) {

        val apiService = Api.getclient().create(Api::class.java)
        val url = Api.BASE_URL +"flight_search"
        val call = apiService.flight_search(url,jobj)
        call.enqueue(object : Callback<Flight_searchResponse> {
            override fun onResponse(call: Call<Flight_searchResponse>, response: Response<Flight_searchResponse>) {
                progress_circular.visibility=View.GONE
                if (response.isSuccessful) {

                    val i= Intent(applicationContext,SearchResultActivity::class.java)
                    i.putExtra("search_id",response.body()!!.search_id)
                    startActivity(i)
                }
                else{

                    Toast.makeText(application,""+response.errorBody(), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<Flight_searchResponse>, t: Throwable) {
                val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                progress_circular.visibility=View.GONE
            }
        })

    }


    private fun calander_popup(dep: String, depDate: String) {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customViewCall1 = inflater.inflate(R.layout.popup_calander, null)
        mPopupWindow1 = PopupWindow(customViewCall1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        customViewCall1.close.setOnClickListener {
            mPopupWindow1.dismiss()
        }
        val nextYear: Calendar = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1)
        val today = Date()
        val departDate = SimpleDateFormat("dd MMMM yyyy").parse(depDate) as Date
        customViewCall1.calendar_view.init(today, nextYear.time).withSelectedDate(departDate)
        customViewCall1.calendar_view.clearHighlightedDates()
        customViewCall1.calendar_view.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date?) {

                val weekDay = SimpleDateFormat("EEEE").format(date!!)
                val monthYr = SimpleDateFormat("MMM yyyy").format(date)
                val monthYr1 = SimpleDateFormat("MMMM yyyy").format(date)
                val dom = SimpleDateFormat("dd").format(date)

                if(dep=="dep"){
                    tvDepDateDay.text = dom
                    tvDepMonthDateYr.text = monthYr
                    tvDepMonthDateWeek.text = weekDay

                    this@MainActivity.depDate = "$dom $monthYr1"
                }
                else{
                    tvRetDateDay.text  = dom
                    tvRetMonthYr.text = monthYr
                    tvRetDateWeek.text  = weekDay

                    retDate = "$dom $monthYr1"
                }
                mPopupWindow1.dismiss()
            }

            override fun onDateUnselected(date: Date?) {

            }

        })

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow1.elevation = 5.0f
        }

        mPopupWindow1.isFocusable = true
        mPopupWindow1.animationStyle = R.style.popupAnimation
        mPopupWindow1.showAtLocation(root_layout, Gravity.CENTER, 0, 0)
    }

    fun md5(s: String): String? {
        val MD5 = "MD5"
        try { // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun setUpInitDay() {

        var date:Date
        var ret_date:Date
        val day:String
        val month:String
        val month1:String
        val week:String
        val day1:String
        val month2:String
        val month3:String
        val week1:String
        val jsondata= PrefManager.getInstance(applicationContext).getSharedString(PREFERENCES,jsonObj.toString())
        val to= PrefManager.getInstance(applicationContext).getSharedString("to",null)
        val from= PrefManager.getInstance(applicationContext).getSharedString("from",null)
        val to_code= PrefManager.getInstance(applicationContext).getSharedString("to_code",null)
        val from_code= PrefManager.getInstance(applicationContext).getSharedString("from_code",null)
        if(jsondata!="{}"){
            tvAirport.text=from
            tvToAirport.text=to
            fromAirPort.code=from_code
            toAirPort.code=to_code
            tvShortCode.text=from_code
            tvToShortCode.text=to_code
            val  jobj=JSONObject(jsondata)
            val segments=jobj.getString("segments")
            adultCount=jobj.getJSONObject("passengers").getInt("adults")
            childCount=jobj.getJSONObject("passengers").getInt("children")
            infantCount=jobj.getJSONObject("passengers").getInt("infants")
            val jsonary=JSONArray(segments)
            println("jsondata9******${jsonary.length()}")
            if(jsonary.length()>1){
                val date2=jsonary.getJSONObject(1).getString("date")
                val date1=jsonary.getJSONObject(0).getString("date")
                date= SimpleDateFormat("yyyy-MM-dd").parse(date1) as Date
                ret_date= SimpleDateFormat("yyyy-MM-dd").parse(date2) as Date
                if (date.compareTo(Calendar.getInstance().time) < 0){
                    date = Calendar.getInstance().time
                }
                day = SimpleDateFormat("dd").format(date)
                month = SimpleDateFormat("MMM yyyy").format(date)
                month1 = SimpleDateFormat("MMMM yyyy").format(date)
                week = SimpleDateFormat("EEEE").format(date)
                if (ret_date.compareTo(Calendar.getInstance().time) < 0){
                    ret_date = Calendar.getInstance().time
                }
                day1 = SimpleDateFormat("dd").format(ret_date)
                month2 = SimpleDateFormat("MMM yyyy").format(ret_date)
                month3 = SimpleDateFormat("MMMM yyyy").format(ret_date)
                week1 = SimpleDateFormat("EEEE").format(ret_date)
            }
            else{
                val date1=jsonary.getJSONObject(0).getString("date")
                date= SimpleDateFormat("yyyy-MM-dd").parse(date1) as Date
                if (date.compareTo(Calendar.getInstance().time) < 0){
                    date = Calendar.getInstance().time
                }
                day = SimpleDateFormat("dd").format(date)
                month = SimpleDateFormat("MMM yyyy").format(date)
                month1 = SimpleDateFormat("MMMM yyyy").format(date)
                week = SimpleDateFormat("EEEE").format(date)

                day1 = SimpleDateFormat("dd").format(date)
                month2 = SimpleDateFormat("MMM yyyy").format(date)
                month3 = SimpleDateFormat("MMMM yyyy").format(date)
                week1 = SimpleDateFormat("EEEE").format(date)
            }



        }
        else{

            date = Calendar.getInstance().time

            day = SimpleDateFormat("dd").format(date)
            month = SimpleDateFormat("MMM yyyy").format(date)
            month1 = SimpleDateFormat("MMMM yyyy").format(date)
            week = SimpleDateFormat("EEEE").format(date)

            day1 = SimpleDateFormat("dd").format(date)
            month2 = SimpleDateFormat("MMM yyyy").format(date)
            month3 = SimpleDateFormat("MMMM yyyy").format(date)
            week1= SimpleDateFormat("EEEE").format(date)
            fromAirPort.code="DXB"
            toAirPort.code="CCJ"
        }
        tvDepDateDay.text = day
        tvDepMonthDateYr.text = month
        tvDepMonthDateWeek.text = week


        tvRetDateDay.text = day1
        tvRetMonthYr.text = month2
        tvRetDateWeek.text = week1

        depDate = "$day $month1"
        retDate = "$day1 $month3"

        tvTravellerAdultCount.text = adultCount.toString()
        tvTravellerChildCount.text = childCount.toString()
        tvTravellerInfantCount.text = infantCount.toString()

    }

    private fun swapAirports(frmAirport: AirportResponse, tAirport: AirportResponse) {

        fromAirPort = frmAirport
        toAirPort = tAirport

        val airport = fromAirPort
        fromAirPort = toAirPort
        toAirPort = airport


        val frmAp = tvAirport.text.toString()
        val frmCode = tvShortCode.text.toString()

        val toArp = tvToAirport.text.toString()
        val toCode = tvToShortCode.text.toString()

        tvAirport.text = toArp
        tvShortCode.text = toCode

        tvToAirport.text = frmAp
        tvToShortCode.text = frmCode

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun displaySearchPopup(title: String, searchHint: String, origin: String) {
        popup_layout.visibility=View.VISIBLE
        val animation: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.top)
        popup_layout.animation = animation
        edtSearch.text.clear()
        closePopup.setOnClickListener {
            popup_layout.visibility=View.GONE
            val animation1: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_out)
            popup_layout.animation = animation1

            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
        }
        edtSearch.requestFocus()

        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        textViewTitle.text = title
        edtSearch.hint = searchHint

        val rvFlights = rvFlights as RecyclerView
        rvFlights.setHasFixedSize(true)
        rvFlights.layoutManager = LinearLayoutManager(this)
       // rvFlights.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvFlights.visibility = View.GONE
        DatabaseClient
            .getInstance(applicationContext)
            .appDatabase
            .taskDao()
            .deleteDuplicates()

        var taskList: List<AirportResponse> = DatabaseClient
            .getInstance(applicationContext)
            .appDatabase
            .taskDao()
            .all
        println("111111111111${taskList.size}")
        if (taskList.isNotEmpty()){
            if(taskList.size>6){
                DatabaseClient
                    .getInstance(applicationContext)
                    .appDatabase
                    .taskDao()
                    .deleteById(taskList[0].id!!)
            }
            taskList = DatabaseClient.getInstance(applicationContext).appDatabase.taskDao().all
            rvFlights.visibility = View.VISIBLE
            Collections.reverse(taskList)
            adapter= AirportAdapter(this@MainActivity,taskList, object :AirportAdapter.OnItemClickListener{
                override fun onItemClick(item: AirportResponse, view: View) {
                    when(view.id) {
                        R.id.linr_click -> {

                            imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                            if (origin.equals("From")) {
                                tvAirport.text = item.name
                                tvShortCode.text = item.code
                                fromAirPort = item
                            } else if (origin.equals("To")) {
                                tvToAirport.text = item.name
                                tvToShortCode.text = item.code
                                toAirPort = item
                            }

                            popup_layout.visibility=View.GONE
                            val animation1: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_out)
                            popup_layout.animation = animation1

                        }

                    }
                }

            })
            rvFlights.adapter = adapter
        }
        edtSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                airportList.clear()

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isEmpty()){
                    rvFlights.visibility = View.GONE
                }else{
                    rvFlights.visibility = View.VISIBLE

                    val apiService = Api.getclient().create(Api::class.java)
                    val url ="https://autocomplete.travelpayouts.com/places2?term=$s&locale=en&types[]= city"
                    val call = apiService.airportList(url)
                    popup_progress.visibility=View.VISIBLE
                    call.enqueue(object : Callback<List<AirportResponse>> {
                        override fun onResponse(call: Call<List<AirportResponse>>, response: Response<List<AirportResponse>>) {

                            if (response.isSuccessful) {
                                popup_progress.visibility=View.GONE
                                airportList.clear()
                                airportList= response.body() as ArrayList<AirportResponse>
                                adapter= AirportAdapter(this@MainActivity, airportList, object :AirportAdapter.OnItemClickListener{
                                    override fun onItemClick(item: AirportResponse, view: View) {
                                        when(view.id) {
                                            R.id.linr_click -> {
                                                imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                                                val task=AirportResponse()
                                                task.country_name=item.country_name
                                                task.code=item.code
                                                task.main_airport_name=item.main_airport_name
                                                task.name=item.name

                                                DatabaseClient.getInstance(applicationContext).appDatabase
                                                    .taskDao()
                                                    .insert(task)

                                                if(origin.equals("From")){
                                                    tvAirport.text = item.name
                                                    tvShortCode.text = item.code

                                                    fromAirPort = item
                                                }
                                                else if(origin.equals("To")){
                                                    tvToAirport.text = item.name
                                                    tvToShortCode.text = item.code
                                                    toAirPort = item
                                                }

                                                popup_layout.visibility=View.GONE
                                                val animation1: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_out)
                                                popup_layout.animation = animation1

                                            }
                                        }

                                    }

                                })
                                rvFlights.adapter = adapter

                            }

                        }
                        override fun onFailure(call: Call<List<AirportResponse>>, t: Throwable) {
                            popup_progress.visibility=View.GONE
                            val toast=Toast.makeText(application,t.message, Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    })


                }

            }

        })

    }

    private fun openTravellerSheet(){

        var adult = adultCount
        var child = childCount
        var infant = infantCount

        var totalCount = adult+child+infant

        val bottomSheetDialog = BottomSheetDialog(this,R.style.BootomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.bottom_traveller,ll_bottom_sheet)

        bottomSheetView.tvCountAdult.text = adultCount.toString()
        bottomSheetView.tvCountChild.text = childCount.toString()
        bottomSheetView.tvCountInfant.text = infantCount.toString()

        bottomSheetView.plusAdult.setOnClickListener {
            if(adult >= 0){
                if(totalCount < 9 ){
                    adult += 1
                    totalCount += 1
                    bottomSheetView.tvCountAdult.text = adult.toString()
                }else{
                    Toast.makeText(this,"Maximum 9 travellers only",Toast.LENGTH_SHORT).show()
                }

            }
        }

        bottomSheetView.minusAdult.setOnClickListener {
            if(adult > 1){
                adult -= 1
                totalCount -= 1
                bottomSheetView.tvCountAdult.text = adult.toString()
            }
        }

        bottomSheetView.plusChild.setOnClickListener {
            if(child >= 0){
                if(totalCount < 9 ) {
                    child += 1
                    totalCount += 1
                    bottomSheetView.tvCountChild.text = child.toString()
                }else{
                    Toast.makeText(this,"Maximum 9 travellers only",Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomSheetView.minusChild.setOnClickListener {
            if(child > 0){
                child -= 1
                totalCount -= 1
                bottomSheetView.tvCountChild.text = child.toString()
            }
        }

        bottomSheetView.plusInfant.setOnClickListener {
            if(infant <adult){
                if(totalCount < 9 ) {
                    infant += 1
                    totalCount += 1
                    bottomSheetView.tvCountInfant.text = infant.toString()
                }else{
                    Toast.makeText(this,"Maximum 9 travellers only",Toast.LENGTH_SHORT).show()

                }
            }
            else{
                Toast.makeText(this,"Number of infant may not be higher than adults",Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetView.minusInfant.setOnClickListener {
            if(infant > 0){
                infant -= 1
                totalCount -= 1
                bottomSheetView.tvCountInfant.text = infant.toString()
            }
        }


        bottomSheetView.btnDone.setOnClickListener {

            if(totalCount > 6){
                Toast.makeText(this,"Maximum 6 travellers only !",Toast.LENGTH_SHORT).show()
            }else if(totalCount <= 0){
                Toast.makeText(this,"Please select atleast 1 traveller",Toast.LENGTH_SHORT).show()
            }else{

                adultCount = adult
                childCount = child
                infantCount = infant

                tvTravellerAdultCount.text = adultCount.toString()
                tvTravellerChildCount.text = childCount.toString()
                tvTravellerInfantCount.text = infantCount.toString()
                bottomSheetDialog.dismiss()
            }

        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        bottomSheetDialog.setOnCancelListener(DialogInterface.OnCancelListener {
            //Toast.makeText(this,"DISMISED",Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        }
        else {
            if (popup_layout.visibility == View.VISIBLE) {
                popup_layout.visibility=View.GONE
                val animation1: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_out)
                popup_layout.animation = animation1
            } else {
                val toast=Toast.makeText(application,"Press Again to Exit from  App", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                back_pressed = System.currentTimeMillis()
            }

        }
    }

}

