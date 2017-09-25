package serial.utils;

public interface SerialPortInterface {
	void onDataReceived(final byte[] buffer, final int size);
}