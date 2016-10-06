package myProject;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainProgram extends JFrame {
	protected static JPanel mainpanel = null;
	private InitPanel initpanel = null;
	protected static CardLayout cl = null;
	protected static Vector<VocaTablePanel> observers = null;
	private String[] itemname = {"初期画面", "    --------------------------", "メモ帳に保存",
			"メモ帳からDBに保存(Safe)", "メモ帳からDBに保存(Speed)", "    --------------------------", "安全に終了"};
	private JMenuItem[] menuitem = new JMenuItem[itemname.length];
	private JMenuItem helpitem = null;

	public static void main(String args[]) {
		new MainProgram("日本語の単語帳");
	}

	public MainProgram(String string) {
		super(string);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) screen.getWidth() / 15 * 12 + 120,
				(int) screen.getHeight() / 15 * 14);
		int xpos = (int) (screen.getWidth() - super.getWidth()) / 2;
		int ypos = (int) (screen.getHeight() - super.getHeight()) / 5;
		setLocation(xpos, ypos);
		UIManager.put("OptionPane.messageFont", VocaInterface.Voca_FONT);
		UIManager.put("OptionPane.buttonFont", new Font("serif", 0, 15));
		UIManager.put("Button.font", new Font("serif", 0, 15));
		UIManager.put("Label.font", new Font("serif", 0, 15));
		UIManager.put("ComboBox.togglePopupText", new Font("serif", 0, 12) );
		UIManager.put("ComboBox.font", new Font("serif", 0, 12) );
		UIManager.put("MenuItem.font", VocaInterface.Voca_FONT);
		UIManager.put("Menu.font", VocaInterface.Voca_FONT);
		UIManager.put("TabbedPane.font", VocaInterface.Function_FONT);
		UIManager.put("Table.alternateRowColor", new Color(235, 235, 235 ) );
		UIManager.put("OptionPane.font",  VocaInterface.Function_FONT );
		
		
		compose();
		setevent();
		setVisible(true);
	}

	private void compose() {
		VocaTablePanel vocaPanel = null;
		VocaTablePanel testMeaningPanel = null;
		VocaTablePanel testWletterPanel = null;
		JMenuBar menubar = null;
		JMenu menu = null;
		JMenu helpmenu = null;

		Container con = super.getContentPane();
		con.setLayout(new BorderLayout());
		mainpanel = new JPanel();
		cl = new CardLayout();
		mainpanel.setLayout(cl);

		con.add(mainpanel, BorderLayout.CENTER);

		initpanel = new InitPanel();
		vocaPanel = new VocaTablePanel(1);
		testMeaningPanel = new VocaTablePanel(2);
		testWletterPanel = new VocaTablePanel(3);
		
		observers= new Vector<VocaTablePanel>();
		observers.add(vocaPanel);
		observers.add(testMeaningPanel);
		observers.add(testWletterPanel);
		
		mainpanel.add(initpanel);
		mainpanel.add(vocaPanel);
		mainpanel.add(testMeaningPanel);
		mainpanel.add(testWletterPanel);

		menubar = new JMenuBar();
		menu = new JMenu("メニュー");
		
		for (int i = 0; i < menuitem.length; i++) {
			menuitem[i] = new JMenuItem(itemname[i]);
			menu.add(menuitem[i]);
			if (i == 1 || i == 5)
				menuitem[i].setEnabled(false);
		}
		
		helpmenu = new JMenu("助言");
		helpitem = new JMenuItem("情報");
		helpmenu.add(helpitem);
		
		menubar.add(menu);
		menubar.add(helpmenu);

		super.setJMenuBar(menubar);
		super.setDefaultLookAndFeelDecorated(true);
		cl.first(mainpanel);

	}
	private void setevent() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuitem[0].addActionListener(new MyActionListener());
		menuitem[2].addActionListener(new MyActionListener());
		menuitem[3].addActionListener(new MyActionListener());
		menuitem[4].addActionListener(new MyActionListener());
		menuitem[6].addActionListener(new MyActionListener());
		helpitem.addActionListener(new MyActionListener());
	}
	static void updateObserver(int tableModelType){
		switch(tableModelType){
			case 1:
				MainProgram.observers.get(0).tablemodel.vocabularies = new VocabularyDAO().getVocabularies("select * from vocabularies");
				MainProgram.observers.get(0).tablemodel.fireTableDataChanged();
				break;
			case 2:
				MainProgram.observers.get(1).tablemodel.vocabularies = new VocabularyDAO().getVocasRandom();
				MainProgram.observers.get(1).tablemodel.typedVocas = MainProgram.observers.get(1).tablemodel.getTypedVocas();
				MainProgram.observers.get(1).tablemodel.fireTableDataChanged();
				VocaTableModel.updateTestVocas(5);
				break;
			case 3:
				MainProgram.observers.get(2).tablemodel.vocabularies = new VocabularyDAO().getVocasRandom();
				MainProgram.observers.get(2).tablemodel.typedVocas = MainProgram.observers.get(2).tablemodel.getTypedVocas();
				MainProgram.observers.get(2).tablemodel.fireTableDataChanged();
				VocaTableModel.updateTestVocas(6);
				break;
		}
	}
	
	class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if( e.getSource() == menuitem[0] ){
				initpanel.jlbl.setIcon(new ImageIcon( VocaInterface.images[new Random().nextInt(5)] ));
				cl.first(mainpanel);
			}else if( e.getSource() == menuitem[2] ){
				VocabularyDAO dao = new VocabularyDAO();
				if( dao.writeNote() == VocaInterface.SUCCESS_NoteWriter ){
					JOptionPane.showMessageDialog(null, "保存が成功されました",
							"メモ帳に保存", JOptionPane.INFORMATION_MESSAGE );
				}
				VocabularyDAO.setConnectionClose();
			}else if( e.getSource() == menuitem[4] ){
				VocabularyDAO dao = new VocabularyDAO();
				String reaction = fileChoice();
				if( reaction != null ){
					int[] i = dao.readNote( reaction );
					VocabularyDAO.setConnectionClose();
					JOptionPane.showMessageDialog(null, i.length+"個の単語がDBに保存されました。");	
				}
				
			}else if( e.getSource() == menuitem[3]){
				int reaction = JOptionPane.showConfirmDialog(null, "この機能は単語数によって少し時間がかかる可能性があります。"
						+ "引き続きしましょうか。保存に失敗した単語は他のメモ帳に保存されます。", "注意", JOptionPane.OK_CANCEL_OPTION);
				if(reaction ==0){
					VocabularyDAO dao = new VocabularyDAO();
					int i = dao.readNoteSafe( fileChoice(), 0);
					VocabularyDAO.setConnectionClose();
					if ( i == 1){
						JOptionPane.showMessageDialog(null, "単語がDBに保存されました。");
					}
				}else if(reaction == 2){
					JOptionPane.showMessageDialog(null, "取り消しました。");
				}
			}else if( e.getSource() == menuitem[6] ){
				VocabularyDAO.setConnectionClose();
				Window[] windows = java.awt.Window.getWindows();
				for (Window window : windows) {
					window.dispose();
				}
			}else if( e.getSource() == helpitem){
				JOptionPane.showMessageDialog(null, "テストモードにはESC Keyで入力できます。Tap Keyで次のColumnになおEnter Keyでは次の行に移れます。"
						+ "\n検索機能は順序によって結果が変更されます。単語の追加は読み仮名と表記が同じなら表記は入力しなくても大丈夫です。"
						+ "\nSpeed modeは重ねた単語がありますとエーラができますので、DBが空いている限り利用してください。Safe modeは構いなく利用できます。"
						+ "\n環境設定はVocaInterface.javaファイルで変更できます。"
						+ "\nご利用くださり、誠にありがとうございます。日本への就職クラスの四期のキム・ソンジンより。");
			}
		}
		public String fileChoice() {
			JFileChooser chooser = new JFileChooser(VocaInterface.pathname); // . 은 현재 작업 중인 폴더
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Only Text File in UTF-8 Code", "txt");
			chooser.setFileFilter(filter);
			int retVal = chooser.showOpenDialog(mainpanel);
			if (retVal == JFileChooser.APPROVE_OPTION ){
				String filename = chooser.getSelectedFile().getName();
				if(filename.toLowerCase().endsWith("txt")){
					return chooser.getSelectedFile().getAbsolutePath();
				}
			}
			else if(retVal == JFileChooser.CANCEL_OPTION){
				JOptionPane.showMessageDialog(null, "Cancel button's been pressed");
			}
			
			return null;
		}
	}
}