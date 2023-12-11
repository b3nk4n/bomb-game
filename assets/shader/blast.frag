#ifdef GL_ES
    precision mediump float;
#endif

// affects the distortion speed
#define TIME_FACTOR 2.0

// PI constant
#define M_PI 3.1415926535897932384626433832795

// maximum distortion amplitude
#define DIST_AMPLITUDE 0.33

// dampening factor
#define LAMBDA 16.0

// roughly equals camerato the amount of rippling in the distortion
#define OMEGA 6.0

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_center_uv;
uniform float u_dist_range;

varying vec4 v_color;
varying vec2 v_texCoords;

float damped_sine_wave(float t) {
    return DIST_AMPLITUDE * exp(-LAMBDA * abs(t)) * (sin(OMEGA * M_PI * t));
}

void main() {
	vec2 uv = v_texCoords;
    uv -= u_center_uv;

    float len = length(uv);
    if (len < 0.033) {
        // divions on a value close to zero causes visual glitch in Android in the center of the blast
        len = 0.033;
    }
    vec2 ver = uv / len;

    // the length of the current point gets distorted following a damped sine wave
    float corr = damped_sine_wave( len - u_time * TIME_FACTOR );
    // the effect of the correction gets further reduced in function of its distance from the center
    uv = ver * (len + corr * clamp(u_dist_range - length(uv), 0.0, u_dist_range));

    uv += u_center_uv;
    gl_FragColor = v_color * texture2D(u_texture, uv);
}
