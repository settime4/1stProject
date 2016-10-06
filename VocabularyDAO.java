package myProject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import myProject.Vocabulary.WordClass;

public class VocabularyDAO {
	Vector<Vocabulary> vocabularies = null;
	HashMap<Integer, Vector<String>> columnNames = null;
	Vector<String>[] columnList = new Vector[6];
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private static Connection conn = null;
	private String url = VocaInterface.url;
	private String user = VocaInterface.user;
	private String password = VocaInterface.password;

	public VocabularyDAO() {
		try {
			Class.forName(driver);
			this.conn = DriverManager.getConnection(url, user, password);;
/*			if (conn != null) {
				System.out.println("DB 커넥션 객체 연결 성공");
			} else {
				System.out.println("DB 커넥션 객체 연결 실패");
			}*/
			setColumnNames();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("오타 또는 jar 파일 위치 확인 요망");
			e.printStackTrace();
		}
	}

	
	private void setColumnNames() {
		columnNames = new HashMap<Integer, Vector<String>>();
		for(int i=0 ; i<columnList.length;i++){
			columnList[i] = new Vector<String>();
			columnNames.put(i+1, columnList[i]);
		}
		columnList[0].add("No.");		columnList[0].add("表記");		columnList[0].add("読み方");		columnList[0].add("品詞");		columnList[0].add("意味");
		columnList[0].add("No.");		columnList[0].add("表記");		columnList[0].add("読み方");		columnList[0].add("品詞");		columnList[0].add("意味");
		columnList[1].add("表記");		columnList[1].add("読み方");		columnList[1].add("意味");
		columnList[1].add("表記");		columnList[1].add("読み方");		columnList[1].add("意味");
		columnList[2].add("意味");		columnList[2].add("表記");		columnList[2].add("読み方");
		columnList[2].add("意味");		columnList[2].add("表記");		columnList[2].add("読み方");
		columnList[3].add("No.");		columnList[3].add("表記");		columnList[3].add("読み方");	columnList[3].add("品詞");			columnList[3].add("意味");
		columnList[3].add("作成日");	columnList[3].add("変更日");	columnList[3].add("検索数");	columnList[3].add("正答数");		columnList[3].add("誤答数");
		columnList[4].add("No.");		columnList[4].add("表記");		columnList[4].add("正解の読み方");		columnList[4].add("お書き");
		columnList[4].add("採点");		columnList[4].add("正解の意味");		columnList[4].add("お書き");
		columnList[4].add("採点");		columnList[4].add("選択");
		columnList[5].add("No.");		columnList[5].add("意味");		columnList[5].add("正解の表記");		columnList[5].add("お書き");
		columnList[5].add("採点");		columnList[5].add("正解の読み方");		columnList[5].add("お書き");
		columnList[5].add("採点");		columnList[5].add("選択");
	}
	
	public Vector<String> getColumnNames(int tableModelType ) {
		return this.columnNames.get(tableModelType);
	}
	
