#extension GL_ARB_texture_rectangle : enable 

uniform sampler2DRect texture_0;
uniform sampler2DRect texture_1;
uniform float deltatime;

varying vec2 vTexCoord;

const int BLOCK_0 = 0;
const int BLOCK_1 = 1;

void main() {
	gl_FragData[BLOCK_0] = texture2DRect(texture_0, vTexCoord);
	gl_FragData[BLOCK_1] = texture2DRect(texture_1, vTexCoord);
}
