package myProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import myProject.Vocabulary.WordClass;

public class VocaTabPanel extends JPanel {
	private int tableModelType = 0;
	private VocaTableModel tablemodel = null; 
	private JTabbedPane jtabPane = null;
	private JPanel searchPane = null, sortingPane = null;
	private JScrollPane jsPane = null;
	private final int composize = VocaInterface.COMPONENT_SIZE;
	private final int tabWidth = VocaInterface.TABBED_WIDTH;
	private final int tabHeight = VocaInterface.TABBED_HEIGHT;

	public VocaTabPanel(VocaTableModel tablemodel, int tableModelType) {
		this.tableModelType = tableModelType;
		this.tablemodel = tablemodel;
		compose();
	}
	
	private void compose() {
		jtabPane = new JTabbedPane(JTabbedPane.TOP);
		
		searchPane = new SearchPanel();
		jtabPane.add("検索·追加", searchPane);
		
		if (tableModelType == 1) {
			sortingPane = new SortingPanel();
			jtabPane.add("整列", sortingPane);
		}
		
		jsPane = new JScrollPane(jtabPane);
		jsPane.setPreferredSize(new Dimension(tabWidth, tabHeight));
		super.setLayout(new BorderLayout());
		super.add(jsPane, BorderLayout.CENTER);
	}
	class SearchPanel extends JPanel{
		String[] slabel = {"検索語", "読み方", "表記", "品詞", "意味"};
		JLabel[] jlabel = new JLabel[slabel.length];
		JTextField[] jfield = new JTextField[slabel.length - 2];
		JComboBox<String> wordclassCombo = new JComboBox<String>(WordClass.getNames());
		JTextArea jtextarea = new JTextArea();
		JButton searchbtn = new JButton("検索する");
		JButton addbtn = new JButton("単語を追加する");
		String[] searchcombolist = {"前後方", "完全", "前方", "後方" };
		JComboBox<String> searchoptioncombo = new JComboBox<String>(searchcombolist);
		public SearchPanel() {
			this.compose();
			this.setevent();

		}
		private void compose() {
			super.setLayout(null);
			for (int i = 0; i < jlabel.length; i++) {
				jlabel[i] = new JLabel(slabel[i]);
				if(i==0){
					jlabel[i].setBounds(composize, 3 * composize + i *  composize, 3 * composize, 2 * composize);	
				}else{
					jlabel[i].setBounds(composize, 10 * composize + 2 * composize * i, 3 * composize, 2 * composize);
				}
				super.add(jlabel[i]);
			}
			
			for (int i = 0; i < jfield.length; i++) {
				jfield[i] = new JTextField();
				if(i==0){
					jfield[i].setBounds(5 * composize, 3 * composize, 7 * composize,  2 * composize);	
				}else{
					jfield[i].setBounds(5 * composize, 10 * composize + i * 2 * composize, 7 * composize,  2 * composize);
				}
				jfield[i].addKeyListener(new VocaKeyHandler());
				super.add(jfield[i]);
			}
			searchoptioncombo.setBounds( 5, 6 * composize, 5 * composize, 2 * composize);
			searchoptioncombo.setFont(new Font("Meiryo", 0, 12));
			super.add(searchoptioncombo);
			searchbtn.setBounds( 6 * composize, 6 * composize, 7 * composize, 2 * composize);
			super.add(searchbtn);
			
			wordclassCombo.setBounds(5 * composize, 10 * composize + ( 3 ) * 2 * composize, 7 * composize,  2 * composize);
			wordclassCombo.setFont(new Font("serif", 0, 12));
			wordclassCombo.setEditable(true);
			super.add(wordclassCombo);
			jtextarea.setFont(new Font("Meiryo", Font.BOLD, 15));
			jtextarea.setBounds(5 * composize, 10 * composize + ( 4 ) * 2 * composize, 7 * composize,  4 * composize);
			jtextarea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
			super.add(jtextarea);
			
			addbtn.setBounds( 2 * composize, 23 * composize, 10 * composize, 2 * composize);
			super.add(addbtn);

		}
		private void setevent() {
			searchbtn.addKeyListener(new VocaKeyHandler());
			searchbtn.addMouseListener(new VocaMouseHandler());
			jtextarea.addKeyListener(new VocaKeyHandler());
			addbtn.addKeyListener(new VocaKeyHandler());
			
			addbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Vocabulary voca = getVocaInfo();
					if ( voca.getRletter().length() > 0 && voca.getMeaning().length() > 0 ){
						int cnt = tablemodel.insertVocabulary(voca);
						tablemodel.vocaCounts.add(tablemodel.vocaCounts.size() + 1);
						if( cnt != VocaInterface.ERROR_DEFAULT){
							for (int i = 1; i < jfield.length; i++) {
							jfield[i].setText(null);
							jtextarea.setText(null);
							}
							jfield[1].requestFocus();
						}						
					} 
				}
			});
			
		}
		class VocaMouseHandler extends MouseAdapter{
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getSource() == searchbtn ){
					searchVoca();
				}
			}
		}
		class VocaKeyHandler extends KeyAdapter{
			@Override
			public void keyPressed(KeyEvent e) {
				Object obj = e.getSource();
				int keycode = e.getKeyCode();
				if( obj instanceof JTextField || obj instanceof JTextArea){
					if( keycode == KeyEvent.VK_ENTER ){
						if( obj == jfield[0]){
							searchbtn.requestFocus();
						}else if( obj == jtextarea ){
							addbtn.requestFocus();
						}else{
							( (Component)obj ).transferFocus();
						}
					}
				}
				
				else if( obj instanceof JButton){
					if( keycode == KeyEvent.VK_ENTER ){
						if( e.getSource() == searchbtn ){
							searchVoca();
						}else if( e.getSource() == addbtn ){
							Vocabulary voca = getVocaInfo();
							if ( voca.getRletter().length() > 0 && voca.getMeaning().length() > 0 ){
								int cnt = tablemodel.insertVocabulary(voca);
								tablemodel.vocaCounts.add(tablemodel.vocaCounts.size() + 1);
								if( cnt != VocaInterface.ERROR_DEFAULT){
									for (int i = 1; i < jfield.length; i++) {
									jfield[i].setText(null);
									jtextarea.setText(null);
									}
									jfield[1].requestFocus();
								}						
							} 
						}
					}
				}
			}
			@Override
			public void keyTyped(KeyEvent e) {
				Object obj = e.getSource();
				int i = e.getKeyChar();
				if( obj instanceof JTextField  ){
					if( Character.isSpaceChar(i) ){
						getToolkit().beep();
						e.consume();
					}
				}
				if( obj == jfield[1] || obj == jfield[2] ){
					if( (i >= 'ㄱ')  && ( i <= 'ㅎ' ) ){
						getToolkit().beep();
						e.consume();
					}else if( (i >= 'ㅏ')  && ( i <= 'ㅣ' ) ){
						getToolkit().beep();
						e.consume();
					}else if( (i >= '가')  && ( i <= '히' ) ){
						getToolkit().beep();
						e.consume();
					}
				}
			}
		}
		protected Vocabulary getVocaInfo() {
			Vocabulary voca = new Vocabulary( (jfield[2].getText().length()==0l) ? jfield[1].getText() : jfield[2].getText(), jfield[1].getText(), 
					WordClass.convertEnum(  (String) wordclassCombo.getSelectedItem() ), jtextarea.getText(), new Date( Calendar.getInstance().getTimeInMillis() ).toString(), 
					new Date( Calendar.getInstance().getTimeInMillis() ).toString(), 0, 0, 0);
			return voca;
		}
		protected  void searchVoca(){
			String text = jfield[0].getText();
			if(text.length() != 0) {
				VocaTablePanel searchPanel = new VocaTablePanel(4);
				VocaTableModel model = searchPanel.tablemodel;
				String sql = "select * from vocabularies where ";
				switch (searchoptioncombo.getSelectedIndex() ) {
					case 0 :
						sql +="wletter like '%"+text+"%' or rletter like'%"+text+"%' or meaning like '%"+text+"%'  ";
						break;
					case 1 :
						sql +="wletter like '"+text+"' or rletter like'"+text+"' or meaning like '%"+text+"%'  ";
						break;
					case 2 :
						sql +="wletter like '"+text+"%' or rletter like'"+text+"%' or meaning like '%"+text+"%'  ";
						break;
					case 3 :
						sql +="wletter like '%"+text+"' or rletter like'%"+text+"' or meaning like '%"+text+"%'  ";
						break;
				}
				model.vocabularies = model.dao.getVocabularies(sql);
				model.vocaCounts = model.getVocaCount();
				for (Vocabulary voca : searchPanel.tablemodel.vocabularies) {
					voca.setSearchCount( voca.getSearchCount() + 1 );
					tablemodel.updateVocabulary(voca);
				}
				model.fireTableDataChanged();
				try {
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				} catch (ClassNotFoundException
						| InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				JFrame jframe = new JFrame("検索結果");
				jframe.add(searchPanel);
				jframe.setSize( searchPanel.tableWidth +VocaInterface.CELL_WIDTH*1, VocaInterface.TABLE_HEIGHT * 11 / 10 );
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				jframe.setLocation( (int) (screen.getWidth() - jframe.getWidth() ) / 2 , (int) (screen.getHeight() - jframe.getHeight() ) / 2 - 10 );
				jframe.setVisible(true);
			}
		}
	}
	class SortingPanel extends JPanel{
		String[] slabel = {"品詞別", "指数順", "文字順", "作成順", "変更日順"};
		JLabel[] jlabel = new JLabel[slabel.length];
		JComboBox[] jcombo = new JComboBox[5];
		String[] indexstr = {"無効", "検索順", "正答数順", "誤答数順", "統合指数順"};
		String[] orderstr = {"無効", "上がり順", "下がり順"};
		String[] btnstr = {"整列する", "整列の初期化"};
		JButton[] jbtn = new JButton[btnstr.length];
		Vector<JComboBox> sortedlists = new Vector<JComboBox>();
		
		public SortingPanel() {
			this.compose();
			this.setevent();
		}
		private void compose() {
			super.setLayout(null);
			for (int i = 0; i < slabel.length; i++) {
				jlabel[i] = new JLabel(slabel[i]);
				jlabel[i].setBounds(composize, 3 * composize * (i + 1), 3 * composize, 2 * composize);
				super.add(jlabel[i]);
			}
			
			Vector<String> wdclassVector = WordClass.getNames();
			wdclassVector.add(0, "無効");
			wdclassVector.add(1, "品詞別"); // groub by wordclass
			jcombo[0] = new JComboBox<String>( wdclassVector );
			jcombo[1] = new JComboBox<String>(indexstr);
			jcombo[2] = new JComboBox<String>(orderstr);
			jcombo[3] = new JComboBox<String>(orderstr);
			jcombo[4] = new JComboBox<String>(orderstr);
			
			for (int i = 0; i < jcombo.length; i++) {
				jcombo[i].setFont(new Font("serif", 0, 12) );
				jcombo[i].setBounds( 5 * composize, 3 * composize * (i + 1), 7 * composize, 2 * composize   );
				super.add(jcombo[i]);
			}
			for (int i = 0; i < btnstr.length; i++) {
				jbtn[i] = new JButton(btnstr[i]);
				jbtn[i].setBounds(2 * composize, 18 * composize + i * 3 * composize, 10 * composize, 2 * composize);
				super.add(jbtn[i]);
			}
		}
		private void setevent() {
			for( int i = 0; i < jcombo.length; i++ ){
				jcombo[i].addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if( e.getItem() != ( (JComboBox) e.getSource()).getItemAt(0) ){
							if( !  sortedlists.contains( e.getSource() )  ){
								sortedlists.add( (JComboBox)e.getSource() );	
							}
						}else{
							sortedlists.remove(  (JComboBox) e.getSource() );
						}
					}
				});
			}
			
			jbtn[0].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String sql = " select * from vocabularies ";
					for (int i = 0; i < sortedlists.size(); i++) {
						
						if( i == 0 ){
							if( (sortedlists.get(i) == jcombo[0] && sortedlists.get(i).getSelectedIndex() == 1) ){
								sql += " order by ";
							}else if( sortedlists.get(i) != jcombo[0]){
								sql += " order by ";
							}
						}
						
						if( i > 0 ){
							if( sortedlists.get(0) == jcombo[0] && ! sql.contains("order by") ){
								sql += " order by ";
							}else if(( sortedlists.get(i) == jcombo[0] && sortedlists.get(i).getSelectedIndex() == 1 && sql.contains("order by") ) ){
								sql +=", ";
							}else if( sortedlists.get(i) != jcombo[0] && sql.contains("order by") ){
								sql +=", ";
							}
							
						}
						
						if ( sortedlists.get(i) == jcombo[0] ){
							if ( sortedlists.get(i).getSelectedIndex() == 1){
								sql +="wordclass";
							}else if ( sortedlists.get(i).getSelectedIndex() >= 2 && sortedlists.get(i).getSelectedIndex() <= 11 ){
								StringBuffer sbf = new StringBuffer(sql);
								sbf.insert(sql.indexOf( "vocabularies" ) + "vocabularies".length() , " where wordclass=");
								
								sbf.insert( sbf.indexOf("wordclass") + "wordclass".length() + 1 ,  " '" + sortedlists.get(i).getSelectedItem() + "' " );
								sql = sbf.toString();
							}
						}else if(sortedlists.get(i) == jcombo[1]){
							switch( sortedlists.get(i).getSelectedIndex() ){
								case 1:
									sql += "scount desc";
									break;
								case 2:
									sql += "rcount desc";
									break;
								case 3:
									sql += "wcount desc";
									break;
								case 4:
									sql += "(scount + 2 * wcount - 3 * rcount) desc";
									break;
							}
							
						}else if( sortedlists.get(i) == jcombo[2] ){
							switch( sortedlists.get(i).getSelectedIndex() ){
								case 1:
									sql += "rletter";
									break;
								case 2:
									sql += "rletter desc";
									break;
							}
						}else if( sortedlists.get(i) == jcombo[3] ){
							switch( sortedlists.get(i).getSelectedIndex() ){
								case 1:
									sql += "regdate";
									break;
								case 2:
									sql += "regdate desc";
									break;
							}
						}else if( sortedlists.get(i) == jcombo[4] ){
							switch( sortedlists.get(i).getSelectedIndex() ){
								case 1:
									sql += "mdate";
									break;
								case 2:
									sql += "mdate desc";
									break;
							}
						}
						
					}
					tablemodel.vocabularies = tablemodel.dao.getVocabularies(sql);
					tablemodel.fireTableDataChanged();
				}
			});
			
			jbtn[1].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					while( sortedlists.size() > 0 ){
						JComboBox jcombo = sortedlists.get(0);
						jcombo.setSelectedIndex(0);
					}
				}
			});
		}
	}
}