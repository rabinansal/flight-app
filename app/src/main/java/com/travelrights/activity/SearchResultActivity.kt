package com.travelrights.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.travelrights.R
import com.travelrights.viewmodel.ApplicationViewModel
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {
    private lateinit var appViewModel: ApplicationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        val search_id=intent.getStringExtra("search_id")
        progressbar.visibility= View.VISIBLE
        appViewModel = ViewModelProviders.of(this).get(ApplicationViewModel::class.java)
        appViewModel.flight_search_result(search_id!!).observe(this, androidx.lifecycle.Observer {

            progressbar.visibility= View.GONE
            Toast.makeText(this, it[0].proposals?.get(0)?.segment?.get(0)?.flight?.get(0)?.aircraft, Toast.LENGTH_SHORT).show()

        })

    }
}