/**
 * This class compute coefficient of the Jungreis function.
 *
 * Since the Jungreis function corresponds to the complement of the Mandelbrot set from outside the unit circle,
 * putting a point on the unit circle into this function is just the boundary of the Mandelbrot set.
 * This means that the coefficients of the Jungreis function are the Fourier coefficients of the boundary of the Mandelbrot set.
 *
 * References:
 * - JUNGREIS, Irwin. The uniformization of the complement of the Mandelbrot set. 1985.
 * - EWING, John H.; SCHOBER, Glenn. The area of the Mandelbrot set. Numerische Mathematik, 1992, 61.1: 59-72.
 */
class JungreisFunction(numSeries: Int)
{
    private val maxM: Int
    private val maxN: Int
    private val beta: Array<DoubleArray>

    init
    {
        if (numSeries <= 0)
        {
            throw IllegalArgumentException()
        }
        maxM = numSeries
        maxN = Integer.SIZE - 1 - Integer.numberOfLeadingZeros(numSeries) + 1 // maxM <= 2^maxN - 1
        beta = Array(maxN) { DoubleArray(maxM) }
    }

    fun calculateCoeff()
    {
        for (n in 0 until maxN)
        { // m = 0
            beta[n][0] = 1.0
        }
        for (m in 1 until maxM)
        { // m >= 1
            for (n in maxN downTo 1)
            {
                val a = 1 shl n // 2^n
                if (m <= a - 2)
                { // m <= 2^n - 2
                    beta[n - 1][m] = 0.0
                }
                else
                { // m >= 2^n - 1; n < maxN because m <= maxM - 1 <= 2^maxN - 2
                    var v = beta[n][m]
                    for (k in a - 1..m - a + 1)
                    {
                        v -= beta[n - 1][k] * beta[n - 1][m - k]
                    }
                    v -= beta[0][m - a + 1]
                    beta[n - 1][m] = 0.5 * v
                }
            }
        }
    }

    val coeff: DoubleArray
        get() = beta[0]
}
