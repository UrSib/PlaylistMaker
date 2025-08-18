package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
    private lateinit var searchHistory: SearchHistory

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private lateinit var history: MutableList<Track>
    private lateinit var adapter: TrackAdapter


    private lateinit var historyAdapter: TrackAdapter


    var searchText = ""

    private lateinit var progressBar: ProgressBar
    private lateinit var clearHistoryButton: Button
    private lateinit var infoText: TextView
    private lateinit var nothingWasFound: ImageView
    private lateinit var communicationProblem: ImageView
    private lateinit var refreshButton: Button
    private lateinit var search: EditText
    private lateinit var historyLayout: LinearLayout

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val searchRunnable = Runnable { request() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(sharedPreferences)
        history = searchHistory.showHistory().toMutableList()

        historyLayout = findViewById<LinearLayout>(R.id.history_layout)

        historyAdapter =
            TrackAdapter(tracks = history, onTrackClick = {}, clickDebounce = ::clickDebounce)

        clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            history.clear()
            historyLayout.isVisible = false
            historyAdapter.notifyDataSetChanged()
        }


        adapter = TrackAdapter(tracks = tracks, onTrackClick = { track ->
            searchHistory.historyEditor(history, track)
            searchHistory.saveHistory(history.toTypedArray<Track>())
            historyAdapter.notifyDataSetChanged()
        }, clickDebounce = ::clickDebounce)


        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        search = findViewById<EditText>(R.id.edit_text_search)
        val clear = findViewById<ImageView>(R.id.clear)
        refreshButton = findViewById<Button>(R.id.refresh_button)
        infoText = findViewById<TextView>(R.id.info_text)
        communicationProblem = findViewById<ImageView>(R.id.ic_communications_problem)
        nothingWasFound = findViewById<ImageView>(R.id.ic_nothing_was_found)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        search.setOnFocusChangeListener { view, hasFocus ->
            historyLayout.isVisible =
                hasFocus && search.text.isEmpty() && history.isNotEmpty()
        }

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
            infoText.visibility = View.GONE
            nothingWasFound.visibility = View.GONE
            communicationProblem.visibility = View.GONE
            refreshButton.isVisible = false

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(search.windowToken, 0)

        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear.isVisible = clearVisibility(s)
                historyLayout.isVisible =
                    search.hasFocus() && s?.isEmpty() == true && history.isNotEmpty()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }
        }


        search.addTextChangedListener(simpleTextWatcher)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter


        var historyRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_history)

        historyRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.adapter = historyAdapter
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
            infoText.isVisible = true
            if (c == 1) {
                nothingWasFound.isVisible = true
            } else {
                communicationProblem.isVisible = true
                refreshButton.isVisible = true
            }
            tracks.clear()
            adapter.notifyDataSetChanged()
            infoText.text = text
        } else {
            infoText.isVisible = false
            communicationProblem.isVisible = false
            nothingWasFound.isVisible = false
            refreshButton.isVisible = false
        }
    }

    private fun request() {

        communicationProblem.isVisible = false
        nothingWasFound.isVisible = false
        refreshButton.isVisible = false
        progressBar.isVisible = true

        if (search.text.isNotEmpty()) {
            itunesService.searchTrack(search.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        progressBar.isVisible = false
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", 1)
                            } else {
                                showMessage(getString(R.string.nothing_was_found), 1)
                                nothingWasFound.isVisible = true

                            }

                        } else {
                            showMessage(getString(R.string.communications_problem), 2)
                            communicationProblem.isVisible = true
                            refreshButton.isVisible = true
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        println("Request failed: ${t.message}")
                        showMessage(getString(R.string.communications_problem), 2)
                        communicationProblem.isVisible = true
                        refreshButton.isVisible = true
                    }
                })

        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (search.text.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            progressBar.isVisible = false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}

