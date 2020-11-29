package config;
import java.nio.file.Path;
import java.util.ArrayList;


public class TemplateConfig {
	public static final String DIR_SUBTEMPLATES = "subtemplates";
	private String clsName ;
	private ArrayList<String> viewDataClsNames,viewDataClsPaths;
	//private Path tplClsFile
	private Path tmplPath;
	private static Path srcPath, destPath;
	private boolean renderToString, renderStatic;
//	private String renderToQStringVariableName;
	private String subDir;
	private boolean includeTranslations;
	
	public TemplateConfig() {
		viewDataClsNames = new ArrayList<>();
		viewDataClsPaths = new ArrayList<>();
	}
	
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
	
	public ArrayList<String> getViewDataClsNames() {
		return viewDataClsNames;
	}	
	
	public String getViewDataClsName(int i) {
		return viewDataClsNames.get(i);
	}
	public String getViewDataClsPath(int i) {
		return viewDataClsPaths.get(i);
	}
	
	public void addViewDataClsName(String viewDataClsName) {
		this.viewDataClsNames.add(viewDataClsName);
	}

	public ArrayList<String> getViewDataClsPath() {
		return viewDataClsPaths;
	}
	
	public void addViewDataClsPath(String viewDataClsPath) {
		this.viewDataClsPaths.add(viewDataClsPath);
	}

	public int getViewDataClsCount() {
		return viewDataClsNames.size();
	}
	
}
