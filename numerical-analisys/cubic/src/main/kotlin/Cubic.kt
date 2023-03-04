import kotlin.math.sqrt

data class Root(val value: Double, val degree: Int)

abstract class Polynomial(private val epsilon: Double) {

    abstract fun valueAt(x: Double): Double

    protected fun findRoot(a: Double, b: Double): List<Root> {
        var left = a
        var right = b
        when {
            a == Double.NEGATIVE_INFINITY -> {
                val (l, r) = locateLeftBorder(b, 1.0)
                left = l
                right = r
            }

            b == Double.POSITIVE_INFINITY -> {
                val (l, r) = locateRightBorder(a, 1.0)
                left = l
                right = r
            }
        }

        if (equalsZero(valueAt(left))) {
            return List(1){Root(left, 1)}
        }
        if (equalsZero(valueAt(right))) {
            return List(1){Root(right, 1)}
        }

        var c = (left + right)/2

        while(!equalsZero(valueAt(c))) {
            c = (left + right)/2
            if (valueAt(left)-valueAt(right) > 0) {
                when {
                    valueAt(c) < -epsilon -> right = c
                    valueAt(c) > epsilon -> left = c
                }
            } else {
                when {
                    valueAt(c) < -epsilon -> left = c
                    valueAt(c) > epsilon -> right = c
                }
            }
        }
        return List(1){Root(c, 1)}
    }
    private fun locateRightBorder(b: Double, delta: Double) : Pair<Double, Double> {
        var left = b
        var right = b + delta

        while (valueAt(left)*valueAt(right) > 0) {
            left = right
            right += delta
        }
        return Pair(left, right)
    }
    private fun locateLeftBorder(a: Double, delta: Double) : Pair<Double, Double> {
        var left = a - delta
        var right = a

        while (valueAt(left)*valueAt(right) > 0) {
            right = left
            left -= delta
        }
        return Pair(left, right)
    }
    protected fun equalsZero(x: Double) = x < epsilon && x >-epsilon
}

class Cubic (private val a: Double, private val b: Double, private val c: Double, private val d: Double,
             private val epsilon: Double) : Polynomial(epsilon) {
    private val derivative = Quadratic(3*a, 2*b, c, epsilon)

    fun solve(): List<Root> {
        return when {
            equalsZero(derivative.discriminant) -> zeroDiscriminant()
            derivative.discriminant > epsilon -> positiveDiscriminant()
            derivative.discriminant < epsilon -> negativeDiscriminant()
            else -> zeroDiscriminant()
        }
    }

    private fun positiveDiscriminant(): List<Root> {
        val (derivativeRoot1, derivativeRoot2) = derivative.solve()
        val r1 = derivativeRoot1?.value
        val r2 = derivativeRoot2?.value
        if (valueAt(r2!!) > epsilon) {
            return findRoot(Double.NEGATIVE_INFINITY, r1!!)
        }
        if (valueAt(r2) < -epsilon) {
            if (equalsZero(valueAt(r1!!))) {
                return List(1){Root(r1, 2)} + findRoot(r2, Double.POSITIVE_INFINITY)
            }
            return findRoot(Double.NEGATIVE_INFINITY, r1) + findRoot(r1, r2) + findRoot(r2, Double.POSITIVE_INFINITY)
        }
        return findRoot(Double.NEGATIVE_INFINITY, r1!!) + Root(r2, 2)
    }

    private fun negativeDiscriminant(): List<Root> {
        if (valueAt(0.0) > epsilon) {
            return findRoot(Double.NEGATIVE_INFINITY, 0.0)
        }
        if (valueAt(0.0) < -epsilon) {
            return findRoot(0.0, Double.POSITIVE_INFINITY)
        }
        return List(1){Root(0.0, 1)}
    }

    private fun zeroDiscriminant(): List<Root> {
        val derivativeRoot = derivative.solve().first?.value!!
        if (valueAt(derivativeRoot) > epsilon) {
            return findRoot(Double.NEGATIVE_INFINITY, derivativeRoot)
        }
        if (valueAt(derivativeRoot) < -epsilon) {
            return findRoot(derivativeRoot, Double.POSITIVE_INFINITY)

        }
        return List(1){Root(derivativeRoot, 3)}
    }

    override fun valueAt(x: Double): Double {
        return a*x*x*x + b*x*x + c*x + d
    }
}

class Quadratic (private val a: Double, private val b: Double, private  val c: Double,
                 private val epsilon: Double) : Polynomial(epsilon) {
    val discriminant: Double = b*b - 4*a*c
    fun solve(): Pair<Root?, Root?> = when {
            discriminant > epsilon -> Pair(Root((-b - sqrt(b*b-4*a*c))/(2 * a), 1),
                Root((-b + sqrt(b*b-4*a*c))/(2*a), 1))
            discriminant < -epsilon -> Pair(null, null)
            else -> Pair(Root(-b/(2*a), 2), null)
        }

    override fun valueAt(x: Double): Double {
        return a*x*x + b*x + c
    }
}

fun test() {
    val epsilon = 0.000000001
    repeat(1000) {
        val x1 = (-100..100).random()
        val x2 = (-100..100).random()
        val x3 = (-100..100).random()
        val a = 1.0
        val b: Double = (-(x1 + x2 + x3)).toDouble()
        val c: Double = (x1*x2 + x2*x3 + x3*x1).toDouble()
        val d: Double = (-x1*x2*x3).toDouble()
        val cubic = Cubic(a, b, c, d, epsilon)
        print("$x1 $x2 $x3: ")
        println(cubic.solve())
    }
}

fun main() {
//    test()
    val epsilon: Double = 0.00000001
    val a = readln().toDouble()
    val b = readln().toDouble()
    val c = readln().toDouble()
    val d = readln().toDouble()
    val polynomial = Cubic(a, b, c, d, epsilon)
    polynomial.solve().forEach { println("$it") }
}