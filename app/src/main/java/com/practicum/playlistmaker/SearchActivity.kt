package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout.BaseOnOffsetChangedListener
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.toString

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    var searchText = ""

    private lateinit var infoText: TextView
    private lateinit var nothingWasFound: ImageView
    private lateinit var communicationProblem: ImageView
    private lateinit var refreshButton: Button
    private lateinit var search: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        search = findViewById<EditText>(R.id.edit_text_search)
        val clear = findViewById<ImageView>(R.id.clear)
        refreshButton = findViewById<Button>(R.id.refresh_button)
        infoText = findViewById<TextView>(R.id.info_text)
        communicationProblem = findViewById<ImageView>(R.id.ic_communications_problem)
        nothingWasFound = findViewById<ImageView>(R.id.ic_nothing_was_found)

        search.setText(searchText)

        toolbarSearch.setNavigationOnClickListener {

            finish()

        }

        refreshButton.setOnClickListener {

            request()

        }

        clear.setOnClickListener {

            search.setText("")

            tracks.clear()
            adapter.notifyDataSetChanged()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(search.windowToken, 0)

        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.isVisible = clearVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }
        }

        search.addTextChangedListener(simpleTextWatcher)

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                request()
                true
            }
            false
        }

        adapter.tracks = tracks

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter


    }

    private fun clearVisibility(s: CharSequence?): Boolean {
        return if (s.isNullOrEmpty()) {
            false
        } else {
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("SEARCH_TEXT", searchText)
    }

    private fun showMessage(text: String, c: Int) {
        if (text.isNotEmpty()) {
            infoText.visibility = View.VISIBLE
            if (c == 1) {
                nothingWasFound.visibility = View.VISIBLE
            } else {
                communicationProblem.visibility = View.VISIBLE
                refreshButton.visibility = View.VISIBLE
            }
            tracks.clear()
            adapter.notifyDataSetChanged()
            infoText.text = text
        } else {
            infoText.visibility = View.GONE
            communicationProblem.visibility = View.GONE
            nothingWasFound.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }

    private fun request() {

        communicationProblem.visibility = View.GONE
        nothingWasFound.visibility = View.GONE
        refreshButton.visibility = View.GONE

        if (search.text.isNotEmpty()) {
            itunesService.searchTrack(search.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", 1)
                            } else {
                                showMessage(getString(R.string.nothing_was_found), 1)
                                nothingWasFound.visibility = View.VISIBLE

                            }

                        } else {
                            showMessage(getString(R.string.communications_problem), 2)
                            communicationProblem.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        showMessage(getString(R.string.communications_problem), 2)
                        communicationProblem.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                    }
                })

        }
    }

}

