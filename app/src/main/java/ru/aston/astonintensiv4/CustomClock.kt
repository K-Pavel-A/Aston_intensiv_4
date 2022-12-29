package ru.aston.astonintensiv4

typealias OnClockChangedListener = (clock: CustomClock) -> Unit

class CustomClock (
    val second : Int = 0,
    val minute: Int = 30,
    val hour: Int = 9
) {
    val listeners = mutableSetOf<OnClockChangedListener>()
}