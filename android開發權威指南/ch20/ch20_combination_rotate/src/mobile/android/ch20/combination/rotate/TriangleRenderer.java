package mobile.android.ch20.combination.rotate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;

public class TriangleRenderer implements GLSurfaceView.Renderer
{

	public TriangleRenderer(Context context)
	{
		mContext = context;
		mTriangle = new Triangle();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

		gl.glDisable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0f, 0f, 0f, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		InputStream is = mContext.getResources().openRawResource(R.raw.robot);
		Bitmap bitmap;
		try
		{
			bitmap = BitmapFactory.decodeStream(is);
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{

			}
		}

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}

	public void onDrawFrame(GL10 gl)
	{

		gl.glDisable(GL10.GL_DITHER);

		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);

		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		long time = SystemClock.uptimeMillis() % 4000L;
		float angle = 0.090f * ((int) time);

		gl.glRotatef(angle, 0, 0, 1.0f);

		mTriangle.draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		gl.glViewport(0, 0, w, h);

		float ratio = (float) w / h;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);

	}

	private Context mContext;
	private Triangle mTriangle;
	private int mTextureID;
}

class Triangle
{
	public Triangle()
	{

		ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTexBuffer = tbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();

		float[] coords =
		{ -0.5f, -0.25f, 0, 0.5f, -0.25f, 0, 0.0f, 0.559016994f, 0 };

		for (int i = 0; i < VERTS; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				mFVertexBuffer.put(coords[i * 3 + j] * 2.0f);
			}
		}

		for (int i = 0; i < VERTS; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				mTexBuffer.put(coords[i * 3 + j] * 2.0f + 0.5f);
			}
		}

		for (int i = 0; i < VERTS; i++)
		{
			mIndexBuffer.put((short) i);
		}

		mFVertexBuffer.position(0);
		mTexBuffer.position(0);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CCW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS,
				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}

	private final static int VERTS = 3;

	private FloatBuffer mFVertexBuffer;
	private FloatBuffer mTexBuffer;
	private ShortBuffer mIndexBuffer;
}
