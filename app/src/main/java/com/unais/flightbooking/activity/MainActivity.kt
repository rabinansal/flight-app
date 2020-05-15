package com.unais.flightbooking.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.unais.flightbooking.R
import com.unais.flightbooking.adapter.AirportAdapter
import com.unais.flightbooking.model.AirportResponse
import com.unais.flightbooking.viewmodel.ApplicationViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_traveller.*
import kotlinx.android.synthetic.main.bottom_traveller.view.*
import kotlinx.android.synthetic.main.popup_search_flight.view.*
import kotlinx.android.synthetic.main.popup_search_flight.view.rvFlights
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    var airportList = ArrayList<AirportResponse>()
    lateinit var mPopupWindow: PopupWindow
    var origin = ""
    var adapter: AirportAdapter?=null
    var fromAirPort = AirportResponse()
    var toAirPort = AirportResponse()


    var adultCount = 1
    var childCount = 0
    var infantCount = 0


    var depDate = ""
    var retDate = ""

    var classType = "ECONOMY"
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
        tvToShortCode.setOnClickListener { tvAirport.performClick() }

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

                    tvRetDateDay.setTextColor(ContextCompat.getColor(this,R.color.md_grey_800))
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

        tvDepDateDay.setOnClickListener {
           // pickerFrom.show(supportFragmentManager,pickerFrom.toString())

            val c = Calendar.getInstance()
            var mYear = c.get(Calendar.YEAR)
            var mMonth = c.get(Calendar.MONTH)
            var mDay = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,R.style.DialogTheme,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                
                var date = "$dayOfMonth ${month+1} $year"

                val dateDate = SimpleDateFormat("dd MM yyyy").parse(date)

                var weekDay = SimpleDateFormat("EEEE").format(dateDate)
                var monthYr = SimpleDateFormat("MMMM yyyy").format(dateDate)
                var dom = SimpleDateFormat("dd").format(dateDate)
                
                tvDepDateDay.text = dom
                tvDepMonthDateYr.text = monthYr
                tvDepMonthDateWeek.text = weekDay

                depDate = "$dom $monthYr"
            },mYear,mMonth,mDay)
            dpd.datePicker.minDate = Calendar.getInstance().timeInMillis
            dpd.show()
        }

        tvDepMonthDateYr.setOnClickListener {
//            pickerFrom.show(supportFragmentManager,pickerFrom.toString())
            tvDepDateDay.performClick()
        }

        tvDepMonthDateWeek.setOnClickListener {
//            pickerFrom.show(supportFragmentManager,pickerFrom.toString())
            tvDepDateDay.performClick()
        }


        tvRetDateDay.setOnClickListener {
            val c = Calendar.getInstance()
            var mYear = c.get(Calendar.YEAR)
            var mMonth = c.get(Calendar.MONTH)
            var mDay = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,R.style.DialogTheme,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                var date = "$dayOfMonth ${month+1} $year"

                val dateDate = SimpleDateFormat("dd MM yyyy").parse(date)

                var weekDay = SimpleDateFormat("EEEE").format(dateDate)
                var monthYr = SimpleDateFormat("MMMM yyyy").format(dateDate)
                var dom = SimpleDateFormat("dd").format(dateDate)

                tvRetDateDay.text  = dom
                tvRetMonthYr.text = monthYr
                tvRetDateWeek.text  = weekDay

                retDate = "$dom $monthYr"
            },mYear,mMonth,mDay)


            dpd.datePicker.minDate =Calendar.getInstance().timeInMillis
                dpd.show()
        }

        tvRetMonthYr.setOnClickListener {
            //pickerTo.show(supportFragmentManager,pickerTo.toString())
            tvRetDateDay.performClick()
        }

        tvRetDateWeek.setOnClickListener {
           // pickerTo.show(supportFragmentManager,pickerTo.toString())
            tvRetDateDay.performClick()
        }


        chipGroup.setOnCheckedChangeListener { group: ChipGroup?, checkedId: Int ->
            val chip = group?.findViewById<Chip>(checkedId)
            chip.let {
//                Toast.makeText(this,it?.chipText.toString(),Toast.LENGTH_SHORT).show()
                classType = it?.chipText.toString()
            }
        }


        findFlight.setOnClickListener {

            Log.d("DATE_CHECK","$depDate  $retDate")

            val departDate = SimpleDateFormat("dd MMMM yyyy").parse(depDate) as Date
            val returnDate = SimpleDateFormat("dd MMMM yyyy").parse(retDate) as Date

            if (fromAirPort.code == toAirPort.code) {

                Toast.makeText(this, "Origin & Destination cannot be same", Toast.LENGTH_SHORT)
                    .show()

            }
            else if(twoWay && returnDate.before(departDate)){
                    Toast.makeText(this, "Please choose correct Date", Toast.LENGTH_SHORT).show()
            } else {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Flight Details")

                if(twoWay){
                    builder.setMessage(
                        "From : ${fromAirPort.code} \nTo : ${toAirPort.code} \n" +
                                "Departure : $depDate \nReturn : $retDate \nAdult Travellers : $adultCount \n" +
                                "Child Travellers : $childCount \nInfant Travellers : $infantCount" +
                                "\nClass : $classType \nTwo Way : $twoWay"
                    )

                }else{
                    builder.setMessage(
                        "From : ${fromAirPort.code} \nTo : ${toAirPort.code} \n" +
                                "Departure : $depDate \nAdult Travellers : $adultCount \n" +
                                "Child Travellers : $childCount \nInfant Travellers : $infantCount" +
                                "\nClass : $classType \nTwo Way : $twoWay"
                    )
                }


                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }

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

    private fun swapAirports(frmAirport: AirportResponse,tAirport:AirportResponse) {

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
                            adapter= AirportAdapter(this@MainActivity, airportList, this)
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
