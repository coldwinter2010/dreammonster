package com.cold.dreammonster.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.WindowManager;

/**
 * @author LC
 */
@SuppressWarnings("all")
public class BitmapUtil {

	/**
	 * 根据图片的路径,获取合适的Bitmap
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getUsableImage(Context context, String path) {
		Options opts = new Options(); // 选项对象(在加载图片时使用)
		opts.inJustDecodeBounds = true; // 修改选项, 只获取大小
		BitmapFactory.decodeFile(path, opts); // 加载图片(只得到图片大小)

		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); // 获取窗体管理器
		int scaleX = opts.outWidth / manager.getDefaultDisplay().getWidth(); // X轴缩放比例(图片宽度/屏幕宽度)
		int scaleY = opts.outHeight / manager.getDefaultDisplay().getHeight(); // Y轴缩放比例
		int scale = scaleX > scaleY ? scaleX : scaleY; // 图片的缩放比例(X和Y哪个大选哪个)

		opts.inJustDecodeBounds = false; // 修改选项, 不只解码边界
		opts.inSampleSize = scale > 1 ? scale : 1; // 修改选项, 加载图片时的缩放比例
		return BitmapFactory.decodeFile(path, opts); // 加载图片(得到缩放后的图片)
	}

	public static Bitmap drawableToBitamp(Drawable drawable) {

		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 获取一指定大小的Bitmap对象
	 */
	public static Bitmap getUsableImage(String pathName, int reqWidth, int reqHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}
	
	public static Bitmap getUsableImage(Resources res, int id,int reqWidth,int reqHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, id, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res,id, options);
	}
	

	public static String getAbsolutePath(Context context, Uri uri) {
		Cursor c = context.getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null,
				null);
		c.moveToNext();
		return c.getString(0);
	}

	public static void closeIO(InputStream in, OutputStream out) {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定分辨率和清晰度的图片压缩方法
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param reqWidth
	 * @param reqHeight
	 * @param quality
	 */
	public static void transImage(String fromFile, String toFile, int reqWidth, int reqHeight, int quality) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// 缩放的尺寸
			float scaleWidth = (float) reqWidth / bitmapWidth;
			float scaleHeight = (float) reqHeight / bitmapHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 产生缩放后的Bitmap对象
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
			// 保存到文件
			bitmap2File(toFile, quality, resizeBitmap);

			if (!bitmap.isRecycled()) {
				// 释放资源，以防止OOM
				bitmap.recycle();
			}

			if (!resizeBitmap.isRecycled()) {
				resizeBitmap.recycle();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// 将Bitmap转换成文件
	public static File bitmap2File(String toFile, int quality, Bitmap bitmap)
			throws FileNotFoundException, IOException {
		File captureFile = new File(toFile);
		FileOutputStream out = new FileOutputStream(captureFile);
		if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
			out.flush();
			out.close();
		}
		return captureFile;
	}

	// 计算图片的缩放值
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;

	}

	// Bitmap、Drawable、InputStream、byte[] 之间转换
	/**********************************************************/
	// 1. Bitmap to InputStream
	public static InputStream bitmap2Input(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public static InputStream bitmap2Input(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

	// 2. Bitmap to byte[]
	public static byte[] bitmap2ByteArray(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
		return baos.toByteArray();
	}

	public static byte[] bitmap2ByteArray(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// 3. Drawable to byte[]
	public static byte[] drawable2ByteArray(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		return out.toByteArray();
	}

	// 4. byte[] to Bitmap
	public static Bitmap byteArray2Bitmap(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
}
