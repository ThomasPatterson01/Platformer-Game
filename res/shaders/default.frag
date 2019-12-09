#version 130

in vec4 vertexColor; //the interpolated color of this fragment (from the vertices)
in vec2 textureCoord; //Which part of the texture is here

out vec4 fragColor; //output what color this fragment should be

uniform sampler2D texImage; //used to access the colors in the texture
uniform int preserveWhite;

void main() {
    vec4 textureColor = texture(texImage, textureCoord); //gets the texture color for this fragment
    
    //if white, dont add the vertex color (so that it stays white)
    if (textureColor != vec4(1,1,1,1))
    	fragColor = vertexColor * textureColor; //combines the vertex and texture color
    else if (preserveWhite != 1)
    	fragColor = vertexColor * textureColor; //combines the vertex and texture color
    else{
    	fragColor = textureColor;
    	fragColor.a = vertexColor.a;
    }
}
