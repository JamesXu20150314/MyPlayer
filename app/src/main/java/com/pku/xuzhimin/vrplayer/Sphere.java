package com.pku.xuzhimin.vrplayer;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by zhimin xu on 2018/9/20.
 */
public class Sphere {


    private static final int sPositionDataSize = 3;
    private static final int sTextureCoordinateDataSize = 2;

    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private ShortBuffer indexBuffer;
    private int mNumIndices;
    private final int mProgram;


    /**
     * modified from zhimin xu on 18/9/20.
     * original source code:
     * https://github.com/shulja/viredero/blob/a7d28b21d762e8479dc10cde1aa88054497ff649/viredroid/src/main/java/org/viredero/viredroid/Sphere.java
     * @param radius -between near plane and far plane.
     * @param rings
     * @param sectors
     */
    public Sphere(float radius, int rings, int sectors) {
        final float PI = (float) Math.PI;
        final float PI_2 = (float) (Math.PI / 2);

        float R = 1f/(float)rings;
        float S = 1f/(float)sectors;
        short r, s;
        float x, y, z;

        int numPoint = (rings + 1) * (sectors + 1);
        float[] vertexs = new float[numPoint * 3];
        float[] texcoords = new float[numPoint * 2];
        short[] indices = new short[numPoint * 6];

        //map texture 2d-3d
        int t = 0, v = 0;
        for(r = 0; r < rings + 1; r++) {
            for(s = 0; s < sectors + 1; s++) {
                x = (float) (Math.cos(2*PI * s * S) * Math.sin( PI * r * R ));
                y = (float) Math.sin( -PI_2 + PI * r * R );
                z = (float) (Math.sin(2*PI * s * S) * Math.sin( PI * r * R ));

                texcoords[t++] = s*S;
                texcoords[t++] = r*R;

                vertexs[v++] = x * radius;
                vertexs[v++] = y * radius;
                vertexs[v++] = z * radius;
            }
        }

        //glDrawElements
        int counter = 0;
        int sectorsPlusOne = sectors + 1;
        for(r = 0; r < rings; r++){
            for(s = 0; s < sectors; s++) {
                indices[counter++] = (short) (r * sectorsPlusOne + s);       //(a)
                indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s+1));  // (d)
            }
        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                vertexs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer cc = ByteBuffer.allocateDirect(
                texcoords.length * 4);
        cc.order(ByteOrder.nativeOrder());
        FloatBuffer texBuffer = cc.asFloatBuffer();
        texBuffer.put(texcoords);
        texBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        mTexCoordinateBuffer=texBuffer;
        mVerticesBuffer=vertexBuffer;
        mNumIndices=indices.length;

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }


    private final String vertexShaderCode =
            "attribute vec4 aPosition;" +
                    "attribute vec4 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "    gl_Position = uMVPMatrix * aPosition;" +
                    "    vTextureCoord = aTextureCoord.xy;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "    gl_FragColor=texture2D(sTexture, vTextureCoord);" +
                    "}";

    public void uploadVerticesBuffer(int positionHandle){
        FloatBuffer vertexBuffer = getVerticesBuffer();
        if (vertexBuffer == null) return;
        vertexBuffer.position(0);

        GLES20.glVertexAttribPointer(positionHandle, sPositionDataSize, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        Log.d("MainActivity", "glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        Log.d("MainActivity","glEnableVertexAttribArray maPositionHandle");
    }

    public void uploadTexCoordinateBuffer(int textureCoordinateHandle){
        FloatBuffer textureBuffer = getTexCoordinateBuffer();
        if (textureBuffer == null) return;
        textureBuffer.position(0);

        GLES20.glVertexAttribPointer(textureCoordinateHandle, sTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, textureBuffer);
        Log.d("MainActivity","glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        Log.d("MainActivity","glEnableVertexAttribArray maTextureHandle");
    }


    public FloatBuffer getVerticesBuffer() {
        return mVerticesBuffer;
    }

    public FloatBuffer getTexCoordinateBuffer() {
        return mTexCoordinateBuffer;
    }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        if (indexBuffer != null){
            indexBuffer.position(0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mNumIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mNumIndices);
        }
    }
}
