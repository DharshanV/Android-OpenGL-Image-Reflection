#version 300 es
in vec2 texCoods;
out vec4 FragColor;

uniform sampler2D boxTexture;

void main(){
    FragColor = texture(boxTexture,texCoods);
}