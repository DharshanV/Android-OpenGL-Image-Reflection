#version 300 es
layout (location = 0) in vec3 inPos;
layout (location = 1) in vec3 inNormals;
layout (location = 2) in vec2 inTexCoods;

out vec2 texCoods;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){
    texCoods = inTexCoods;
    gl_Position = projection * view * model * vec4(inPos,1.0f);
}