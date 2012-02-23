varying float Blur;
uniform sampler2D Tex0;

void main (void)
{
	gl_FragData[0] = vec4(texture2D(Tex0, gl_TexCoord[0].st).rgb, Blur);
}
