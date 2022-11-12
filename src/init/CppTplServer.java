package init;

import java.io.IOException;

import io.server.DebugServer;

public class CppTplServer {

	public static void main(String[] args) {
		try {
			DebugServer.serve();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
