package eu.sirkel.chessclock

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ClockActivity : AppCompatActivity() {
    private lateinit var whiteTimerText: TextView
    private lateinit var blackTimerText: TextView
    private lateinit var whiteArea: View
    private lateinit var blackArea: View
    private lateinit var whitePauseButton: ImageButton
    private lateinit var blackPauseButton: ImageButton

    private var countDownTimer: CountDownTimer? = null
    private val timerInterval: Long = 1000  // Update every second.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        // Prevent screen from sleeping
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Find views.
        whiteTimerText = findViewById(R.id.whiteTimer)
        blackTimerText = findViewById(R.id.blackTimer)
        whiteArea = findViewById(R.id.whiteArea)
        blackArea = findViewById(R.id.blackArea)
        whitePauseButton = findViewById(R.id.whitePauseButton)
        blackPauseButton = findViewById(R.id.blackPauseButton)

        // Full player areas are clickable to switch turns.
        whiteArea.setOnClickListener {
            if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_WHITE) {
                switchTurn()
            }
        }
        blackArea.setOnClickListener {
            if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_BLACK) {
                switchTurn()
            }
        }

        // Pause buttons: tapping pause cancels the timer and returns to MainActivity.
        whitePauseButton.setOnClickListener { pauseGame() }
        blackPauseButton.setOnClickListener { pauseGame() }

        // Check if we're resuming.
        val isResume = intent.getBooleanExtra("isResume", false)
        if (!isResume) {
            // For a new game, initialization was already handled.
        }

        startTimerForCurrentPlayer()
        updateTimerTexts()
    }

    private fun startTimerForCurrentPlayer() {
        countDownTimer?.cancel()
        val timeLeft = if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_WHITE)
            ChessClockGame.whiteRemainingMillis
        else
            ChessClockGame.blackRemainingMillis

        countDownTimer = object : CountDownTimer(timeLeft, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_WHITE) {
                    ChessClockGame.whiteRemainingMillis = millisUntilFinished
                } else {
                    ChessClockGame.blackRemainingMillis = millisUntilFinished
                }
                updateTimerTexts()
            }

            override fun onFinish() {
                updateTimerTexts()
                playBellSound()
                // Only flash the area of the player who ran out.
                if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_WHITE) {
                    startFlashingFor(whiteArea, Color.WHITE)
                } else {
                    startFlashingFor(blackArea, Color.BLACK)
                }
            }
        }.start()
    }

    private fun updateTimerTexts() {
        whiteTimerText.text = formatTime(ChessClockGame.whiteRemainingMillis)
        blackTimerText.text = formatTime(ChessClockGame.blackRemainingMillis)
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun switchTurn() {
        countDownTimer?.cancel()
        ChessClockGame.currentPlayer = if (ChessClockGame.currentPlayer == ChessClockGame.PLAYER_WHITE)
            ChessClockGame.PLAYER_BLACK
        else
            ChessClockGame.PLAYER_WHITE
        startTimerForCurrentPlayer()
    }

    private fun pauseGame() {
        countDownTimer?.cancel()
        ChessClockGame.paused = true
        finish()  // Return to MainActivity.
    }

    private fun playBellSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.bell_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    // Helper: Flash only the designated area with a toned-down red.
    private fun startFlashingFor(view: View, baseColor: Int) {
        val flashColor = Color.parseColor("#FF6666")  // toned-down red.
        ObjectAnimator.ofArgb(view, "backgroundColor", baseColor, flashColor).apply {
            duration = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
    }
}
