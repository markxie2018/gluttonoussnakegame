package aaa.ccc.gluttonoussnakegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.lang.Math.min

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    lateinit var gridBean: GridBean
    lateinit var snackBean: SnackBean
    lateinit var pointBean: PointBean
    lateinit var paint: Paint
    lateinit var paintSnack: Paint
    lateinit var paintFood: Paint
    lateinit var paintText: Paint
    lateinit var paintPoint: Paint


    var isEattingFood = false
    var isAddFood = false
    var foodCount = 0
    var range = 0
    var grid = mutableListOf<PointBean>()
    val winText = "魔王，你贏惹！"
    val fialedText = "連我阿嬤都比你強！"
    lateinit var rect: Rect

    /**
     * 控制方向按鈕
     */
    var control = Control.UP

    /**
     * 控制鍵與蛇移動有時間差
     * 此參數實際蛇頭的方向
     */
    private var realDirection = Control.UP

    /**
     * 隨機食物的點
     */
    var finalFood: PointBean? = null

    /**
     * 遊戲狀態
     */
    var gameStatus = GameStatus.PLAY


    init {

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        paintSnack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }

        paintFood = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
        }

        paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
//            style = Paint.Style.STROKE
            textSize = 80f
        }

        paintPoint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
//            style = Paint.Style.STROKE
            strokeWidth = 20f
        }


        rect = Rect()

        gridBean = GridBean()
        snackBean = SnackBean()
        val pointBean = PointBean(
                gridBean.gridSize / 2, gridBean.gridSize / 2
        )
        snackBean.snack.add(pointBean)
        range = gridBean.gridSize * gridBean.gridSize
        for (i in 0 until gridBean.gridSize) {
            for (c in 0 until gridBean.gridSize) {
                grid.add(PointBean(i, c))
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        gridBean.size = size

        setMeasuredDimension(gridBean.size, gridBean.size)
        gridBean.run {

            lineLength = size - offSet * 2
            gridWidth = lineLength / gridSize

            /**
             * 寬度除以 gridSize 為 int,
             * 若小數點很大會造成 gameView 不置中，重新計算
             */
            lineLength = gridWidth * gridSize
            offSet = (size - lineLength) / 2
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if (gameStatus == GameStatus.PLAY) {
            drawGrid(canvas)
            drawSnack(canvas)
            drawFood(canvas)
        }

        drawWall(canvas)
        drawText(canvas)

    }

    /**
     * 畫外框
     */
    private fun drawWall(canvas: Canvas) {
        paint.strokeWidth = 5f
        paint.color = Color.BLACK
        canvas.drawRect(
                gridBean.offSet.toFloat(),
                gridBean.offSet.toFloat(),
                gridBean.offSet + gridBean.gridWidth * (gridBean.gridSize).toFloat(),
                gridBean.offSet + gridBean.gridWidth * (gridBean.gridSize).toFloat(),
                paint)

    }

    /**
     * 畫格線
     */
    private fun drawGrid(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.strokeWidth = 1f

        for (c in 0 until gridBean.gridSize) {

            /**
             * 畫橫線
             */
            val startX = gridBean.offSet + gridBean.gridWidth * c
            val stopX = startX
            val startY = gridBean.offSet
            val stopY = startY + gridBean.lineLength

            canvas.drawLine(startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(), paint)

            /**
             * 畫直線
             */
            val startXX = gridBean.offSet
            val stopXX = startXX + gridBean.lineLength
            val startYY = gridBean.offSet + gridBean.gridWidth * c
            val stopYY = startYY

            canvas.drawLine(startXX.toFloat(),
                    startYY.toFloat(),
                    stopXX.toFloat(),
                    stopYY.toFloat(), paint)
        }

    }


    /**
     * 畫蛇
     */
    private fun drawSnack(canvas: Canvas) {
        snackBean.snack.forEach {

            val startX = gridBean.offSet + gridBean.gridWidth * it.x
            val stopX = startX + gridBean.gridWidth
            val startY = gridBean.offSet + gridBean.gridWidth * it.y
            val stopY = startY + gridBean.gridWidth
            canvas.drawRect(startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(), paintSnack)
        }

    }


    fun refreshView(): Boolean {
        val snack = snackBean.snack
        val point = snack[0]
        val newPoint: PointBean
        when (control) {
            Control.UP -> {
                if (realDirection != Control.DOWN) {
                    newPoint = PointBean(point.x, point.y - 1)
                    realDirection = Control.UP
                } else {
                    newPoint = PointBean(point.x, point.y + 1)
                }
            }
            Control.DOWN -> {
                if (realDirection != Control.UP) {
                    newPoint = PointBean(point.x, point.y + 1)
                    realDirection = Control.DOWN
                } else {
                    newPoint = PointBean(point.x, point.y - 1)
                }
            }
            Control.LEFT -> {
                if (realDirection != Control.RIGHT) {
                    newPoint = PointBean(point.x - 1, point.y)
                    realDirection = Control.LEFT
                } else {
                    newPoint = PointBean(point.x + 1, point.y)
                }
            }
            Control.RIGHT -> {
                if (realDirection != Control.LEFT) {
                    newPoint = PointBean(point.x + 1, point.y)
                    realDirection = Control.RIGHT
                } else {
                    newPoint = PointBean(point.x - 1, point.y)
                }
            }
        }

        snack.add(0, newPoint)
        if (!snackEatFood(newPoint)) {
            snack.removeAt(snack.size - 1)
        }


        if (isFailed(point)) {
            gameStatus = GameStatus.LOSE
            invalidate()
            return true
        }

        invalidate()
        return false
    }


    private fun isFailed(point: PointBean): Boolean {
        when {
            point.y == 0 && realDirection == Control.UP -> return true
            point.x == 0 && realDirection == Control.LEFT -> return true
            point.y == gridBean.gridSize - 1 && realDirection == Control.DOWN -> return true
            point.x == gridBean.gridSize - 1 && realDirection == Control.RIGHT -> return true
            else -> {
                return checkImpact(point)
            }
        }
    }

    fun gameReset() {
        gameStatus = GameStatus.PLAY
        val pointBean = PointBean(
                gridBean.gridSize / 2, gridBean.gridSize / 2
        )
        snackBean.snack.clear()
        snackBean.snack.add(pointBean)
        control = Control.UP
        realDirection = Control.UP

        foodCount = 0
        isAddFood = false


        invalidate()

    }


    /**
     * 畫食物
     */
    private fun drawFood(canvas: Canvas) {
        L.e("drawFood")
        //還沒有加食物時
        //隨機加一格食物
        if (!isAddFood) {

            val gridTmp = mutableListOf<PointBean>()
            gridTmp.addAll(grid)
            snackBean.snack.forEach {
                gridTmp.remove(it)
            }
            L.e("snackBean.snack = ${snackBean.snack}")
            L.e("gridTmp size = ${gridTmp.size}")
            L.e("grid size = ${grid.size}")

            if (gridTmp.isEmpty()) {
                L.e("魔王，你贏惹")
                gameStatus = GameStatus.WIN
                return
            }

            val c = (Math.random() * (gridTmp.size - 1)).toInt()
            finalFood = gridTmp[c]

            finalFood?.let {
                val startX = gridBean.offSet + gridBean.gridWidth * it.x
                val stopX = startX + gridBean.gridWidth
                val startY = gridBean.offSet + gridBean.gridWidth * it.y
                val stopY = startY + gridBean.gridWidth
                canvas.drawRect(startX.toFloat(),
                        startY.toFloat(),
                        stopX.toFloat(),
                        stopY.toFloat(), paintFood)
            }
            isAddFood = true
            foodCount++
        } else {
            //有食物時
            //分成被吃跟還沒被吃

            if (!isEattingFood) {
                finalFood?.let {
                    val startX = gridBean.offSet + gridBean.gridWidth * it.x
                    val stopX = startX + gridBean.gridWidth
                    val startY = gridBean.offSet + gridBean.gridWidth * it.y
                    val stopY = startY + gridBean.gridWidth
                    canvas.drawRect(startX.toFloat(),
                            startY.toFloat(),
                            stopX.toFloat(),
                            stopY.toFloat(), paintFood)
                }
            }
        }
    }

    /**
     * 判斷蛇是否吃到食物
     */
    private fun snackEatFood(point: PointBean): Boolean {
        if (finalFood == null) {
            return false
        }
        finalFood?.let {
            if (point.x == it.x && point.y == it.y) {
                isAddFood = false
                return true
            }
        }
        return false
    }

    /**
     * 判斷蛇撞到自己的身體
     */
    private fun checkImpact(point: PointBean): Boolean {
        if (snackBean.snack.size < 5) return false

        var isImpact = false
        snackBean.snack.filterIndexed { i, it ->
            i > 3
        }.forEach {
            when {
                realDirection == Control.UP -> {
                    if (point.x == it.x && point.y == it.y + 1) {
                        isImpact = true
                    }
                }

                realDirection == Control.DOWN -> {
                    if (point.x == it.x && point.y == it.y - 1) {
                        isImpact = true
                    }
                }

                realDirection == Control.LEFT -> {
                    if (point.x == it.x + 1 && point.y == it.y) {
                        isImpact = true
                    }
                }

                realDirection == Control.RIGHT -> {
                    if (point.x == it.x - 1 && point.y == it.y) {
                        isImpact = true
                    }
                }
            }


        }
        return isImpact
    }

    /**
     * 畫成功或失敗的文字
     */
    private fun drawText(canvas: Canvas) {
        if (gameStatus == GameStatus.PLAY) return

        val text: String = when (gameStatus) {
            GameStatus.WIN -> {
                winText
            }
            GameStatus.LOSE -> {
                fialedText
            }
            else -> {
                ""
            }
        }

        paintText.getTextBounds(text, 0, text.length, rect)
        val textW = rect.right - rect.left
        val textH = Math.abs(rect.bottom) - Math.abs(rect.top)
        val offsetW = (width - textW) / 2f
        val offsetH = ((height - textH) / 2).toFloat()

        canvas.save()
        canvas.translate(offsetW, offsetH)

        //畫灰框
        paintText.color = Color.GRAY
        canvas.drawRoundRect(
                -60f,
                60f,
                textW.toFloat() + 60f,
                textH.toFloat() - 60f,
                20f,
                20f,
                paintText)

        //畫白底
        paintText.color = Color.WHITE
        canvas.drawRoundRect(
                -50f,
                50f,
                textW.toFloat() + 50f,
                textH.toFloat() - 50f,
                20f,
                20f,
                paintText)
        paintText.color = Color.BLACK
        canvas.drawText(text, 0f, 0f, paintText)


        canvas.restore()


    }

}