#version 100

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

// roughly equals to the amount of rippling in the distortion
#define OMEGA 6.0

uniform sampler2D u_texture;
uniform vec4 u_entries[8]; // x, y, z=time, w=dist_range
uniform int u_num_entries;

varying vec4 v_color;
varying vec2 v_texCoords;

float damped_sine_wave(float t) {
    return DIST_AMPLITUDE * exp(-LAMBDA * abs(t)) * (sin(OMEGA * M_PI * t));
}

void apply(inout vec2 uv, vec4 entry, int idx, int num) {
    if (idx >= num) return;
    uv -= entry.xy;

    float len = length(uv);
    if (len < 0.033) {
        // divions on a value close to zero causes visual glitch in Android in the center of the blast
        len = 0.033;
    }
    vec2 ver = uv / len;

    // the length of the current point gets distorted following a damped sine wave
    float corr = damped_sine_wave(len - entry.z * TIME_FACTOR);
    // the effect of the correction gets further reduced in function of its distance from the center
    uv = ver * (len + corr * clamp(entry.w - length(uv), 0.0, entry.w));

    uv += entry.xy;
}

void main() {
    vec2 uv = v_texCoords;
    // dynamic indexing not supported in GLSL
    apply(uv, u_entries[0], 0, u_num_entries);
    apply(uv, u_entries[1], 1, u_num_entries);
    apply(uv, u_entries[2], 2, u_num_entries);
    apply(uv, u_entries[3], 3, u_num_entries);
    apply(uv, u_entries[4], 4, u_num_entries);
    apply(uv, u_entries[5], 5, u_num_entries);
    apply(uv, u_entries[6], 6, u_num_entries);
    apply(uv, u_entries[7], 7, u_num_entries);
    gl_FragColor = v_color * texture2D(u_texture, uv);
}
