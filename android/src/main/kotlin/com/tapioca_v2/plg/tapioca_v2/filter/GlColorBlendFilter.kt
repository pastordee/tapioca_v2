package com.tapioca_v2.plg.tapioca_v2.filter

import com.daasuu.mp4compose.filter.GlFilter;
import android.opengl.GLES20
import com.tapioca_v2.plg.tapioca_v2.Filter
import android.graphics.Color

class GlColorBlendFilter(filter: Filter) : GlFilter(GlFilter.DEFAULT_VERTEX_SHADER,this.CONTRAST_FRAGMENT_SHADER) {
    private val filter: Filter = filter
    companion object {
        const val CONTRAST_FRAGMENT_SHADER =
        """
        precision mediump float;
        varying vec2 vTextureCoord;
        uniform lowp sampler2D sTexture;
        uniform lowp float red;
        uniform lowp float green;
        uniform lowp float blue;
        uniform lowp float alpha;
        void main() {
           vec4 color = vec4(red,green,blue,0.1);
           gl_FragColor = mix(texture2D(sTexture, vTextureCoord), color, alpha);
       }
       """
    }

    public override fun onDraw() {
        val color = Color.parseColor(filter.type)
        GLES20.glUniform1f(getHandle("red"), Color.red(color) / 255f)
        GLES20.glUniform1f(getHandle("green"), Color.green(color)/ 255f)
        GLES20.glUniform1f(getHandle("blue"), Color.blue(color)/ 255f)
        GLES20.glUniform1f(getHandle("alpha"), filter.alpha.toFloat())
    }
}
