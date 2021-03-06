@file:Suppress("UNUSED_PARAMETER")

package lesson6.task1

import lesson1.task1.sqr
import java.lang.Math.*

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double = if (center.distance(other.center) > radius + other.radius)
        center.distance(other.center) - (radius + other.radius)
    else 0.0

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    fun center(): Point = Point((begin.x + end.x) / 2, (begin.y + end.y) / 2)
    fun length(): Double = sqrt(sqr(end.x - begin.x) + sqr(end.y - begin.y))
    override fun equals(other: Any?) =
            other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
            begin.hashCode() + end.hashCode()
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    var max = -1.0
    var begin = Point(0.0, 0.0)
    var end = Point(0.0, 0.0)
    if (points.size < 2) throw IllegalArgumentException()
    for (i in 0 until points.size)
        for (j in i + 1 until points.size) if (points[i].distance(points[j]) > max) {
            max = points[i].distance(points[j])
            begin = points[i]
            end = points[j]
        }
    return Segment(begin, end)
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle = Circle(diameter.center(), diameter.length() / 2)

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        assert(angle >= 0 && angle < Math.PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * Math.cos(angle) - point.x * Math.sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        val x = (other.b * cos(angle) - b * cos(other.angle)) / sin(angle - other.angle)
        val y = if (abs(PI / 2 - angle) > abs(PI / 2 - other.angle)) tan(angle) * x +
                b * (cos(angle) + tan(angle) * sin(angle))
        else tan(other.angle) * x + other.b * (cos(other.angle) + tan(other.angle) * sin(other.angle))
        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${Math.cos(angle)} * y = ${Math.sin(angle)} * x + $b)"
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = Line(s.begin, atan((s.end.y - s.begin.y) / (s.end.x - s.begin.x)) % PI)

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    var angle = atan2((b.y - a.y), (b.x - a.x))
    if (angle >= PI) angle -= PI
    else if (angle < 0) angle += PI
    return Line(a, angle)
}

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    var angle = lineByPoints(a, b).angle + PI / 2
    if (angle >= PI) angle -= PI
    else if (angle < 0) angle += PI
    return Line(Point((a.x + b.x) / 2, (a.y + b.y) / 2), angle)
}


/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    var angle1 = atan2((b.y - a.y), (b.x - a.x))
    if (angle1 >= PI) angle1 -= PI
    else if (angle1 < 0) angle1 += PI
    var angle2 = atan2((c.y - b.y), (c.x - b.x))
    if (angle2 >= PI) angle2 -= PI
    else if (angle2 < 0) angle2 += PI
    val center = bisectorByPoints(a, b).
            crossPoint(bisectorByPoints(b, c))
    return Circle(center, center.distance(a))
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */

fun minContainingCircle(vararg points: Point): Circle {
    var resCircle: Circle
    when (points.size) {
        0 -> throw IllegalArgumentException()
        1 -> return Circle(points[0], 0.0)
        2 -> return circleByDiameter(Segment(points[0], points[1]))
    }
    val list = mutableListOf(0)
    var index = 0
    var new = 0
    var max = -1.0
    for (i in 1.until(points.size))
        if (Segment(points[0], points[i]).length() > max) {
            max = Segment(points[0], points[i]).length()
            index = i
        }
    list.add(index)
    resCircle = circleByDiameter(Segment(points[0], points[list[1]]))
    do {
        val rad = resCircle.radius
        max = -1.0
        for (i in 0.until(points.size))
            if (Segment(resCircle.center, points[i]).length() > max) {
                max = Segment(resCircle.center, points[i]).length()
                new = i
            }
        if (resCircle.contains(points[new])) break
        var mostDist1 = list[0]
        max = -1.0
        for (i in 0.until(list.size))
            if (Segment(points[new], points[list[i]]).length() > max) {
                max = Segment(points[new], points[list[i]]).length()
                mostDist1 = list[i]
            }
        resCircle = circleByDiameter(Segment(points[new], points[mostDist1]))
        if (list.size > 2) {
            var nearCenter = 0
            var min = Segment(resCircle.center, points[list[0]]).length()
            for (i in 0.until(list.size)) {
                if (Segment(resCircle.center, points[list[i]]).length() < min) {
                    min = Segment(resCircle.center, points[list[i]]).length()
                    nearCenter = i
                }
            }
            list.remove(list[nearCenter])
        }
        val spare = if (list[0] != mostDist1) 0
        else 1
        if (resCircle.contains(points[list[spare]])) list[spare] = new
        else {
            list.add(new)
            resCircle = circleByThreePoints(points[list[0]], points[list[1]], points[list[2]])
            var mostDist3 = 0
            var mostDist4 = 0
            var max = -1.0
            for (j in 0..2)
                for (i in 0 until points.size)
                    if (Segment(points[i], points[list[j]]).length() > max) {
                        max = Segment(points[i], points[list[j]]).length()
                        mostDist3 = i
                        mostDist4 = j
                    }
            var temp = circleByDiameter(Segment(points[mostDist3], points[mostDist4]))
            if (temp.radius < resCircle.radius && points.all {temp.contains(it)}) resCircle = temp
        }
    } while (rad != resCircle.radius)
    return resCircle
}

