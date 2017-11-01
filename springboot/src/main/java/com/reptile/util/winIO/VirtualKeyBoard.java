package com.reptile.util.winIO;


public class VirtualKeyBoard {
	public static final WinIo64 winIo64 = WinIo64.INSTANCE;

	static {
		if (!WinIo64.INSTANCE.InitializeWinIo()) {
			System.err.println("Cannot Initialize the WinIO");
			System.exit(1);
		}
	}

	public static void KeyDown(int key) throws Exception {
		User32Util.KBCWait4IBE();
		winIo64.SetPortVal(WinIo64.CONTROL_PORT, 0xd2, 1);
		User32Util.KBCWait4IBE();
		winIo64.SetPortVal(WinIo64.DATA_PORT, key, 1);
	}

	public static void KeyUp(int key) throws Exception {
		User32Util.KBCWait4IBE();
		winIo64.SetPortVal(WinIo64.CONTROL_PORT, 0xd2, 1);
		User32Util.KBCWait4IBE();
		winIo64.SetPortVal(WinIo64.DATA_PORT, (key | 0x80), 1);
	}

	public static void KeyPress(char key) throws Exception {
		KeyPress(VKMapping.toVK("" + key));
	}
	public static void KeyPress(int vk) throws Exception {
		int scan = User32.INSTANCE.MapVirtualKey(vk, 0);
		KeyDown(scan);
		KeyUp(scan);
	}

	public static void main(String[] args) throws Exception {
//		NativeLibrary.addSearchPath("WinIo64", VirtualKeyBoard.class.getResource("/").getPath());
		String s = "helloworld";
		for (int i = 0; i < s.length(); i++) {
			KeyPress(s.charAt(i));
		}
	}

}
