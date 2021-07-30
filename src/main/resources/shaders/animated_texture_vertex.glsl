#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in ivec3 joints;
in vec3 weights;

out vec2 pass_textureCoords;
out vec3 pass_position;
out vec3 pass_normal;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointTransforms[50];

void main() {
    vec4 totalLocalPos = vec4(0.0);
    vec4 totalNormal = vec4(0.0);

    for(int i=0;i<3;i++){
        mat4 jointTransform = jointTransforms[joints[i]];
        vec4 posePosition = jointTransform * vec4(position, 1.0);
        totalLocalPos += posePosition * normalize(weights)[i];

        vec4 worldNormal = jointTransform * vec4(normal, 0.0);
        totalNormal += worldNormal * normalize(weights)[i];
    }

    vec4 worldPos = transformationMatrix * totalLocalPos;
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    pass_position = worldPos.xyz;
    pass_normal = (transformationMatrix * totalNormal).xyz;
    pass_textureCoords = textureCoords;
}

