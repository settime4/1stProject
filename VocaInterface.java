package myProject;

import java.awt.Font;


public interface VocaInterface {
	
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "user01"; // database user name
	String password = "oracle"; // database user Password
	
	String[] images = {"c:\\user01\\main00.png", "c:\\user01\\main01.png", "c:\\user01\\main02.png", "c:\\user01\\main03.png", "c:\\user01\\main04.png"};
	String pathname = "c:\\user01\\notepad\\";
	
	int VocaFileNumberLimit = 100; // the numbers of vocabulary per page in Vocabulary List Mode 
	
	int TEST_WORDS_SIZE = 50; // the total of words in the Test Mode
	int TEST_INDEX_SIZE = 15; // taking test mode, this size of words would be listed up at first into the test words size, by an index, which is scount * 1 + wcount * 2 - rcount * 3
	int TEST_RANKING_INDEX_SIZE = 100; // taking test mode, this size is supposed to make up the range of the pool, ordered by the index
	
	Font Voca_FONT = new Font("Meiryo", 0, 12);
	Font Function_FONT = new Font("Meiryo", 0, 15);
	int COMPONENT_SIZE = 15;
	int CELL_HEIGHT = 25;
	int CELL_WIDTH = 40;
	int TABLE_HEIGHT = CELL_HEIGHT * 26 - 2;
	int TABBED_HEIGHT = 420;
	int TABBED_WIDTH = 210;
	
	int ERROR_DEFAULT = -99999; //To evade oracle Error
	int ERROR_NoteWriter = -88888;
	int ERROR_NoteReader = -77777;
	int SUCCESS_NoteWriter = 88888;
	int SUCCESS_NoteReader = 77777;
}
