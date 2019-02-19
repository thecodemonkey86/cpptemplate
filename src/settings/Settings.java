package settings;
import java.nio.file.Path;




public class Settings {
	int lineWidth = 1024;
	Path  closureCompilerExecutable, yuiCompressorExecutable;

	public int getLineWidth() {
		return lineWidth;
	}
	
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public Path getClosureCompilerExecutable() {
		return closureCompilerExecutable;
	}

	public void setClosureCompilerExecutable(Path closureCompilerExecutable) {
		this.closureCompilerExecutable = closureCompilerExecutable;
	}

	public Path getYuiCompressorExecutable() {
		return yuiCompressorExecutable;
	}

	public void setYuiCompressorExecutable(Path yuiCompressorExecutable) {
		this.yuiCompressorExecutable = yuiCompressorExecutable;
	}
	
	@Override
	public String toString() {
		return closureCompilerExecutable+"|"+yuiCompressorExecutable;
	}
	
	
	
}
