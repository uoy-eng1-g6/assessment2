#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform vec4 u_color;

const float outline = 1.0 / 256.0;

void main() {
  vec4 fragColor = v_color * texture2D(u_texture, v_texCoords);

  // sum the alphas of all adjacent fragments, trying to see if we're next to a non empty fragment
  float a =
    texture2D(u_texture, vec2(v_texCoords.x + outline, v_texCoords.y)).a +
		texture2D(u_texture, vec2(v_texCoords.x - outline, v_texCoords.y)).a +
		texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + outline)).a +
		texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - outline)).a +
		texture2D(u_texture, vec2(v_texCoords.x + outline, v_texCoords.y + outline)).a +
		texture2D(u_texture, vec2(v_texCoords.x - outline, v_texCoords.y - outline)).a +
		texture2D(u_texture, vec2(v_texCoords.x + outline, v_texCoords.y - outline)).a +
		texture2D(u_texture, vec2(v_texCoords.x - outline, v_texCoords.y + outline)).a;

  if (fragColor.a == 0 && a > 0.0)
    // give the fragment the color inputted in the game if its an outline
    gl_FragColor = u_color;
  else
    // give it the default fragment colour otherwise
    gl_FragColor = fragColor;
}
