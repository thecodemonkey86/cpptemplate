package test;

import java.io.IOException;
import java.nio.file.Paths;

import settings.Settings;
import util.exception.CancelException;
import io.CssJsProcessor;

public class TplTest {
	public static void main(String[] args) {
		try {
			Settings settings = new Settings();
			settings.setClosureCompilerExecutable(Paths.get("D:\\Programme\\closurecompiler\\compiler.jar"));
			settings.setYuiCompressorExecutable(Paths.get("D:\\Programme\\yuicompressor\\yuicompressor-2.4.7.jar"));
			CssJsProcessor.setSettings(settings);
			CssJsProcessor.setRepositoryPath(Paths.get("D:\\Bernhard\\eclipse_workspace\\CppTemplateGenerator\\bin\\repository"));
			//CssJsProcessor.loadJs("http://code.jquery.com/jquery-2.2.4.js");
			CssJsProcessor.loadCss("http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.css");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
