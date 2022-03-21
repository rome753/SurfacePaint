#version 300 es
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;

void main()
{
     FragColor = texture(screenTexture, TexCoords);
}