package aaa.ccc.gluttonoussnakegame

data class PointBean(
        val x: Int,
        val y: Int
)


data class SnackBean(
        val snack: MutableList<PointBean> = mutableListOf()
)

class GridBean {


    /**
     * 寬高（正方形）
     */
    var size = 0

    /**
     * 格子間距
     */
    var offSet = 20

    /**
     * 格子數
     */
    val gridSize = 30

    /**
     * 線長
     */
    var lineLength = 0

    /**
     * 格子寬
     */
    var gridWidth :Int = 0

    init {
        lineLength = size - offSet * 2
        gridWidth = lineLength / gridSize

    }
}

enum class Control {
    UP, DOWN, LEFT, RIGHT
}

enum class GameStatus {
    PLAY, WIN, LOSE
}