varying vec3 LightDir;
varying vec3 EyeDir;

uniform sampler2D colorMap;
uniform sampler2D normalMap;
uniform float invRadius;
uniform float bumping;

void main() {
	float distSqr = dot(LightDir, LightDir);
	float att = clamp(1.0 - invRadius * sqrt(distSqr), 0.0, 1.0);
	vec3 lVec = LightDir * inversesqrt(distSqr);

	vec3 vVec = normalize(EyeDir);
	
	vec4 base = texture2D(colorMap, gl_TexCoord[0].st);
	
	vec3 bump = normalize( texture2D(normalMap, gl_TexCoord[0].st).xyz * 2.0 - 1.0);

	vec4 vAmbient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;

	float diffuse = max( dot(lVec, bump), 0.0 );
	
	vec4 vDiffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * diffuse;	

	float specular = pow(clamp(dot(reflect(lVec, bump), vVec), 0.1, 1.0), gl_FrontMaterial.shininess );
	
	vec4 vSpecular = gl_LightSource[0].specular * gl_FrontMaterial.specular * specular;	
	//vSpecular = mix(vSpecular, base, 0.5);
	vSpecular.a = 1.0;

	vec4 finalcolor;
	if (bumping > 0.0)
		finalcolor = (vAmbient*base + vDiffuse*base + vSpecular*base) * att;
	else
		finalcolor = base;
	
	finalcolor.a = base.a;

	gl_FragColor = finalcolor;
}