	public Vector<Vocabulary> getVocabularies(String sql) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		vocabularies = new Vector<Vocabulary>();
		try {
			pstmt = conn.prepareStatement(sql.trim().replaceAll(";", ""));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Vocabulary vocabulary = new Vocabulary();
				vocabulary.setWletter(  (rs.getString("wletter")==null) ? rs.getString("rletter") : rs.getString("wletter")  );
				vocabulary.setRletter(rs.getString("rletter"));
				vocabulary.setWordClass(WordClass.convertEnum( rs.getString("wordclass") )   );
				vocabulary.setMeaning(rs.getString("meaning"));
				vocabulary.setRegdate( rs.getDate("regdate").toString() );
				vocabulary.setMdate(rs.getDate("mdate").toString());
				vocabulary.setSearchCount(rs.getInt("scount"));
				vocabulary.setRightCount(rs.getInt("rcount"));
				vocabulary.setWrongCount(rs.getInt("wcount"));
				
				vocabularies.add(vocabulary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			
		return this.vocabularies;
	}
	public Vector<Vocabulary> getVocasRandom() {
		int wordsize = VocaInterface.TEST_WORDS_SIZE;
		int ranksize = VocaInterface.TEST_RANKING_INDEX_SIZE;
		int indexsize = VocaInterface.TEST_INDEX_SIZE;
		
		String sql = "select *";
		sql+=" from (	select wletter, rletter, wordclass, meaning, regdate, mdate, scount, rcount, wcount, rank() over(order by( scount-3*rcount+2*wcount ) desc ) as ranking";
		sql+=" from VOCABULARIES where wletter != rletter order by dbms_random.value	) where ranking <= " + String.valueOf( ranksize ) ;
		sql+=" and rownum <= " + String.valueOf( indexsize );

		Vector<Vocabulary> sourceVocas = new Vector<Vocabulary>();
		vocabularies = this.getVocabularies(sql);
		
		if( vocabularies.size() > 0 ){
			sourceVocas.addAll(vocabularies);
			vocabularies.clear();
			vocabularies = this.getVocabularies("select * from vocabularies");
			
			boolean flag = vocabularies.size() > wordsize;
			
			if(flag){
				while( sourceVocas.size() < wordsize ){
					Vocabulary voca = vocabularies.get( new Random().nextInt(vocabularies.size()) );
					if ( ! sourceVocas.contains(voca)  ){
						sourceVocas.add( voca );
					}
				}			
			}else{
				while( sourceVocas.size() < vocabularies.size() ){
					Vocabulary voca = vocabularies.get( new Random().nextInt(vocabularies.size()) );
					if ( !sourceVocas.contains(voca)   ){
						sourceVocas.add( voca );
					}
				}			
			}
		}
		return sourceVocas;
	}

	public int insertVocabulary(Vocabulary vocabulary) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		PreparedStatement pstmt = null;
		String sql = "insert into vocabularies(wletter, rletter, wordclass, meaning, ";
		sql += "regdate, mdate, scount, rcount, wcount) values( ?, ?, ?, ?, to_date(?, 'yyyy-MM-dd'), to_date(?, 'yyyy-MM-dd'), ?, ?, ? )";
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vocabulary.getWletter());
			pstmt.setString(2, vocabulary.getRletter());
			pstmt.setString(3, vocabulary.getWordClass().getName());
			pstmt.setString(4, vocabulary.getMeaning());
			pstmt.setDate(5, new Date(  Calendar.getInstance().getTimeInMillis() )   );
			pstmt.setDate(6, new Date(  Calendar.getInstance().getTimeInMillis() )   );
			pstmt.setInt(7, vocabulary.getSearchCount());
			pstmt.setInt(8, vocabulary.getRightCount());
			pstmt.setInt(9, vocabulary.getWrongCount());
			cnt = pstmt.executeUpdate();
			conn.commit();
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			System.out.println("Error code : " + errCode);
			cnt = -errCode;
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	public int updateVocabulary(Vocabulary vocabulary, String ex_wletter, String ex_meaning ) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		PreparedStatement pstmt = null;
		String sql = "update vocabularies set wletter = ?, rletter = ?, wordclass = ?, meaning =?, mdate = to_date(?, 'yyyy-MM-dd'),  ";
		sql += " scount = ?, rcount = ?, wcount = ? where wletter = ? and meaning = ?";
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vocabulary.getWletter());
			pstmt.setString(2, vocabulary.getRletter());
			pstmt.setString(3, vocabulary.getWordClass().getName());
			pstmt.setString(4, vocabulary.getMeaning());
			pstmt.setDate(5,  new Date(  Calendar.getInstance().getTimeInMillis() )  );
			pstmt.setInt(6, vocabulary.getSearchCount()); // 검색 버튼 눌렀을 때
			pstmt.setInt(7, vocabulary.getRightCount()); // 채점 결과 저장 버튼 눌렀을 때
			pstmt.setInt(8, vocabulary.getWrongCount()); // 채점 결과 저장 버튼 눌렀을 때
			pstmt.setString(9, ex_wletter);
			pstmt.setString(10, ex_meaning);
			cnt = pstmt.executeUpdate();
			conn.commit();
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			System.out.println("Error code : " + errCode);
			cnt = -errCode;
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	public int updateVocabulary(Vocabulary vocabulary ) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		PreparedStatement pstmt = null;
		String sql = "update vocabularies set wletter = ?, wordclass = ?,  mdate = to_date(?, 'yyyy-MM-dd'), scount = ?, ";
		sql += "rcount = ?, wcount = ? where wletter = ? and meaning = ?";
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vocabulary.getWletter());
			pstmt.setString(2, vocabulary.getWordClass().getName());
			pstmt.setDate(3,  new Date(  Calendar.getInstance().getTimeInMillis() )  );
			pstmt.setInt(4, vocabulary.getSearchCount()); // 검색 버튼 눌렀을 때
			pstmt.setInt(5, vocabulary.getRightCount()); // 채점 결과 저장 버튼 눌렀을 때
			pstmt.setInt(6, vocabulary.getWrongCount()); // 채점 결과 저장 버튼 눌렀을 때
			pstmt.setString(7, vocabulary.getWletter());
			pstmt.setString(8, vocabulary.getMeaning());
			cnt = pstmt.executeUpdate();
			conn.commit();
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			System.out.println("Error code : " + errCode);
			cnt = -errCode;
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	public int deleteVocabulary(String wletter, String meaning) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		PreparedStatement pstmt = null;
		String sql = "delete from vocabularies where wletter = ? and meaning = ?";
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, wletter);
			pstmt.setString(2, meaning);
			cnt = pstmt.executeUpdate();
			conn.commit();
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			System.out.println("Error code : " + errCode);
			cnt = -errCode;
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	public int writeNote(){
		int cnt = VocaInterface.ERROR_NoteWriter;
		NoteIOHandler niohandler = new NoteIOHandler();
		this.vocabularies = getVocabularies("select * from vocabularies");
		cnt = niohandler.noteWriter(vocabularies);
		return cnt;
	}
	
	public int[] readNote(String filepath){
		int[] cnt = null;
		NoteIOHandler niohandler = new NoteIOHandler();
		this.vocabularies = niohandler.noteReader(filepath);
		
		PreparedStatement pstmt = null;
		String sql = "insert into vocabularies(wletter, rletter, wordclass, meaning, ";
		sql += "regdate, mdate, scount, rcount, wcount) values( ?, ?, ?, ?, to_date(?, 'yyyy-MM-dd'), to_date(?, 'yyyy-MM-dd'), ?, ?, ? )";
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			int index = 0;
			while(index< vocabularies.size()){
				pstmt.setString(1, vocabularies.get(index).getWletter());
				pstmt.setString(2, vocabularies.get(index).getRletter());
				pstmt.setString(3, vocabularies.get(index).getWordClass().getName());
				pstmt.setString(4, vocabularies.get(index).getMeaning());
				pstmt.setDate(5, new Date(Calendar.getInstance().getTimeInMillis()) );
				pstmt.setDate(6, new Date(Calendar.getInstance().getTimeInMillis()) );
				pstmt.setInt(7, vocabularies.get(index).getSearchCount());
				pstmt.setInt(8, vocabularies.get(index).getRightCount());
				pstmt.setInt(9, vocabularies.get(index).getWrongCount());
				pstmt.addBatch();
				index++;
			}
			int[] updateCounts = pstmt.executeBatch();
			cnt = updateCounts;
			conn.commit();
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			try{
				System.out.println("Error code : " + errCode);
				e.printStackTrace();
				conn.rollback();
			}catch(SQLException e1){
				e1.printStackTrace();
			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	public int readNoteSafe(String filepath, int initnum){
		int cnt = VocaInterface.ERROR_NoteReader;
		NoteIOHandler niohandler = new NoteIOHandler();
		this.vocabularies = niohandler.noteReader(filepath);
		PreparedStatement pstmt = null;
		String sql = "insert into vocabularies(wletter, rletter, wordclass, meaning, ";
		sql += "regdate, mdate, scount, rcount, wcount) values( ?, ?, ?, ?, to_date(?, 'yyyy-MM-dd'), to_date(?, 'yyyy-MM-dd'), ?, ?, ? )";
		int index = initnum;
		try{
			while(index< vocabularies.size()){
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vocabularies.get(index).getWletter());
				pstmt.setString(2, vocabularies.get(index).getRletter());
				pstmt.setString(3, vocabularies.get(index).getWordClass().getName());
				pstmt.setString(4, vocabularies.get(index).getMeaning());
				pstmt.setDate(5, new Date(Calendar.getInstance().getTimeInMillis()) );
				pstmt.setDate(6, new Date(Calendar.getInstance().getTimeInMillis()) );
				pstmt.setInt(7, vocabularies.get(index).getSearchCount());
				pstmt.setInt(8, vocabularies.get(index).getRightCount());
				pstmt.setInt(9, vocabularies.get(index).getWrongCount());
				cnt = pstmt.executeUpdate();
				conn.commit();
				pstmt.close();
				index++;
			}
		}catch(SQLException e){
			int errCode = e.getErrorCode();
			//System.out.println("Error code : " + errCode);
			if(errCode == 1){
				niohandler.noteWriter(vocabularies.get(++index));
				this.readNoteSafe(filepath, index);
			}
//			try{
//				e.printStackTrace();
//				conn.rollback();
//			}catch(SQLException e1){
//				e1.printStackTrace();
//			}
		}finally{
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cnt;
	}
	
	public static void setConnectionClose(){
		try {
			if(conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
