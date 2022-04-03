#version 310 es

// The uniform parameters that are passed from application for every frame.
uniform float radius;

// Declare the custom data type that represents one point of a circle.
// This is vertex position and color respectively,
// that defines the interleaved data within a
// buffer that is Vertex|Color|Vertex|Color|
struct AttribData
{
    vec4 v;
    vec4 c;
};

// Declare an input/output buffer that stores data.
// This shader only writes data into the buffer.
// std430 is a standard packing layout which is preferred for SSBOs.
// Its binary layout is well defined.
// Bind the buffer to index 0. You must set the buffer binding
// in the range [0..3]. This is the minimum range approved by Khronos.
// Some platforms might support more indices.
layout(std430, binding = 0) buffer destBuffer
{
    AttribData data[];
} outBuffer;

// Declare the group size.
// This is a one-dimensional problem, so prefer a one-dimensional group layout.
layout (local_size_x = 64, local_size_y = 1, local_size_z = 1) in;

// Declare the main program function that is executed once
// glDispatchCompute is called from the application.
void main()
{
    // Read the current global position for this thread
    uint storePos = gl_GlobalInvocationID.x;

    // Calculate the global number of threads (size) for this work dispatch.
    uint gSize = gl_WorkGroupSize.x * gl_NumWorkGroups.x;

    // Calculate an angle for the current thread
    float alpha = 2.0 * 3.14159265359 * (float(storePos) / float(gSize));

    // Calculate the vertex position based on
    // the previously calculated angle and radius.
    // This is provided by the application.
    outBuffer.data[storePos].v = vec4(sin(alpha) * radius, cos(alpha) * radius, 0.0, 1.0);

    // Assign a color for the vertex
    outBuffer.data[storePos].c = vec4(float(storePos) / float(gSize), 0.0, 1.0, 1.0);
}
