package io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class JsCompressor implements Compressor{

	String closureCompilerExecutable;
	Path jreDir;
	public JsCompressor( Path closureCompilerExecutable) {
		this.closureCompilerExecutable = closureCompilerExecutable.toString();
		jreDir = closureCompilerExecutable.getParent().resolve("bin");
	}
	
	@Override
	public void compress(Path input, Path output) throws IOException {
		//ProcessBuilder proc = new ProcessBuilder("java","-jar", settings.getClosureCompilerExecutable().toString(),tempJsFile.toAbsolutePath().toString(),"--js_output_file",repositoryPath.resolve("js").resolve(minJsFileName).toAbsolutePath().toString(),"--jscomp_off=uselessCode");
		ProcessBuilder proc = new ProcessBuilder(jreDir.resolve("java.exe").toString(),"-jar", this.closureCompilerExecutable,input.toAbsolutePath().toString(),"--js_output_file",output.toAbsolutePath().toString(),"--jscomp_off=uselessCode","--language_in=ECMASCRIPT_NEXT");
		Process p = proc.start();
		try {
			InputStream errorStream = p.getErrorStream();
			int r;
			while((r=errorStream.read())>-1) {
				System.out.print((char)r);
			}
			InputStream inputStream = p.getInputStream();
			while((r=inputStream.read())>-1) {
				System.out.print((char)r);
			}
			p.waitFor(300, TimeUnit.SECONDS);
			

		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		
	}

}
