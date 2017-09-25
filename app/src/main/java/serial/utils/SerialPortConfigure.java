package serial.utils;
/**
 * 串口配置
 * @author yk
 *
 */
public class SerialPortConfigure {
	private String path; //串口号
	private int baudrate; //波特率
	private char nVerify; //校验位
	private int nbits; //数据位
	private int nstop; //停止位
	private boolean isWrap; //是否换行显示
	private boolean isSixteenShou; //是否十六进制接收
	private boolean isSixteenFa; //是否十六进制发送
	private boolean isOpen; //串口是否打开
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getBaudrate() {
		return baudrate;
	}
	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}
	public int getNbits() {
		return nbits;
	}
	public void setNbits(int nbits) {
		this.nbits = nbits;
	}
	public char getnVerify() {
		return nVerify;
	}
	public void setnVerify(char nVerify) {
		this.nVerify = nVerify;
	}
	public int getNstop() {
		return nstop;
	}
	public void setNstop(int nstop) {
		this.nstop = nstop;
	}
	public boolean isWrap() {
		return isWrap;
	}
	public void setWrap(boolean isWrap) {
		this.isWrap = isWrap;
	}
	public boolean isSixteenShou() {
		return isSixteenShou;
	}
	public void setSixteenShou(boolean isSixteenShou) {
		this.isSixteenShou = isSixteenShou;
	}
	public boolean isSixteenFa() {
		return isSixteenFa;
	}
	public void setSixteenFa(boolean isSixteenFa) {
		this.isSixteenFa = isSixteenFa;
	}
}
