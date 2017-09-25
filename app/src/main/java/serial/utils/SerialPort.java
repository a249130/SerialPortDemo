package serial.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {
	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	public FileDescriptor mFd;
	public FileInputStream mFileInputStream;
	public FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int nBits, char nEvent, int nStop, int flags)
			throws SecurityException, IOException {
		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		Log.d("aaaaaaa", "111111111111111111111111111111111");
		mFd = open(device.getAbsolutePath(), baudrate, nBits, nEvent, nStop, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			Log.d("aaaaaaa", "222222222222222222222222222222222");
			throw new IOException();
		}
		Log.d("aaaaaaa", "33333333333333333333333333333333");
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		Log.d("aaaaaaa", "4444444444444444444444444");
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		Log.d("aaaaaaa", "5555555555555555555555555555");
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, // 波特率
											  int nBits, // 数据位
											  char nVerify, // 偶校验位
											  int nStop, // 停止位
											  int flags);

	public native void close();

	public native int sri_Init();

	public native void sri_DeInit();

	public native int sri_IOCTL(int controlcode);

	public native int write(byte[] data);

	public native int read(byte[] buf, int len);

	public native int select(int sec, int usec);

	static {
		System.loadLibrary("serial_port");
	}
}