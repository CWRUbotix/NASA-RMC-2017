package com.wasianish.hci;

public class SerialPacket {
	public final byte command;
	public final byte[] data;
	SerialPacket(byte command, byte[] data) {
		this.command = command;
		this.data = data;
	}
	
	public byte[] asPacket() {
		byte[] out = new byte[3+data.length];
		out[0] = command;
		out[1] = (byte)(data.length>>8);
		out[2] = (byte)(data.length & 0xFF);
		System.arraycopy(data, 0, out, 3, data.length);
		return out;
	}
}
