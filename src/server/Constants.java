package server;

import java.io.File;

public final class Constants {

	public static final String PROTOCOL = "udp";
	public static final String STACK_PATH = "gov.nist";
	public static final String STACK_PATH_NAME = "gov.nist";
	public static final String STUN_ADDRESS = "stun.xten.net";
	public static final int STUN_PORT = 5060;
	public static final int LOCALHOST_PORT = 5061;
	public static final String LOCALHOST_ADDRESS = "localhost";
	public static final String SIPPROXY_ADDRESS = "proxy01.sipphone.com";
	public static final int SIPPROXY_PORT = 5060;
	public static final String FILE_CONFIG = System.getProperty("user.dir") + File.separator + "config.xml";
}
