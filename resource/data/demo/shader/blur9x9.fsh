uniform sampler2D textureunit;
uniform float     direction;
uniform vec2      texOffset;
uniform float     strength;
uniform float     spread;


void main(void)
{
  vec4 blurFilter[9];
  vec4 finalColor = vec4(0.0,0.0,0.0,0.0);

  float xOffset    = spread * direction * texOffset.x;
  float yOffset    = spread * ( 1.0 - direction ) * texOffset.y;

  blurFilter[0]  = vec4( 4.0*xOffset, 4.0*yOffset, 0.0, 0.0217);
  blurFilter[1]  = vec4( 3.0*xOffset, 3.0*yOffset, 0.0, 0.0434);
  blurFilter[2]  = vec4( 2.0*xOffset, 2.0*yOffset, 0.0, 0.0869);
  blurFilter[3]  = vec4( 1.0*xOffset, 1.0*yOffset, 0.0, 0.1739);
  blurFilter[4]  = vec4(         0.0,         0.0, 0.0, 0.3478);
  blurFilter[5]  = vec4(-1.0*xOffset,-1.0*yOffset, 0.0, 0.1739);
  blurFilter[6]  = vec4(-2.0*xOffset,-2.0*yOffset, 0.0, 0.0869);
  blurFilter[7]  = vec4(-3.0*xOffset,-3.0*yOffset, 0.0, 0.0434);
  blurFilter[8]  = vec4(-4.0*xOffset,-4.0*yOffset, 0.0, 0.0217);

  for (int i = 0;i< 9;i++) {
	finalColor += texture2D(textureunit, gl_TexCoord[0].st + blurFilter[i].xy) * blurFilter[i].w;
  }

  gl_FragColor = finalColor*strength;
}
