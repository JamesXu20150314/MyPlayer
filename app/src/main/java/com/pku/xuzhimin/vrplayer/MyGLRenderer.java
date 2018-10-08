package com.pku.xuzhimin.vrplayer;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mTriangle;
    private Square   mSquare;
    private Sphere mSphere;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private float[] mRotationMatrix = new float[16];

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        //mTriangle.draw(scratch);
        mSphere.draw();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        // initialize a triangle
        mTriangle = new Triangle();
        // initialize a square
        mSquare = new Square();

        mSphere = new Sphere(18, 75,150);

    }

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


    //sphere model with sphere coords
    /*private final float[] spherPoint = new float[6 * 158 * 158 * 158];//int(6 * 158 * 158)
    private final float[] texturePoint = new float[4 * 158 * 158 * 158];
    private final float PAI = (float) 3.14;
    private final float thetaStep = (float) 0.02;
    private final float phiStep = (float) 0.02;
    private final float r = 5;

    public void initSphereCoords() {
        float theta = 0;
        float phi = 0;
        int pointer = 0;
        for(theta = 0;theta <= PAI;theta += thetaStep) {
            for(phi = 0;phi <= 2*PAI;phi += phiStep) {
                spherPoint[pointer++] = (float) (r * sin(theta) * cos(phi));
                spherPoint[pointer++] = (float) (r * sin(theta) * sin(phi));
                spherPoint[pointer++] = (float) (r * cos(theta));

                spherPoint[pointer++] = (float) (r * sin(theta+thetaStep) * cos(phi));
                spherPoint[pointer++] = (float) (r * sin(theta+thetaStep) * sin(phi));
                spherPoint[pointer++] = (float) (r * cos(theta+thetaStep));
            }
        }

        pointer = 0;
        for(theta = 0;theta <= PAI;theta += thetaStep) {
            for (phi = 0; phi <= 2 * PAI; phi += phiStep) {
                texturePoint[pointer++] = (float) (phi / (2 * PAI));
                texturePoint[pointer++] = (float) (1 - theta / PAI);

                texturePoint[pointer++] = (float) (phi / (2 * PAI));
                texturePoint[pointer++] = (float) (1 - (theta + thetaStep) / PAI);
            }
        }

    }
    */
}