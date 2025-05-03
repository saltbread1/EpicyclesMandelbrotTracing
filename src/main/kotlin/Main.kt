import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape
import processing.core.PVector

class Main : PApplet()
{
    companion object
    {
        // number of cycle which equals to number of terms of Fourier series
        private const val NUM_CYCLES = 8192

        // range of drawing Mandelbrot set
        private const val X_MIN = -2.1f
        private const val Y_MIN = -1.5f
        private const val X_MAX =  0.9f
        private const val Y_MAX =  1.5f

        private const val DELTA_TIME = 1.0e-5f
        private const val DISPLAY_INTERVAL = 50
        private const val TOTAL_TIME = 1.0f

        private const val PIXEL_DENSITY = 2
    }

    private lateinit var mandelbrot: PGraphics
    private lateinit var fourierCoeff: DoubleArray
    private var time: Float = 0.0f
    private val boundaryPos: MutableList<PVector> = mutableListOf()

    override fun settings()
    {
        size(640, 640, P3D)
        pixelDensity(PIXEL_DENSITY)
    }

    override fun setup()
    {
        frameRate(30.0f)
        ortho(X_MIN, X_MAX, Y_MIN, Y_MAX, -1.0f, 1.0f)
        camera(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

        // load shader for Mandelbrot
        val vert = javaClass.getResource("mandelbrot.vert")!!.path
        val frag = javaClass.getResource("mandelbrot.frag")!!.path
        val mandelbrotShader = loadShader(frag, vert)
        mandelbrotShader.set("range", X_MIN, Y_MIN, X_MAX, Y_MAX)

        // create Mandelbrot image
        val resolutionScale = PIXEL_DENSITY
        val w = width * resolutionScale
        val h = height * resolutionScale
        val rect: PShape = createShape()
        rect.beginShape()
        rect.vertex(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        rect.vertex(0.0f, h.toFloat(), 0.0f, 0.0f, 1.0f)
        rect.vertex(w.toFloat(), h.toFloat(), 0.0f, 1.0f, 1.0f)
        rect.vertex(w.toFloat(), 0.0f, 0.0f, 1.0f, 0.0f)
        rect.endShape()
        rect.setStrokeWeight(0.0f)
        mandelbrot = createGraphics(w, h, P3D)
        mandelbrot.beginDraw()
        mandelbrot.shader(mandelbrotShader)
        mandelbrot.shape(rect)
        mandelbrot.endDraw()

        // get Jungreis coefficients
        val jungreis = JungreisFunction(NUM_CYCLES)
        jungreis.calculateCoeff()
        fourierCoeff = jungreis.coeff
    }

    override fun draw()
    {
        if (time > TOTAL_TIME)
        {
            return
        }

        background(0)
        image(mandelbrot, X_MIN, Y_MIN, X_MAX - X_MIN, Y_MAX - Y_MIN)

        // calculate boundary position
        for (i in 0 until DISPLAY_INTERVAL)
        {
            update()
        }

        noFill()

        // draw epicycles
        pushStyle()
        val pos = PVector()
        for (i in 0 until NUM_CYCLES)
        {
            val degree = 1 - i
            stroke(0x80dedab0.toInt())
            circle(pos.x, pos.y, fourierCoeff[i].toFloat() * 2.0f)
            val prev = PVector(pos.x, pos.y)
            pos.add(PVector.fromAngle(time * degree * TAU).mult(fourierCoeff[i].toFloat()))
            stroke(0xfffff100.toInt())
            line(prev.x, prev.y, pos.x, pos.y)
        }
        popStyle()

        // draw boundary
        pushStyle()
        stroke(0f)
        strokeWeight(2.0f)
        for (i in 1 until boundaryPos.size)
        {
            val pos1 = boundaryPos[i - 1]
            val pos2 = boundaryPos[i]
            line(pos1.x, pos1.y, pos2.x, pos2.y)
        }
        popStyle()
    }

    private fun update()
    {
        val pos = PVector()
        for (i in 0 until NUM_CYCLES)
        {
            val degree = 1 - i
            pos.add(PVector.fromAngle(time * degree * TAU).mult(fourierCoeff[i].toFloat()))
        }
        boundaryPos.add(pos)

        time += DELTA_TIME
    }
}

fun main()
{
    PApplet.main(Main::class.java)
}
