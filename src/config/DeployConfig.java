package config;

import java.nio.file.Path;

public class DeployConfig {
	Path targetPath;
	
	public Path getTargetPath() {
		return targetPath;
	}
	
	public void setTargetPath(Path targetPath) {
		this.targetPath = targetPath;
	}
}
