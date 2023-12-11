#ifdef GL_ES
    precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_vignetteIntensity;
uniform float u_vignetteX;
uniform float u_vignetteY;
uniform float u_centerX;
uniform float u_centerY;

varying vec2 v_texCoords;

void main() {
    vec3 rgb = texture2D(u_texture, v_texCoords).xyz;
    float d = distance(v_texCoords, vec2(u_centerX, u_centerY));
    float factor = smoothstep(u_vignetteX, u_vignetteY, d);
    rgb = rgb * factor + rgb * (1.0 - factor) * (1.0 - u_vignetteIntensity);
    gl_FragColor = vec4(rgb, 1);
}