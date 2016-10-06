package myProject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InitPanel extends JPanel {
	private JButton jbtn[] = new JButton[3];
	private String[] str = {"単語帳", "意味のテスト", "表記のテスト"};
	private GridBagConstraints gbc = null;
	private JPanel[] jpanel = new JPanel[3];
	protected JLabel jlbl = null;
	
	public InitPanel() {
		compose();
		setevent();
	}
	private void compose(){
		super.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 200));;                 //setLayout(new FlowLayout(FlowLayout., 50, 50));
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 50));
		
		jlbl = new JLabel();
		jlbl.setIcon(  new ImageIcon( VocaInterface.images[new Random().nextInt(5)] )   );
		center.add(jlbl);
		super.add(center, BorderLayout.CENTER);
		
		for (int i = 0; i < jbtn.length; i++) {
			jbtn[i] = new JButton(str[i]);
			jbtn[i].setFont(new Font("serif", Font.BOLD, 25));
			jbtn[i].setPreferredSize(new Dimension(VocaInterface.COMPONENT_SIZE*15, VocaInterface.COMPONENT_SIZE*6) );
			south.add(jbtn[i]);
		}
		super.add(south, BorderLayout.SOUTH);
	}
	private void setevent() {
		for (int i = 0; i < jbtn.length; i++) {
			jbtn[i].addActionListener(new mybtnAction());	
		}
	}
	class mybtnAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if ( e.getSource() == jbtn[0] ){
				MainProgram.updateObserver(1);
				MainProgram.cl.first(MainProgram.mainpanel);
				for(int i=0; i<1 ; i++)
					MainProgram.cl.next(MainProgram.mainpanel);
			}else if( e.getSource() == jbtn[1]){
				MainProgram.updateObserver(2);
				MainProgram.cl.first(MainProgram.mainpanel);
				for(int i=0; i<2 ; i++){
					MainProgram.cl.next(MainProgram.mainpanel);
				}
			}else if( e.getSource() == jbtn[2]){
				MainProgram.updateObserver(3);
				MainProgram.cl.first(MainProgram.mainpanel);
				for(int i=0; i<3 ; i++)
					MainProgram.cl.next(MainProgram.mainpanel);
			}
		}
	}
}