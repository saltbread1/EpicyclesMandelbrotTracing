#version 410

in vec2 vTexCoord;
out vec4 fragColor;

uniform vec4 range; // (min.x, min.y, max.x, max.y)

const int MAX_ITERATIONS = 200;
const float BAILOUT_RADIUS = 5.0;
const float INV_LOG2 = 1. / log(2.);

vec3 getColor(float val)
{
    return vec3((cos(val*6.4-3.9)+1.)/2., (cos(val*6.4-2.8)+1.)/2., (cos(val*6.4-1.7)+1.)/2.);
}

void main()
{
    vec2 uv = vTexCoord;
    uv = range.xy * (1.0 - uv) + range.zw * uv;

    vec2 z = vec2(0);
    float iterations;
    for (int i = 0; i < MAX_ITERATIONS; i++)
    {
        iterations++;
        z = vec2(z.x * z.x - z.y * z.y + uv.x, 2.0 * z.x * z.y + uv.y);
        if (dot(z, z) > BAILOUT_RADIUS * BAILOUT_RADIUS) { break; }
    }

    vec3 col = vec3(0);
    if (iterations < float(MAX_ITERATIONS))
    {
        float log_zn = log(dot(z, z)) * 0.5;
        float nu = log(log_zn * INV_LOG2) * INV_LOG2;
        iterations = iterations + 1. - nu;
        col = mix(getColor(floor(iterations)), getColor(floor(iterations+1.)), fract(iterations));
    }

    fragColor = vec4(col, 1);
}
