package eu.sirkel.chessclock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val debugMode = false

    private lateinit var resumeButton: Button
    private lateinit var fiveMinButton: Button
    private lateinit var tenMinButton: Button
    private lateinit var fifteenMinButton: Button
    private lateinit var thirtyMinButton: Button
    private lateinit var sixtyMinButton: Button
    private lateinit var tenSecButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the default splash screen.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resumeButton = findViewById(R.id.btnResume)
        fiveMinButton = findViewById(R.id.btn5)
        tenMinButton = findViewById(R.id.btn10)
        fifteenMinButton = findViewById(R.id.btn15)
        thirtyMinButton = findViewById(R.id.btn30)
        sixtyMinButton = findViewById(R.id.btn60)
        tenSecButton = findViewById(R.id.btn10sec)

        resumeButton.visibility = if (ChessClockGame.paused) View.VISIBLE else View.GONE

        fiveMinButton.setOnClickListener { startClockActivityWithMillis(5 * 60 * 1000L) }
        tenMinButton.setOnClickListener { startClockActivityWithMillis(10 * 60 * 1000L) }
        fifteenMinButton.setOnClickListener { startClockActivityWithMillis(15 * 60 * 1000L) }
        thirtyMinButton.setOnClickListener { startClockActivityWithMillis(30 * 60 * 1000L) }
        sixtyMinButton.setOnClickListener { startClockActivityWithMillis(60 * 60 * 1000L) }

        if (debugMode) {
            tenSecButton.visibility = View.VISIBLE
            tenSecButton.setOnClickListener { startClockActivityWithMillis(10 * 1000L) }
        } else {
            tenSecButton.visibility = View.GONE
        }

        resumeButton.setOnClickListener {
            val intent = Intent(this, ClockActivity::class.java)
            intent.putExtra("isResume", true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeButton.visibility = if (ChessClockGame.paused) View.VISIBLE else View.GONE
    }

    private fun startClockActivityWithMillis(initialTimeMillis: Long) {
        ChessClockGame.initialTimeMillis = initialTimeMillis
        ChessClockGame.whiteRemainingMillis = initialTimeMillis
        ChessClockGame.blackRemainingMillis = initialTimeMillis
        ChessClockGame.currentPlayer = ChessClockGame.PLAYER_WHITE
        ChessClockGame.paused = false

        val intent = Intent(this, ClockActivity::class.java)
        intent.putExtra("initialTimeMillis", initialTimeMillis)
        intent.putExtra("isResume", false)
        startActivity(intent)
    }
}
