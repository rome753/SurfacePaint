#version 300 es
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;

//void main()
//{
//     FragColor = texture(screenTexture, TexCoords);
//
//     // 反相效果
//     FragColor = vec4(1.0 - texture(screenTexture, TexCoords).rgb, 1.0);
//
//     // 灰度效果
//     float ave = (FragColor.r + FragColor.g + FragColor.b) / 3.0;
//     FragColor = vec4(ave, ave, ave, 1.0);
//}

const float offset = 1.0 / 300.0;

void main()
{
     vec2 offsets[9] = vec2[](
     vec2(-offset,  offset), // 左上
     vec2( 0.0f,    offset), // 正上
     vec2( offset,  offset), // 右上
     vec2(-offset,  0.0f),   // 左
     vec2( 0.0f,    0.0f),   // 中
     vec2( offset,  0.0f),   // 右
     vec2(-offset, -offset), // 左下
     vec2( 0.0f,   -offset), // 正下
     vec2( offset, -offset)  // 右下
     );

     // 锐化核
     float kernel[9] = float[](
     -1.0, -1.0, -1.0,
     -1.0,  9.0, -1.0,
     -1.0, -1.0, -1.0
     );

//     // 边缘检测核
//     float kernel[9] = float[](
//     1.0, 1.0, 1.0,
//     1.0,-8.0, 1.0,
//     1.0, 1.0, 1.0
//     );

//     // 模糊核
//     float kernel[9] = float[](
//     1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0,
//     2.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0,
//     1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0
//     );

     vec3 sampleTex[9];
     for(int i = 0; i < 9; i++)
     {
          sampleTex[i] = vec3(texture(screenTexture, TexCoords.st + offsets[i]));
     }
     vec3 col = vec3(0.0);
     for(int i = 0; i < 9; i++)
     col += sampleTex[i] * kernel[i];

     FragColor = vec4(col, 1.0);
}