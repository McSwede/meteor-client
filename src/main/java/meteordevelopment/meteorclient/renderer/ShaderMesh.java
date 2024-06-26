/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

public class ShaderMesh extends Mesh {
    private final Shader shader;

    public ShaderMesh(Shader shader, DrawMode drawMode, Attrib... attributes) {
        super(drawMode, attributes);
        if (shader == null) throw new NullPointerException("Expected shader to be non-null");

        this.shader = shader;
    }

    @Override
    protected void beforeRender() {
        shader.bind();
    }
}
