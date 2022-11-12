package init;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import util.Pair;
import util.ParseUtil;
import util.exception.CancelException;

public class TemplateCodingHelper {

	static void writeRoles(Path base,List<Pair<String,String>>  newRoles) throws IOException {
		Path rolesFile = base.resolve("view/roles.h");
		String code = new String(Files.readAllBytes(rolesFile),StandardCharsets.UTF_8);
		int k=code.indexOf("\n", code.lastIndexOf("constexpr"));
		String newcode = code.substring(0,k+1);
		
		for(Pair<String,String>r:newRoles) {
			newcode += String.format("  static constexpr const char * %s= \"%s\";\r\n",r.getValue1(),r.getValue2());
		}
		
		newcode += code.substring(k+1);
		Files.write(rolesFile, newcode.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	static List<Pair<String,String>> getRoles(Path base) throws IOException {
		List<Pair<String,String>>  result = new ArrayList<>();
		List<String> lines = Files.readAllLines(base.resolve("view/roles.h"));
		for(String l:lines) {
			
			/*if(split.length>6 && split[0].trim().equals("static") && split[1].trim().equals("constexpr") &&  split[2].trim().equals("const") &&split[3].trim().equals("char") && split[4].equals("*")) {
				String[] split2 = split[5].split("=",2);
				if(split2.length==2)
				result.add(new Pair<String,String>(split2[0],split2[1]));
			}*/
			
			
			if(l.contains("static")&& l.contains("constexpr") && l.contains("const")&& l.contains("char") && l.contains("*") ) {
				int k=l.indexOf('*');
				String[] split = l.substring(k+1,l.length()-1).split("=") ;
				result.add(new Pair<String,String>(split[0].trim(),split[1].trim().replace("\"", "")));
			}
		}
		return result;
	}
	
	static String getExistingRole(List<Pair<String,String>> existingRoles, String role) {
		for(Pair<String,String>p:existingRoles) {
			if(p.getValue2().equals(role)) {
				return p.getValue1();
			}
		}
		return null;
	}
	
	static void replaceRolesHtml(List<Pair<String,String>> existingRoles, Path file, Path base) throws IOException, CancelException {
		String html = new String(Files.readAllBytes(file),StandardCharsets.UTF_8); 
		boolean change=false;
		int i=0;
		List<Pair<String,String>>  newRoles=new ArrayList<>();
		while((i=html.indexOf("data-role=",i+1))>-1) {
			Pair<Integer,Character> p = ParseUtil.firstIndexOf(html, '"','\'',i);
			int j=html.indexOf(p.getValue2(),p.getValue1()+1);
			String role = html.substring(p.getValue1()+1,j);
			
			if(!role.startsWith("{")) {
				String e= getExistingRole(existingRoles, role);
				if(e!=null) {
					change = true;
					html = html.substring(0, p.getValue1()+1)+"{{Roles::"+e+"}}"+html.substring(j);
				} else if(!role.contains("%") && !role.contains("{")){
					String roleVar=role.replace("-", "_");
					
					for(int k=1;k<roleVar.length();k++) {
						if(Character.isUpperCase(roleVar.charAt(k) )) {
							roleVar = roleVar.substring(0,k)+"_"+roleVar.substring(k);
							k++;
						}
					}
					roleVar = roleVar.toUpperCase();
					boolean conflict=false;
					do {
					for(Pair<String,String>r:existingRoles) {
						if(r.getValue1().equals(roleVar)) {
							conflict=true;
						}
					}
					if(conflict) {
						roleVar = JOptionPane.showInputDialog("Konflikt Variablenname",roleVar);
						if(roleVar==null) {
							throw new CancelException();
						}
					}
					}while(conflict);
					html = html.substring(0, p.getValue1()+1)+"{{Roles::"+roleVar+"}}"+html.substring(j);
					newRoles.add(new Pair<String, String>(roleVar, role));
					existingRoles.add(newRoles.get(newRoles.size()-1));
				}
			}
		}
		if(change)
			Files.write(file, html.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
		
		if(!newRoles.isEmpty() ) {
			writeRoles(base, newRoles);
		}
		
	}
	
	static void replaceRolesJs(List<Pair<String,String>> existingRoles, Path file, Path base) throws IOException, CancelException {
		String js = new String(Files.readAllBytes(file),StandardCharsets.UTF_8); 
		List<Pair<String,String>>  newRoles=new ArrayList<>();
		for(String s:new String[]{"roleSelector","roleItem"}) { 
		boolean change=false;
		
		int i=0;
		while((i=js.indexOf(s,i+1))>-1) {
			Pair<Integer,Character> p = ParseUtil.firstIndexOf(js,new char[] { '"','\'','`'},i);
			int j=js.indexOf(p.getValue2(),p.getValue1()+1);
			String role = js.substring(p.getValue1()+1,j);
			if(!role.startsWith("{") && !js.substring(i,p.getValue1()).contains(")")) {
				String e= getExistingRole(existingRoles, role);
				if(e!=null) {
					change = true;
					js = js.substring(0, p.getValue1()+1)+"{{{Roles::"+e+"}}}"+js.substring(j);
				} else if(!role.contains("%") && !role.contains("{")){
					String roleVar=role.replace("-", "_");
					
					for(int k=1;k<roleVar.length();k++) {
						if(Character.isUpperCase(roleVar.charAt(k) )) {
							roleVar = roleVar.substring(0,k)+"_"+roleVar.substring(k);
							k++;
						}
					}
					roleVar = roleVar.toUpperCase();
					boolean conflict=false;
					do {
					for(Pair<String,String>r:existingRoles) {
						if(r.getValue1().equals(roleVar)) {
							conflict=true;
						}
					}
					if(conflict) {
						roleVar = JOptionPane.showInputDialog("Konflikt Variablenname",roleVar);
						if(roleVar==null) {
							throw new CancelException();
						}
					}
					}while(conflict);
					js = js.substring(0, p.getValue1()+1)+"{{{Roles::"+roleVar+"}}}"+js.substring(j);
					newRoles.add(new Pair<String, String>(roleVar, role));
					existingRoles.add(newRoles.get(newRoles.size()-1));
				}
			}
		}
		if(change)
			Files.write(file, js.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
		
		}
		if(!newRoles.isEmpty() ) {
			writeRoles(base, newRoles);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length==0) {
			System.out.println("Argumente fehlen"); 
			return;
		}
		
		switch (args[0]) {
		case "roleconstants":
			Path base=Paths.get(args[1]);
			final List<Pair<String,String>> existingRoles = getRoles(base);
			for (String path:new String[] {"templates","subtemplates","layout_templates"}) {
				Files.walkFileTree(base.resolve("template").resolve(path), new FileVisitor<Path>() {
	
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if(file.toString().endsWith(".html")) {
							try {
								replaceRolesHtml(existingRoles, file,base);
							} catch (CancelException e) {
								return FileVisitResult.TERMINATE;
							}
						}
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
				});
			}
			Files.walkFileTree(base.resolve("template").resolve("js"), new FileVisitor<Path>() {
	
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if(file.toString().endsWith(".js")) {
							try {
								replaceRolesJs(existingRoles, file,base);
							} catch (CancelException e) {
								return FileVisitResult.TERMINATE;
							}
						}
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
	
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						// TODO Auto-generated method stub
						return FileVisitResult.CONTINUE;
					}
				});
			break;

		default:
			break;
		}
	}

}
