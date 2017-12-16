@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7.task1

/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E

    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)

    operator fun set(cell: Cell, value: E)
}

/**
 * Простая
 *
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> {
    if (height <= 0 || width <= 0) throw IllegalArgumentException()
    val result = MatrixImpl<E>(height, width)
    for (i in 0.until(width))
        for (j in 0.until(height))
            result[i, j] = e
    return result
}

/**
 * Средняя сложность
 *
 * Реализация интерфейса "матрица"
 */
class MatrixImpl<E>(override val height: Int, override val width: Int) : Matrix<E> {

    private val map = mutableMapOf<Cell, E>()

    override fun get(row: Int, column: Int): E = get(Cell(row, column))

    override fun get(cell: Cell): E = map[cell] ?: throw IllegalArgumentException("cell doesn't exist")

    override fun set(row: Int, column: Int, value: E) = set(Cell(row, column), value)

    override fun set(cell: Cell, value: E) = map.set(cell, value)

    override fun equals(other: Any?): Boolean {
        if (!(other is MatrixImpl<*> &&
                height == other.height &&
                width == other.width)) return false
        for (i in 0.until(this.width))
            for (j in 0.until(this.height))
                if (this[j, i] != other[j, i]) return false
        return true
    }
    override fun toString(): String {
        val sb = StringBuilder()
        for (row in 0..height - 1) {
            for (column in 0..width - 1) {
                sb.append(this[row, column])
                sb.append(' ')
            }
            sb.append("\n")
        }
        return "$sb"
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + map.hashCode()
        return result
    }

}
