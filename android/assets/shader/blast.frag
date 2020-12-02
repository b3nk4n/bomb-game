#ifdef GL_ES
    precision mediump float;
#endif

// affects the distorsion speed
#define TIME_FACTOR 2.0

// PI constant
#define M_PI 3.1415926535897932384626433832795

// maximum distorsion distance
#define DIST_RANGE 0.5

// maximum distorsion amplitude
#define A 0.1

//dampening factor
#define LAMBDA 20.0

//roughly equals to the amount of rippling in the distorsion
#define OMEGA 8.0

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_center_uv;

varying vec4 v_color;
varying vec2 v_texCoords;

float damped_sine_wave(float t) {
    return A * exp(-LAMBDA * abs(t)) * (sin(OMEGA * M_PI * t));
}

void main() {
	vec2 uv = v_texCoords;
    uv -= u_center_uv;
    float len = length(uv);
    vec2 ver = uv / len;
    //first correction: the length of the current point gets distorted following a damped sine wave.
    float corr = damped_sine_wave( len - u_time * TIME_FACTOR );
    //second correction: the effect of the correction gets further reduced in function of its distance from the center.
    uv = ver * (len + corr * clamp(DIST_RANGE - length(uv), 0.0, DIST_RANGE));
    uv += u_center_uv;
    gl_FragColor = v_color * texture2D(u_texture, uv);
}
