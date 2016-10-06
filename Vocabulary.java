package myProject;

import java.util.Vector;


public class Vocabulary {

	private String wletter, rletter, meaning, regdate, mdate;
	private WordClass wordclass;
	private int scount = 0, rcount= 0, wcount = 0;
	//表記(Writing Letters), 読み方(Reading Letters), 品詞(a part of speech = Word Class), 意味(Meaning)
	//変更日(mdate), 検索数(search count),	正答数(right count),	誤答数(wrong count)
	public Vocabulary(){
		//dummy voca
		this.wletter = "";
		this.rletter = "";
		this.wordclass = wordclass.ETC;
		this.meaning = "";
		this.setRegdate("");
		this.mdate = "";
		this.scount = 0;
		this.rcount = 0;
		this.wcount = 0;
	}
	public Vocabulary(String wletter, String rletter, WordClass wordclass, String meaning, String regdate, String mdate,
			int scount, int rcount, int wcount) {
		this.wletter = wletter;
		this.rletter = rletter;
		this.wordclass = wordclass;
		this.meaning = meaning;
		this.regdate= regdate;
		this.mdate = mdate;
		this.scount = scount;
		this.rcount = rcount;
		this.wcount = wcount;
	}
	
	@Override
	public String toString() {
		return "Vocabulary [wletter=" + wletter + ", rletter=" + rletter
				+ ", meaning=" + meaning + ", regdate=" + regdate + ", mdate="
				+ mdate + ", wordclass=" + wordclass + ", scount=" + scount
				+ ", rcount=" + rcount + ", wcount=" + wcount + "]";
	}
	public String getWletter() {
		return (wletter!=null) ? wletter : rletter;
	}
	public void setWletter(String wletter) {
		this.wletter = wletter;
	}
	public String getRletter() {
		return rletter;
	}
	public void setRletter(String rletter) {
		this.rletter = rletter;
	}
	public WordClass getWordClass() {
		return this.wordclass;
	}
	public void setWordClass(WordClass wordClass) {
		try{
			this.wordclass = wordClass;
		}catch(Exception e){
			System.out.println("WordClass Enum에 없는 항목입니다.");
		}
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getMdate() {
		return mdate;
	}
	public void setMdate(String mdate) {
		this.mdate = mdate;
	}
	public int getSearchCount() {
		return scount;
	}
	public void setSearchCount(int scount) {
		this.scount = scount;
	}
	public int getRightCount() {
		return rcount;
	}
	public void setRightCount(int rcount) {
		this.rcount = rcount;
	}
	public int getWrongCount() {
		return wcount;
	}
	public void setWrongCount(int wcount) {
		this.wcount = wcount;
	}



	enum WordClass{
		//품사전체, 명사, 대명사, 동사, 조사, 형용사, 접사, 부사, 감동사, 형용동사, 기타
		NOUN("명사"), VERB("동사"), ADJECTIVE("형용사"), DescribeVerb("형용동사"), AdVerb("부사"),  
		//Noun(1), Verb(2), Adjective(3), DescribeVerb(4), Adverb(5)
		AFFIX("접사"), PRONOUN("대명사"), POSTPOSITION("조사"), EXCLAMATION("감동사"), ETC("기타");
		// Affix(6), Pronoun(7), postposition(8), Exclamation(9), Etc(10);
		private final String name;
		private WordClass(String name){
			this.name = name;
		}
		public String getName(){
			return name;
		}
		public static WordClass convertEnum(String str){
			for(WordClass wordclass : WordClass.values())
				if(wordclass.getName().equals(str))
					return wordclass;
			return null;
		}
		public static boolean hasString(String str){
			for(WordClass wordclass : WordClass.values()){
				return wordclass.getName().equals(str);
			}
			return false;
		}
		public static Vector<String> getNames(){
			Vector<String> namesVector = new Vector<String>();
			for (WordClass wordclass : WordClass.values()) {
				namesVector.add( wordclass.getName() );
			}
			return namesVector;
		}
	}
}
