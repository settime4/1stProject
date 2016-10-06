package myProject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class VocaStatePanel extends JPanel {
		private int tableModelType = 0;
		private VocaTableModel tablemodel = null;
		private int composize = VocaInterface.COMPONENT_SIZE;
		private int cellwidth = VocaInterface.CELL_WIDTH;
		private int tableHeight = VocaInterface.TABLE_HEIGHT;
		private JButton newtest= null, showAnswer = null ;
		protected JLabel[] jlabel = null;
		private JButton[] jbutton = null;
		
		
		
		public VocaStatePanel( VocaTableModel tablemodel, int tableModelType) {
			this.tablemodel = tablemodel;
			this.tableModelType = tableModelType;
			this.compose();
			this.setevent();
		}
		private void compose() {
			if(tableModelType == 2 || tableModelType ==3){
				super.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));
				newtest = new JButton("新しいテスト");	
				showAnswer = new JButton("正解を確認する");
				
				newtest.setPreferredSize(new Dimension(composize* 10, composize*5));
				showAnswer.setPreferredSize(new Dimension(composize* 10, composize*5));
				
				super.add(newtest );
				super.add(showAnswer);
				
			}else if(tableModelType == 5 || tableModelType == 6){
				super.setLayout(new GridLayout(3, 4, 5, 5));
				jlabel = new JLabel[6];
				jbutton = new JButton[6];
				String[] lbltext = {"正解数  ", "", "誤答数  ", "", "保留数  ", ""};
				String[] btntext = {"選択を正解処理","選択を削除処理","選択を保留処理","結果を保存して消す","選択を誤答処理","保存しないで消す"};

				for (int i = 0; i < lbltext.length; i++) {
					jlabel[i] = new JLabel(lbltext[i]);
					jlabel[i].setPreferredSize(new Dimension(composize*2, composize *2 ) );
				}
				int[] score = scoreTest(this.tablemodel);
				for(int i =0; i<score.length; i++){
					jlabel[ i*2+1 ].setText( String.valueOf( score[i] ) );
				}
				
				for (int i = 0; i < btntext.length; i++) {
					jbutton[i] = new JButton(btntext[i]);
					jbutton[i].setPreferredSize(new Dimension(composize*4, composize*2) );
					jbutton[i].addActionListener(new VocaAnswersActionListener());
				}
				for( int i=0 ; i < 3; i++){
					for( int j =0; j < 4; j++){
						switch( j % 4 ){
							case 0:
								jlabel[i*2+j%2].setHorizontalAlignment( SwingConstants.RIGHT );
								super.add( jlabel[i*2+j%2] );
								break;
							case 1:
								jlabel[i*2+j%2].setHorizontalAlignment( SwingConstants.CENTER );
								super.add( jlabel[i*2+j%2] );
								break;
							case 2:
								super.add( jbutton[i*2+j%2] );
								break;
							case 3:
								super.add( jbutton[i*2+j%2] );
								break;
						}
					}
				}
			}
		}
		private void setevent() {
			
			if(tableModelType == 2 || tableModelType ==3 ){
				newtest.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MainProgram.updateObserver(tableModelType);
					}
				});
				showAnswer.addActionListener(new VocaTestActionListener() );
				
			}

			
		}
		class VocaAnswersActionListener implements ActionListener{
			//"選択の正答処理","選択の削除処理","選択の採点取り消し","結果を保存して消す","選択のご誤答処理","保存しないで消す"
			// 				0						1					2						3								4						5
			@Override
			public void actionPerformed(ActionEvent e) {
				if( e.getSource() == jbutton[0]){
					for( int i=0; i < tablemodel.vocabularies.size() ;i++){
						if((Boolean)tablemodel.getValueAt( i, 8) == true){
							if( ! ( (String)tablemodel.getValueAt(i, tablemodel.findColumn("お書き")) ).matches("ibid.") ){
								tablemodel.setValueAt("O", i, 4);
							}
							if( ! ( (String)tablemodel.getValueAt(i, 6) ).matches("ibid.") ){
								tablemodel.setValueAt("O", i, 7);
							}
							tablemodel.setValueAt(false, i, 8);
//							tablemodel.fireTableDataChanged();
						}
					}
					tablemodel.fireTableDataChanged();
					updateScoreText(tablemodel);
				}else if(e.getSource() == jbutton[1]){
					for( int i=0; i < tablemodel.vocabularies.size() ;i++){
						if((Boolean)tablemodel.getValueAt( i, 8) == true){
							Vocabulary voca = tablemodel.vocabularies.get(i);
							tablemodel.checkedBoxColumn.remove(i);
							tablemodel.scoreBoxColumn.remove(i);
							tablemodel.typedVocas.remove(i);
							//tablemodel.setFlagtoeven();
							tablemodel.deleteVocabulary(voca);
							tablemodel.vocaCounts.remove(i);
							tablemodel.fireTableDataChanged();
							i-=1;
						}
					}
					updateScoreText(tablemodel);
				}else if(e.getSource() == jbutton[2]){
					for( int i=0; i < tablemodel.vocabularies.size() ;i++){
						if((Boolean)tablemodel.getValueAt( i, 8) == true){
							if( ! ( (String)tablemodel.getValueAt(i, tablemodel.findColumn("お書き")) ).matches("ibid.") ){
								tablemodel.setValueAt("保留", i, 4);
							}
							if( ! ( (String)tablemodel.getValueAt(i, 6) ).matches("ibid.") ){
								tablemodel.setValueAt("保留", i, 7);
							}
							tablemodel.setValueAt(false, i, 8);
//							tablemodel.fireTableDataChanged();
						}
					}
					tablemodel.fireTableDataChanged();
					updateScoreText(tablemodel);
				}else if(e.getSource() == jbutton[3]){
					for( int i=0; i < tablemodel.vocabularies.size() ;i++){
						if( ((String) tablemodel.getValueAt( i, 4)).matches("O") ){
							Vocabulary voca = tablemodel.vocabularies.get(i);
							voca.setRightCount( voca.getRightCount() + 1 );
							tablemodel.updateVocabulary(voca);
						}else if( ((String) tablemodel.getValueAt( i, 4)).matches("X") ){
							Vocabulary voca = tablemodel.vocabularies.get(i);
							voca.setWrongCount( voca.getWrongCount() + 1 );
							tablemodel.updateVocabulary(voca);
						}
						if( ((String) tablemodel.getValueAt( i, 7)).matches("O") ){
							Vocabulary voca = tablemodel.vocabularies.get(i);
							voca.setRightCount( voca.getRightCount() + 1 );
							tablemodel.updateVocabulary(voca);
						}else if( ((String) tablemodel.getValueAt( i, 7)).matches("X") ){
							Vocabulary voca = tablemodel.vocabularies.get(i);
							voca.setWrongCount( voca.getWrongCount() + 1 );
							tablemodel.updateVocabulary(voca);
						}
					}
					Component button = (Component)e.getSource();
					Window window = SwingUtilities.windowForComponent(button);
					window.dispose();
				}else if(e.getSource() == jbutton[4]){
					for( int i=0; i < tablemodel.vocabularies.size() ;i++){
						if((Boolean)tablemodel.getValueAt( i, 8) == true){
							if( ! ( (String)tablemodel.getValueAt(i, tablemodel.findColumn("お書き")) ).matches("ibid.") ){
								tablemodel.setValueAt("X", i, 4);
							}
							if( ! ( (String)tablemodel.getValueAt(i, 6) ).matches("ibid.") ){
								tablemodel.setValueAt("X", i, 7);
							}
							tablemodel.setValueAt(false, i, 8);
//							tablemodel.fireTableDataChanged();
						}
					}
					tablemodel.fireTableDataChanged();
					updateScoreText(tablemodel);
				}else if( e.getSource() == jbutton[5] ){
					Component button = (Component)e.getSource();
					Window window = SwingUtilities.windowForComponent(button);
					window.dispose();
				}
			}
		}
		class VocaTestActionListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ( tableModelType) {
					case 2:{
						VocaTablePanel meaningAnswerPanel = new VocaTablePanel(5);
						VocaTableModel model = meaningAnswerPanel.tablemodel;
						model.setFlagtoeven();
						int rowIndex = 0;
						for (Vocabulary voca : meaningAnswerPanel.tablemodel.vocabularies) {
							Vector<String> vector = new Vector<String>();
							vector.add( model.setScore(voca.getRletter(), model.typedVocas.get(rowIndex).get(0) ) );
							vector.add( model.setScore(voca.getMeaning(), model.typedVocas.get(rowIndex).get(1) ) );   //setScore(vocabulary.getMeaning(), typedVoca.get(1)) 
							model.updateScoreBoxColumn(rowIndex, vector);
							rowIndex++;
						}
						model.fireTableDataChanged();
						int[] score = scoreTest(model);
						for(int i =0; i<score.length; i++){
							meaningAnswerPanel.statePanel.jlabel[ i*2+1 ].setText( String.valueOf( score[i] ) );
						}
						
						try {
							UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
						} catch (ClassNotFoundException
								| InstantiationException
								| IllegalAccessException
								| UnsupportedLookAndFeelException e1) {
							e1.printStackTrace();
						}

						JFrame jframe = new JFrame("単語の正解紙");
						jframe.add(meaningAnswerPanel);
						jframe.setSize( meaningAnswerPanel.tableWidth + VocaInterface.TABBED_WIDTH + cellwidth*1, tableHeight * 5 /4 );
						Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
						jframe.setLocation( (int) (screen.getWidth() - jframe.getWidth() ) / 2 , (int) (screen.getHeight() - jframe.getHeight() ) / 2 - 10 );
						jframe.setVisible(true);
						break;
					}
					case 3:{
						try {
							UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
						} catch (ClassNotFoundException
								| InstantiationException
								| IllegalAccessException
								| UnsupportedLookAndFeelException e1) {
							e1.printStackTrace();
						}
						VocaTablePanel wletterAnswerPanel = new VocaTablePanel(6);
						VocaTableModel model = wletterAnswerPanel.tablemodel;
						model.setFlagtoeven();
						int rowIndex = 0;
						for (Vocabulary voca : wletterAnswerPanel.tablemodel.vocabularies) {
							Vector<String> vector = new Vector<String>();
							vector.add( model.setScore(voca.getWletter(), model.typedVocas.get(rowIndex).get(0) ) );  // setScore(vocabulary.getWletter(), typedVoca.get(0))
							vector.add( model.setScore(voca.getRletter(), model.typedVocas.get(rowIndex).get(1) ) ); // setScore(vocabulary.getRletter(), typedVoca.get(1)) 
							model.updateScoreBoxColumn(rowIndex, vector);
							rowIndex++;
						}
						model.fireTableDataChanged();
						int[] score = scoreTest(model);
						for(int i =0; i<score.length; i++){
							wletterAnswerPanel.statePanel.jlabel[ i*2+1 ].setText( String.valueOf( score[i] ) );
						}
						
						JFrame jframe = new JFrame("単語の正解紙");
						jframe.add(wletterAnswerPanel);
						jframe.setSize( wletterAnswerPanel.tableWidth + VocaInterface.TABBED_WIDTH + cellwidth*1, tableHeight * 5 /4 );
						Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
						jframe.setLocation( (int) (screen.getWidth() - jframe.getWidth() ) / 2 , (int) (screen.getHeight() - jframe.getHeight() ) / 2 - 10 );
						jframe.setVisible(true);
						break;
					}
					default:
						break;
				}
			}
		}
		private int[] scoreTest(VocaTableModel vocaTablemodel){
			VocaTableModel tablemodel = vocaTablemodel;
			int[] score = new int[3];
				for( int i=0; i < tablemodel.vocabularies.size() ;i++){
					if( ((String) tablemodel.getValueAt( i, 4)).matches("O") ){
						score[0] ++;
					}else if( ((String) tablemodel.getValueAt( i, 4)).matches("X") ){
						score[1] ++;
					}else if( ((String) tablemodel.getValueAt( i, 4)).matches("保留") ){
						score[2] ++;
					}
					if( ((String) tablemodel.getValueAt( i, 7)).matches("O") ){
						score[0] ++;
					}else if( ((String) tablemodel.getValueAt( i, 7)).matches("X") ){
						score[1] ++;
					}else if( ((String) tablemodel.getValueAt( i, 7)).matches("保留") ){
						score[2] ++;
					}
				}
			return score;
		}
		private void updateScoreText(VocaTableModel vocaTablemodel){
			int[] score = scoreTest(vocaTablemodel);
			for(int i =0; i<score.length; i++){
				jlabel[ i*2+1 ].setText( String.valueOf( score[i] ) );
			}
		}
}
