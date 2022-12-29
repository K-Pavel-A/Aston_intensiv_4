package ru.aston.astonintensiv4

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.lang.Integer.max
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.properties.Delegates

typealias OnTimeChangedListener = (second: Int, minute: Int, hour: Int) -> Unit

class ClockView (
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
): View(context, attributeSet, defStyleAttr, defStyleRes) {

    var clock: CustomClock? = null

        set(value){
            field?.listeners?.remove(listener)
            field = value
            field?.listeners?.add(listener)
            updateViewSize()
            requestLayout()
            invalidate()
        }

    private var secondHandColor by Delegates.notNull<Int>()
    private var minuteHandColor by Delegates.notNull<Int>()
    private var hourHandColor by Delegates.notNull<Int>()
    private var clockFaceColor by Delegates.notNull<Int>()

    private val fieldRect = RectF()
    private var clockFaceSize: Float = DESIRED_CLOCK_SIZE

    private lateinit var secondHandPaint: Paint
    private lateinit var minuteHandPaint: Paint
    private lateinit var hourHandPaint: Paint
    private lateinit var clockFacePaint: Paint


    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int): this(context, attributeSet, defStyleAttr, R.attr.ClockViewStyle)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, R.attr.ClockViewStyle)
    constructor(context: Context) : this(context, null)

    init {
        if(attributeSet != null){
            initAttributes(attributeSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        initPaints()
        if (isInEditMode) {
            clock = CustomClock()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        clock?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clock?.listeners?.remove(listener)
    }

    private fun initPaints(){
        secondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        secondHandPaint.color = secondHandColor
        secondHandPaint.style = Paint.Style.STROKE
        secondHandPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)

        minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        minuteHandPaint.color =  minuteHandColor
        minuteHandPaint.style = Paint.Style.STROKE
        minuteHandPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)

        hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        hourHandPaint.color = hourHandColor
        hourHandPaint.style = Paint.Style.STROKE
        hourHandPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, resources.displayMetrics)

        clockFacePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockFacePaint.color = clockFaceColor
        clockFacePaint.style = Paint.Style.STROKE
        clockFacePaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, resources.displayMetrics)
    }

    private val listener: OnClockChangedListener = {

    }

    private fun initAttributes(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int){
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ClockView, defStyleAttr, defStyleRes)
        secondHandColor = typedArray.getColor(R.styleable.ClockView_secondHandColor, SECONDHAND_DEFAULT_COLOR)
        minuteHandColor = typedArray.getColor(R.styleable.ClockView_minuteHandColor, MINUTEHAND_DEFAULT_COLOR)
        hourHandColor = typedArray.getColor(R.styleable.ClockView_hourHandColor, HOURHAND_DEFAULT_COLOR)
        clockFaceColor = typedArray.getColor(R.styleable.ClockView_clockFaceColor, CLOCKFACE_DEFAULT_COLOR)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingStart + paddingEnd
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredClockSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DESIRED_CLOCK_SIZE, resources.displayMetrics).toInt()

        val desiredWidth = max(minWidth,desiredClockSizeInPixels + paddingStart + paddingEnd)
        val desiredHeight = max(minHeight,desiredClockSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSize()
    }

    private fun initDefaultColors() {
        secondHandColor = SECONDHAND_DEFAULT_COLOR
        minuteHandColor = MINUTEHAND_DEFAULT_COLOR
        hourHandColor = HOURHAND_DEFAULT_COLOR
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(clock == null) return
        if(fieldRect.width() <= 0) return
        if(fieldRect.height() <= 0) return

        drawClockFace(canvas)
        drawSecondHand(canvas)
        drawMinuteHand(canvas)
        drawHourHand(canvas)
    }

    private fun drawClockFace(canvas: Canvas?){
        val field = this.clock ?: return
        val xStart = fieldRect.left
        val yTop = fieldRect.top
        canvas?.drawCircle(xStart, yTop, clockFaceSize, clockFacePaint)
    }

    private fun drawSecondHand(canvas: Canvas?){
        val currentSeconds = clock?.second ?: 0
        val field = this.clock ?: return
        val xStart = fieldRect.left
        val yStart = fieldRect.top
        val xEnd = (fieldRect.left + clockFaceSize*(cos((0.10472*currentSeconds-PI/2)))).toFloat()
        val yEnd = (fieldRect.top+ clockFaceSize*(sin((0.10472*currentSeconds-PI/2)))).toFloat()
//        val path = Path()
        canvas?.drawLine(xStart, yStart, xEnd, yEnd, secondHandPaint)
    }

    private fun drawMinuteHand(canvas: Canvas?){
        val currentMinutes = clock?.minute ?: 0
        val field = this.clock ?: return
        val xStart = fieldRect.left
        val yStart = fieldRect.top
        val xEnd = (fieldRect.left + clockFaceSize*(cos((0.10472*currentMinutes-PI/2)))/1.5).toFloat()
        val yEnd = (fieldRect.top+ clockFaceSize*(sin((0.10472*currentMinutes-PI/2)))/1.5).toFloat()
        canvas?.drawLine(xStart, yStart, xEnd, yEnd, minuteHandPaint)
    }

    private fun drawHourHand(canvas: Canvas?){
        val currentHours = clock?.hour ?: 0
        val field = this.clock ?: return
        val xStart = fieldRect.left
        val yStart = fieldRect.top
        val xEnd = (fieldRect.left + clockFaceSize*(cos((0.10472*currentHours-PI/2)))/2).toFloat()
        val yEnd = (fieldRect.top+ clockFaceSize*(sin((0.10472*currentHours-PI/2)))/2).toFloat()
        canvas?.drawLine(xStart, yStart, xEnd, yEnd, hourHandPaint)
    }


    private fun updateViewSize (){
        val field = this.clock ?: return

        val safeWidth = width - paddingStart - paddingEnd
        val safeHeight = height - paddingTop - paddingBottom

        fieldRect.left = (paddingStart + safeWidth/2).toFloat()
        fieldRect.top = (paddingTop + safeHeight/2).toFloat()
        fieldRect.right = fieldRect.left + safeWidth
        fieldRect.bottom = fieldRect.bottom + safeHeight
    }

    companion object{
        const val SECONDHAND_DEFAULT_COLOR = Color.RED
        const val MINUTEHAND_DEFAULT_COLOR = Color.YELLOW
        const val HOURHAND_DEFAULT_COLOR = Color.GREEN
        const val CLOCKFACE_DEFAULT_COLOR = Color.GRAY

        const val DESIRED_CLOCK_SIZE = 500f
    }
}