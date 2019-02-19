package config;
import java.nio.file.Path;


public class TemplateConfig {
	private String clsName; 
	//private Path tplClsFile
	private Path tmplPath;
	private static Path srcPath, destPath;
	private boolean renderToQString;
	private String renderToQStringVariableName;
	private String subDir;
	
	public void setSubDir(String subDir) {
		this.subDir = subDir;
	}
	
	public String getSubDir() {
		return subDir;
	}
	
	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	/*public Path getTplClsFile() {
		return tplClsFile;
	}

	public void setTplClsFile(Path tplClsFile) {
		this.tplClsFile = tplClsFile;
	}*/

	public Path getTmplPath() {
		return tmplPath;
	}

	public void setTmplPath(Path tmplPath) {
		this.tmplPath = tmplPath;
	}

	public static Path getSrcPath() {
		return srcPath;
	}

	public static void setSrcPath(Path srcPath) {
		TemplateConfig.srcPath = srcPath;
	}

	public static Path getDestPath() {
		return destPath;
	}

	public static void setDestPath(Path destPath) {
		TemplateConfig.destPath = destPath;
	}
	
	public void setRenderToQString(boolean renderToQString) {
		this.renderToQString = renderToQString;
	}
	
	public boolean isRenderToQString() {
		return renderToQString;
	}

	public String getRenderToQStringVariableName() {
		return renderToQStringVariableName;
	}
	
	public void setRenderToQStringVariableName(String renderToQStringVariableName) {
		this.renderToQStringVariableName = renderToQStringVariableName;
	}
	
	
}
