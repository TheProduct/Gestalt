uniform sampler2D textureunit;
uniform sampler2D colortexture;
uniform float offset[16];
uniform float weight[16];
uniform float spread;

vec4 rgb_to_hsv(vec4 rgb)
{
	float cmax, cmin, h, s, v, cdelta;
	vec3 c;

	cmax = max(rgb[0], max(rgb[1], rgb[2]));
	cmin = min(rgb[0], min(rgb[1], rgb[2]));
	cdelta = cmax-cmin;

	v = cmax;
	if (cmax!=0.0)
		s = cdelta/cmax;
	else {
		s = 0.0;
		h = 0.0;
	}

	if (s == 0.0) {
		h = 0.0;
	}
	else {
		c = (vec3(cmax, cmax, cmax) - rgb.xyz)/cdelta;

		if (rgb.x==cmax) h = c[2] - c[1];
		else if (rgb.y==cmax) h = 2.0 + c[0] -  c[2];
		else h = 4.0 + c[1] - c[0];

		h /= 6.0;

		if (h<0.0)
			h += 1.0;
	}

	return vec4(h, s, v, rgb.w);
}

vec4 hsv_to_rgb(vec4 hsv)
{
	float i, f, p, q, t, h, s, v;
	vec3 rgb;

	h = hsv[0];
	s = hsv[1];
	v = hsv[2];

	if(s==0.0) {
		rgb = vec3(v, v, v);
	}
	else {
		if(h==1.0)
			h = 0.0;
		
		h *= 6.0;
		i = floor(h);
		f = h - i;
		rgb = vec3(f, f, f);
		p = v*(1.0-s);
		q = v*(1.0-(s*f));
		t = v*(1.0-(s*(1.0-f)));
		
		if (i == 0.0) rgb = vec3(v, t, p);
		else if (i == 1.0) rgb = vec3(q, v, p);
		else if (i == 2.0) rgb = vec3(p, v, t);
		else if (i == 3.0) rgb = vec3(p, q, v);
		else if (i == 4.0) rgb = vec3(t, p, v);
		else rgb = vec3(v, p, q);
	}

	return vec4(rgb, hsv.w);
}

void main(void)
{
	vec4 color = vec4(0.0);
	vec2 myOffset;
	for ( int i = 0; i < 16; i++ ) {
	   /* x */
	   myOffset = vec2(offset[i] * spread, 0);
	   vec4 myDepthColor = texture2D(textureunit, gl_TexCoord[0].st + myOffset);
        myDepthColor.r = 1.0 - myDepthColor.r;
        myDepthColor.g = 1.0 - myDepthColor.g;
        myDepthColor.b = 1.0 - myDepthColor.b;
        myDepthColor.a = 1.0 - myDepthColor.a;
	   color += weight[i] * myDepthColor;
	   /* y */
	   myOffset = vec2(0, offset[i] * spread);
	   myDepthColor = texture2D(textureunit, gl_TexCoord[0].st + myOffset);
        myDepthColor.r = 1.0 - myDepthColor.r;
        myDepthColor.g = 1.0 - myDepthColor.g;
        myDepthColor.b = 1.0 - myDepthColor.b;
        myDepthColor.a = 1.0 - myDepthColor.a;
	   color += weight[i] * myDepthColor;
	}
vec4 myDepthColor = texture2D(textureunit, gl_TexCoord[0].st);

color -= myDepthColor;
color.a = 1.0;

float myGrey = (color.r + color.g + color.b) / 3.0;
//myGrey = pow(myGrey,5.0);
vec4 myColor = texture2D(colortexture, gl_TexCoord[0].st);
vec4 myHSV = rgb_to_hsv(myColor);
myHSV.b = color.r;
vec4 myNewColor = hsv_to_rgb(myHSV);
//myNewColor = vec4(myHSV.b);
myNewColor.a = 1.0;
gl_FragColor = myNewColor;
}
