package util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import codegen.CodeUtil;

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
	
	public static String qStringLiteral(String s, boolean latin1AsConstCharPtr) {
		byte[] bytesUtf8 = s.getBytes(StandardCharsets.UTF_8);
		byte[] bytesLatin1 = s.getBytes(StandardCharsets.ISO_8859_1);
		return Arrays.equals(bytesUtf8, bytesLatin1) 
				? 
						latin1AsConstCharPtr 
							? CodeUtil.quote(s)
							: "QStringLiteral"+CodeUtil.parentheses(CodeUtil.quote(s))
				: "QString::fromUtf8"+CodeUtil.parentheses(CodeUtil.quote(s));
	}
	public static String qStringLiteral(String s) {
		return getQStringLiteralConstructor(s, false)+CodeUtil.parentheses(CodeUtil.quote(s));
	}
	public static String getQStringLiteralConstructor(String s, boolean toHtmlEscapedRequired) {
		byte[] bytesUtf8 = s.getBytes(StandardCharsets.UTF_8);
		byte[] bytesLatin1 = s.getBytes(StandardCharsets.ISO_8859_1);
		return !toHtmlEscapedRequired && Arrays.equals(bytesUtf8, bytesLatin1) 
				? "QStringLiteral"
				: "QString::fromUtf8";
	}
	
	public static String commaSep(Object...tokens) {
		
		ArrayList<Object> l=new ArrayList<>();
		for(Object o : tokens) {
			if(o!=null) {
				l.add(o);
			}
		}
		return CodeUtil.commaSep(l);
	}
	
}
