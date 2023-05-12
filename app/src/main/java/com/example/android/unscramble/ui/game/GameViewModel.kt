package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//membuat class GameViewModel menjadi subclass dari ViewModel
class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

//membuat _currentScrambleWord hanya bisa diakses dan edit dalam GameViewModel
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

//untuk menyimpan daftar kata pada game
    private var wordsList: MutableList<String> = mutableListOf()
//untuk menyimpan kata yang disusun pemain
    private lateinit var currentWord: String

//blok untuk penyiapan inisialisasi instance objek
    init {
        Log.d("GameFragment", "GameViewModel created!")
//agar menampilkan kata yang diacak aplikasi
        getNextWord()
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
//mengkonversi string currentWord ke array karakter
        val tempWord = currentWord.toCharArray()
//untuk mengacak kata
        tempWord.shuffle()
//perulangan agar kata yang diacak tidak sama dengan kata aslinya
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
//pengkondisian untuk menyimpan kata yang baru diacak
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

//untuk merest skor sehingga bisa bermain lagi
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
//untuk menambah nilai skor pemain
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }
    fun isUserWordCorrect(playerWord: String): Boolean {
//pengkondisian jika kata yang ditebak benar maka skor bertambah
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

//mengembalikan nilai true jika jumlah kata kurang dari max_no_of_words
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}