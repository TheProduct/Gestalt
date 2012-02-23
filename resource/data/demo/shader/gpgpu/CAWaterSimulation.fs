// inspired by http://freespace.virgin.net/hugo.elias/graphics/x_water.htm

#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect read_cells;
uniform sampler2DRect read_prev_cells;
uniform sampler2DRect energy_map;
uniform vec2 flow_direction; // does not work yet
uniform float damping;

void main( void ) {
    	float s = gl_TexCoord[0].s;
    	float t = gl_TexCoord[0].t;

    	const float d = 1.0;

	// neighbor height sum
    	float neighbour_sum =
        texture2DRect( read_cells, vec2( s - d, t) + flow_direction ).r +
        texture2DRect( read_cells, vec2( s + d, t) + flow_direction ).r +
        texture2DRect( read_cells, vec2( s, t - d) + flow_direction ).r +
        texture2DRect( read_cells, vec2( s, t + d) + flow_direction ).r;
	neighbour_sum /= 2.0; // this is a very reduced formula to integrate velocity we need to unwrap it again...

	// current height
  	float current_height = texture2DRect( read_prev_cells, vec2( s, t ) ).r;

	// new height
	float new_height = neighbour_sum - current_height;
	new_height *= damping;

	// external energy
    	float external_energy = texture2DRect( energy_map, vec2( s, t ) ).r;
	new_height += external_energy;
	
	// clamp
	new_height = max (new_height, -1.0);
	new_height = min (new_height, 1.0);

    // output
    gl_FragColor = vec4(new_height, new_height, new_height, 1 );
}
