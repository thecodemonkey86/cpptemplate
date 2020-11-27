package config;
import java.nio.file.Path;


public class TemplateConfig {
	public static final String DIR_SUBTEMPLATES = "subtemplates";
	private String clsName, viewDataClsName, viewDataClsPath; 
	//private Path tplClsFile
	private Path tmplPath;
	private static Path srcPath, destPath;
	private boolean renderToString, renderStatic;
//	private String renderToQStringVariableName;
	private String subDir;
	private boolean includeTranslations;
	
	public void setIncludeTranslations(boolean includeTranslations) {
		this.includeTranslations = includeTranslations;
	}
	
	public boolean isIncludeTranslations() {
		return includeTranslations;
	}
	
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
	
	/*public void setRenderToQString(boolean renderToQString) {
		this.renderToQString = renderToQString;
	}
	
	public boolean isRenderToQString() {
		return renderToQString;
	}*/

	public String getRenderToQStringVariableName() {
		return "_html";
	}
	
	/*public void setRenderToQStringVariableName(String renderToQStringVariableName) {
		this.renderToQStringVariableName = renderToQStringVariableName;
	}*/
	
	public boolean isRenderToString() {
		return renderToString;
	}
	
	public void setRenderToString(boolean renderToString) {
		this.renderToString = renderToString;
	}
	
	@Override
	public String toString() {
		return clsName;
	}
	
	
	public boolean isRenderStatic() {
		return renderStatic;
	}
	
	public void setRenderStatic(boolean renderStatic) {
		this.renderStatic = renderStatic;
	}
	
	public String getViewDataClsName() {
		return viewDataClsName;
	}
	
	public void setViewDataClsName(String viewDataClsName) {
		this.viewDataClsName = viewDataClsName;
	}

	public String getViewDataClsPath() {
		return viewDataClsPath;
	}
	
	public void setViewDataClsPath(String viewDataClsPath) {
		this.viewDataClsPath = viewDataClsPath;
	}
	
}
