import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape

class Main() : PApplet()
{
    private lateinit var mandelbrot: PGraphics

    override fun settings()
    {
        size(640, 640, P3D)
    }

    override fun setup()
    {
        background(0)

        // load shader for Mandelbrot
        val vert = javaClass.getResource("mandelbrot.vert")!!.path
        val frag = javaClass.getResource("mandelbrot.frag")!!.path
        val mandelbrotShader = loadShader(frag, vert)
        mandelbrotShader.set("range", -2.5f, -2.0f, 1.5f, 2.0f)

        // create Mandelbrot image
        val resolutionScale = 2
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
    }

    override fun draw()
    {
        background(0)
        image(mandelbrot, 0.0f, 0.0f, width.toFloat(), height.toFloat())
    }
}

fun main()
{
    PApplet.main(Main::class.java)
}
