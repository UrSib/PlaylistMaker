package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        val search = findViewById<EditText>(R.id.edit_text_search)
        val clear = findViewById<ImageView>(R.id.clear)

        toolbarSearch.setNavigationOnClickListener {

            finish()

        }

        clear.setOnClickListener {

            search.setText("")

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(search.windowToken, 0)

        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clear.visibility = clearVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
              // TODO:
            }
        }

        search.addTextChangedListener(simpleTextWatcher)

    }

    private fun clearVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}
