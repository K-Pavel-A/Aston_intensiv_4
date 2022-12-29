package ru.aston.astonintensiv4

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import ru.aston.astonintensiv4.databinding.ActivityMainBinding
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clockView.clock = CustomClock(getActualSeconds(), getActualMinutes(), getActualHours())
        println(getActualSeconds())
        println(getActualMinutes())
        println(getActualHours())

        binding.clockView.setOnClickListener {
            binding.clockView.clock = CustomClock(getActualSeconds(), getActualMinutes(), getActualHours())
            println(getActualSeconds())
            println(getActualMinutes())
            println(getActualHours())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getActualMinutes(): Int {
        return getActualTime()[1].toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getActualHours(): Int {
        return getActualTime()[0].toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getActualSeconds(): Int{
        val currentSeconds = try {
            val seconds = getActualTime()[2].split(".")
            seconds[0].toInt()
        } catch (e: IndexOutOfBoundsException) {
            0
        }
        return currentSeconds
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getActualTime(): List<String> {
        val currentTime = LocalTime.now().toString()
        return currentTime.split(":")
    }
}