#version 130

in vec2 position; //position of vertex on screen
in vec4 color; //color of vertex
in vec2 texcoord; //texture co-ordinates

out vec4 vertexColor; //send vertex color to fragment shader
out vec2 textureCoord; //send texture coords to fragment shader

//view, model and projection matrices
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	//send values onto fragment shader
    vertexColor = color;
    textureCoord = texcoord;
    
    //apply mvp matrices to the vertex position
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 0.0, 1.0);
}
