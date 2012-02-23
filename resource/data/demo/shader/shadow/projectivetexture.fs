uniform sampler2D projMap;
void main()
{
	gl_FragColor = vec4(gl_Color);

	if (gl_TexCoord[1].q > 0.0) { // prevent reverse projection
		vec4 myTexColor = texture2DProj(projMap, gl_TexCoord[1]);
		gl_FragColor += vec4(myTexColor.xyz, 1);
	}
}