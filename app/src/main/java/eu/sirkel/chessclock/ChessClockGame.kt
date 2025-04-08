package eu.sirkel.chessclock

object ChessClockGame {
    const val PLAYER_WHITE = 0
    const val PLAYER_BLACK = 1

    var initialTimeMillis: Long = 0
    var whiteRemainingMillis: Long = 0
    var blackRemainingMillis: Long = 0
    var currentPlayer: Int = PLAYER_WHITE
    var paused: Boolean = false
}
