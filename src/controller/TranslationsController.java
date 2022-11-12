package controller;

import java.util.ArrayList;

import xml.model.XliffFile;

public class TranslationsController {
	private static ArrayList<XliffFile> translationSourceFiles;
	private static ArrayList<XliffFile> translationTargetFiles;
	
	public static void setTranslationSourceFiles(ArrayList<XliffFile> translationSourceFiles) {
		TranslationsController.translationSourceFiles = translationSourceFiles;
	}
	
	public static void setTranslationTargetFiles(ArrayList<XliffFile> translationTargetFiles) {
		TranslationsController.translationTargetFiles = translationTargetFiles;
	}
	
	public static boolean hasTranslationsLoaded() {
		return translationSourceFiles!=null && translationTargetFiles!=null;
	}
	
	public static boolean hasTranslationSource(String key) {
		for(XliffFile x:translationSourceFiles) {
			if(x.hasTranslation(key)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasTranslationTarget(String key) {
		for(XliffFile x:translationTargetFiles) {
			if(x.hasTranslation(key)) {
				return true;
			}
		}
		return false;
	}
}
