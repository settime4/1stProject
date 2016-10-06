package myProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import myProject.Vocabulary.WordClass;

public class NoteIOHandler {
	Vector<Vocabulary> vocabularies = null;
	public NoteIOHandler() {}
	
	public int noteWriter(Vector<Vocabulary> vocabularies){ //for advanced mode
		int i = VocaInterface.ERROR_NoteWriter;
		this.vocabularies = vocabularies;
		String pathname = VocaInterface.pathname;
		File myfolder = new File(pathname);
		File myfile = null;

		if( ! myfolder.exists()){
			myfolder.mkdir();
		}
		int num = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("MyVoca_00");
		file_name:
		while(num < VocaInterface.VocaFileNumberLimit){
			myfile = new File(pathname, sdf.format( Calendar.getInstance().getTime() ) + df.format(num) +".txt");
			if( ! myfile.exists() ){
				pathname = pathname + sdf.format( Calendar.getInstance().getTime() ) + df.format(num)+".txt";
				myfile = new File(pathname);
				break file_name;
			}
			num++;
		}
		try {
			FileWriter fw = new FileWriter(myfile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Vocabulary voca : vocabularies) {
				bw.write(voca.getRletter());
				if( voca.getWletter() == null){
					bw.write( "["+voca.getRletter() +"]" );
					bw.write( " 발음 재생" );
					bw.newLine();
				}else{
					bw.write("["+voca.getWletter()+"]");
					bw.write( " 발음 재생" );
					bw.newLine();
				}
				
				bw.write("["+voca.getWordClass().getName()+"]");
				bw.write(voca.getMeaning());
				bw.newLine();
				bw.newLine();
			}
			bw.close();
			fw.close();
			i = VocaInterface.SUCCESS_NoteWriter;
		} catch (IOException e) {
		}
		return i;
	}
	public int noteWriter(Vocabulary voca){ //for safe mode
		int i = VocaInterface.ERROR_NoteWriter;
		String pathname = VocaInterface.pathname;
		File myfolder = new File(pathname);
		File myfile = null;

		if( ! myfolder.exists()){
			myfolder.mkdir();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		myfile = new File(pathname, sdf.format( Calendar.getInstance().getTime() )+ "DuplicatedVoca" + ".txt");
		try {
			String filetext = null;
			if( myfile.exists() ){
				FileReader fr = new FileReader(myfile);
				BufferedReader br = new BufferedReader(fr);
				filetext ="";
				String noteline = null;
				while( (noteline = br.readLine()) != null ){
					filetext +=noteline+"\r\n";
				}
				fr.close();
				br.close();	
			}
			FileWriter fw = new FileWriter(myfile);
			BufferedWriter bw = new BufferedWriter(fw);
			if( filetext != null){
				bw.write(filetext);
			}
			bw.write(voca.getRletter());
			if( voca.getWletter() == null){
				bw.write( "["+voca.getRletter() +"]" );
				bw.write( " 발음 재생" );
				bw.newLine();
			}else{
				bw.write("["+voca.getWletter()+"]");
				bw.write( " 발음 재생" );
				bw.newLine();
			}
			
			bw.write("["+voca.getWordClass().getName()+"]");
			bw.write(voca.getMeaning());
			bw.newLine();
			bw.newLine();
			bw.close();
			fw.close();
			i = VocaInterface.SUCCESS_NoteWriter;
		} catch (IOException e) {
		}
		return i;
	}
	public Vector<Vocabulary> noteReader(String filename){
		Vector<Vocabulary> vocabularies = new Vector<Vocabulary>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String str;
			int i = 0;
			Vocabulary voca = null; 	
			while( (str=br.readLine()) != null ){
				if(str.length() == 0){
					continue;
				}
				if( str.indexOf("[") > 0  ){
					voca = new Vocabulary();
					voca.setRletter( str.substring(0, str.indexOf("[")) );
					voca.setWletter( str.substring(str.indexOf("[")+1, str.indexOf("]")) );
				}else if( str.indexOf(" 발음 재생") > 0 ) {
					voca = new Vocabulary();
					voca.setRletter( str.substring(0, str.indexOf(" 발음 재생")) );
					voca.setWletter( voca.getRletter() );
				}else if(str.startsWith("[") ) {
					voca = vocabularies.get(i);
					vocabularies.remove(i);
					voca.setWordClass(   WordClass.convertEnum( str.substring(1, str.indexOf("]")) )   );
					voca.setMeaning(   str.substring( str.indexOf("]") +1 , str.length() )   );
					i++;
				}else{
					voca = new Vocabulary();
					voca.setRletter(  str.substring(0, str.length()-1 )       );
					voca.setWletter(  voca.getRletter() );
				}
				vocabularies.add(voca);
			}
		} catch (IOException e) {
		}  finally{
			try {
				if(br!=null)
					br.close();
				if(fr!=null)
					fr.close();
			} catch (Exception e2) {
			}
		}
		return vocabularies;
	}
}