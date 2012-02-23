uniform sampler2D Tex0;

void main (void)
{
	gl_FragData[0] = texture2D(Tex0, gl_TexCoord[0].st);
}
