#version 100

#ifdef GL_ES
    precision mediump float;
#endif

#define PI_TWO 6.28318530718

#define BLUR_DIRECTIONS 16.0
#define BLUR_QUALITY 4.0

uniform sampler2D u_texture;
uniform float u_radius;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);

    for (float d = 0.0; d < PI_TWO; d += PI_TWO / BLUR_DIRECTIONS) {
        for(float i = 1.0 / BLUR_QUALITY; i <= 1.0; i += 1.0 / BLUR_QUALITY) {
            color += texture2D(u_texture, v_texCoords + vec2(cos(d), sin(d)) * u_radius * i);
        }
    }

    color /= BLUR_QUALITY * BLUR_DIRECTIONS;
    gl_FragColor = v_color * color;
}
