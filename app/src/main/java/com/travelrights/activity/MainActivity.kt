package com.travelrights.activity

import android.app.DatePickerDialog
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
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.timessquare.CalendarPickerView
import com.travelrights.R
import com.travelrights.adapter.AirportAdapter
import com.travelrights.model.AirportResponse
import com.travelrights.viewmodel.ApplicationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_traveller.*
import kotlinx.android.synthetic.main.bottom_traveller.view.*
import kotlinx.android.synthetic.main.popup_calander.view.*
import kotlinx.android.synthetic.main.popup_search_flight.view.*
import kotlinx.android.synthetic.main.popup_search_flight.view.textViewTitle
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    var airportList = ArrayList<AirportResponse>()
    lateinit var mPopupWindow: PopupWindow
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

    private lateinit var appViewModel: ApplicationViewModel
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appViewModel = ViewModelProviders.of(this).get(ApplicationViewModel::class.java)

        setUpInitDay()
        fromAirPort.code="BOM"
        toAirPort.code="DEL"
        tvTravellerAdultCount.text = adultCount.toString()

        tvRetOnTitle.isEnabled = false
        tvRetDateDay.isEnabled = false
        tvRetMonthYr.isEnabled = false
        tvRetDateWeek.isEnabled = false

        tvRetDateDay.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
        tvRetMonthYr.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))
        tvRetDateWeek.setTextColor(ContextCompat.getColor(this,R.color.md_grey_500))

        tvAirport.setOnClickListener {
            origin = "From"
            displaySearchPopup("Origin", "Search Origin", origin)
        }

        tvToAirport.setOnClickListener {
            origin = "To"
            displaySearchPopup("Destination", "Search Destination", origin)
        }

        tvShortCode.setOnClickListener { tvAirport.performClick() }
        tvToShortCode.setOnClickListener { tvToAirport.performClick() }

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


        tvTravellerAdultCount.setOnClickListener {
            openTravellerSheet()
        }

        tvTravellerChildCount.setOnClickListener {
            openTravellerSheet()
        }

        tvTravellerInfantCount.setOnClickListener {
            openTravellerSheet()
        }

        dep_date.setOnClickListener {
            calander_popup("dep")
        }

        ret_date.setOnClickListener {

            calander_popup("return")
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
                appViewModel.flight_search(jsonObj).observe(this, androidx.lifecycle.Observer {

                    val i= Intent(applicationContext,SearchResultActivity::class.java)
                    i.putExtra("search_id",it.search_id!!)
                    startActivity(i)
                    progress_circular.visibility=View.GONE

                })

            }
        }

    }

    private fun calander_popup(dep: String) {
        val inflater =
            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customViewCall1 = inflater.inflate(R.layout.popup_calander, null)
        mPopupWindow1 = PopupWindow(customViewCall1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        customViewCall1.close.setOnClickListener {
            mPopupWindow1.dismiss()
        }
        val nextYear: Calendar = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1)
        val today = Date()
        customViewCall1.calendar_view.init(today, nextYear.getTime()).withSelectedDate(today)
        customViewCall1.calendar_view.clearHighlightedDates()
        customViewCall1.calendar_view.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date?) {

                val weekDay = SimpleDateFormat("EEEE").format(date!!)
                val monthYr = SimpleDateFormat("MMMM yyyy").format(date)
                val dom = SimpleDateFormat("dd").format(date)

                if(dep=="dep"){
                    tvDepDateDay.text = dom
                    tvDepMonthDateYr.text = monthYr
                    tvDepMonthDateWeek.text = weekDay

                    depDate = "$dom $monthYr"
                }
                else{
                    tvRetDateDay.text  = dom
                    tvRetMonthYr.text = monthYr
                    tvRetDateWeek.text  = weekDay

                    retDate = "$dom $monthYr"
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


        val date = Calendar.getInstance().time
        val day = SimpleDateFormat("dd").format(date)
        val month = SimpleDateFormat("MMMM yyyy").format(date)
        val week = SimpleDateFormat("EEEE").format(date)

        tvDepDateDay.text = day
        tvDepMonthDateYr.text = month
        tvDepMonthDateWeek.text = week


        tvRetDateDay.text = day
        tvRetMonthYr.text = month
        tvRetDateWeek.text = week

        depDate = "$day $month"
        retDate = "$day $month"
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


    private fun displaySearchPopup(title: String, searchHint: String, origin: String) {

        val inflater3 =
            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customViewCall = inflater3.inflate(R.layout.popup_search_flight, null)
        mPopupWindow = PopupWindow(
            customViewCall,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        customViewCall.closePopup.setOnClickListener {
            mPopupWindow.dismiss()
        }

        customViewCall.textViewTitle.text = title
        customViewCall.edtSearch.hint = searchHint

        val rvFlights = customViewCall.rvFlights as RecyclerView
        rvFlights.setHasFixedSize(true)
        rvFlights.layoutManager = LinearLayoutManager(this)
        rvFlights.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvFlights.visibility = View.GONE

        customViewCall.edtSearch.addTextChangedListener(object : TextWatcher,
            AirportAdapter.OnItemClickListener {

            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()){
                    rvFlights.visibility = View.GONE
                }else{
                    rvFlights.visibility = View.VISIBLE

                    appViewModel.getAirportlist(s.toString()).observe(this@MainActivity, androidx.lifecycle.Observer {


                        if(it.size!=0){
                            airportList= it as ArrayList<AirportResponse>
                            adapter=
                                AirportAdapter(
                                    this@MainActivity,
                                    airportList,
                                    this
                                )
                            rvFlights.adapter = adapter

                        }



                    })

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun onItemClick(item: AirportResponse) {

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

                mPopupWindow.dismiss()
            }
        })

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.elevation = 5.0f
        }

        mPopupWindow.isFocusable = true
        mPopupWindow.animationStyle = R.style.popupAnimation
        mPopupWindow.showAtLocation(root_layout, Gravity.CENTER, 0, 0)
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


}
