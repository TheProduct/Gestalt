uniform sampler2DRect data_buffer;

void main()
{
	/* read the float data from our buffer */
	vec4 mData = texture2DRect(data_buffer, gl_FragCoord.xy);
	/* set the fragement color to float data */
	gl_FragColor = vec4(mData.xy, 1.0, 1.0);
}
