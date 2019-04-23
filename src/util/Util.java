package util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Util {
	public static Path getConfigurablePath(String path, Path execPath) {
		if (path.startsWith("%EXEC_PATH%")) {
			String s = StringUtil.dropFirst(path,"%EXEC_PATH%" );
			if(s.startsWith("/") || s.startsWith("\\")) {
				s = s.substring(1);
			}
			
			if(s.length()>0)
				return execPath.resolve(s);
			return execPath;
		} else {
			return Paths.get(path);
		}
	}
	
	public static String getQStringLiteralConstructor(String s, boolean toHtmlEscapedRequired) {
		byte[] bytesUtf8 = s.getBytes(StandardCharsets.UTF_8);
		byte[] bytesLatin1 = s.getBytes(StandardCharsets.ISO_8859_1);
		return !toHtmlEscapedRequired && Arrays.equals(bytesUtf8, bytesLatin1) ? "QLatin1Literal" : "QStringLiteral";
	}
}
