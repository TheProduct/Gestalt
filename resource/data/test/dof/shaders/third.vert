varying vec2 Tap[4], TapNeg[3];
uniform int Width;

void main(void)
{
	vec2 horzTapOffs[7];
	vec2 TexCoord = gl_MultiTexCoord0.st;

	const float dx = 1.0/float(Width);
	horzTapOffs[0] = vec2(0.0, 0.0);
	horzTapOffs[1] = vec2(1.3366 * dx, 0.0);
	horzTapOffs[2] = vec2(3.4295 * dx, 0.0);
	horzTapOffs[3] = vec2(5.4264 * dx, 0.0);
	horzTapOffs[4] = vec2(7.4359 * dx, 0.0);
	horzTapOffs[5] = vec2(9.4436 * dx, 0.0);
	horzTapOffs[6] = vec2(11.4401 * dx, 0.0);

	Tap[0] = TexCoord;
	Tap[1] = TexCoord + horzTapOffs[1];
	Tap[2] = TexCoord + horzTapOffs[2];
	Tap[3] = TexCoord + horzTapOffs[3];

	TapNeg[0] = TexCoord - horzTapOffs[1];
	TapNeg[1] = TexCoord - horzTapOffs[2];
	TapNeg[2] = TexCoord - horzTapOffs[3];

	gl_Position = ftransform();
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
