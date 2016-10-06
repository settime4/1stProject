package myProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import myProject.Vocabulary.WordClass;

public class VocaTablePanel extends JPanel {
	protected int tableModelType = 0;
	protected int tableWidth = 0;
	protected JTable table = null;
	protected VocaTableModel tablemodel = null;
	private JPanel defaultPanel = null;
	private JScrollPane jspane = null;
	protected VocaStatePanel statePanel =null;
	private final int tableHeight = VocaInterface.TABLE_HEIGHT;
	private final int cellwidth = VocaInterface.CELL_WIDTH;
	private final int cellheight = VocaInterface.CELL_HEIGHT;
	
	public VocaTablePanel(int tableModelType) {
		this.tableModelType = tableModelType;

		this.defaultPanel = new JPanel();
		this.defaultPanel.setLayout(new BorderLayout());
		
		TablePanel tablepanel = new TablePanel();
		this.defaultPanel.add(tablepanel, BorderLayout.CENTER);
		this.columnSizeRenderer();
		
		if( tableModelType == 2 || tableModelType == 3 || tableModelType == 5 || tableModelType ==6 ){
			statePanel = new VocaStatePanel( this.tablemodel, tableModelType );
			this.defaultPanel.add(statePanel, BorderLayout.SOUTH);
		}
		
		if( tableModelType == 1 ||  tableModelType == 5 || tableModelType == 6){
			VocaTabPanel tabPanel = new VocaTabPanel( this.tablemodel, tableModelType );
			this.defaultPanel.add(tabPanel, BorderLayout.EAST);	
		}
		
		super.add(defaultPanel);
		this.setevent();
	}

	private void setevent() {
	}

	class TablePanel extends JPanel {
		public TablePanel() {
			tablemodel = new VocaTableModel(tableModelType);
			table = new JTable(tablemodel);
			
			if(tableModelType == 1 ){
				JComboBox<String> wordclass_comboBox = new JComboBox<String>( WordClass.getNames() );
				
				TableColumn tbcolumn_lvoca = table.getColumnModel().getColumn(3);
				TableColumn tbcolumn_rvoca = table.getColumnModel().getColumn(8);
				
				DefaultCellEditor wordclass_cellEditor = new DefaultCellEditor(wordclass_comboBox);
				
				tbcolumn_lvoca.setCellEditor(wordclass_cellEditor);
				tbcolumn_rvoca.setCellEditor(wordclass_cellEditor);
				table.setDefaultRenderer(table.getColumnClass(0), new VocaTableCellRenderer());
				table.repaint();
			}
			else if(tableModelType == 4){
				JComboBox<String> wordclass_comboBox = new JComboBox<String>( WordClass.getNames() );
				TableColumn tbcolumn = table.getColumnModel().getColumn(3);
				
				DefaultCellEditor wordclass_cellEditor = new DefaultCellEditor(wordclass_comboBox);
				
				tbcolumn.setCellEditor(wordclass_cellEditor);
				table.setDefaultRenderer(table.getColumnClass(0), new VocaTableCellRenderer());
				table.repaint();
			}
			else if(tableModelType == 5 || tableModelType ==6){
				String[] score_list = {"保留","O","X"};
				JComboBox<String> score_comboBox = new JComboBox<String>(score_list);
				
				TableColumn tbcolumn_leftscore = table.getColumnModel().getColumn(4);
				TableColumn tbcolumn_rightscore = table.getColumnModel().getColumn(7);
				DefaultCellEditor score_cellEditor = new DefaultCellEditor(score_comboBox);
				
				tbcolumn_leftscore.setCellEditor(score_cellEditor);
				tbcolumn_rightscore.setCellEditor(score_cellEditor);
				table.setDefaultRenderer(table.getColumnClass(0), new VocaTableCellRenderer());
				table.repaint();
			}
				
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowHeight(cellheight);
			//table.setModel();
			jspane = new JScrollPane(table);
			this.add(jspane);
			table.addMouseListener(new TableMouseAdapter());
		}
	}
	
	private void columnSizeRenderer(){
		switch (tableModelType) {
			case 1 : {
				int columnWidth[] = {cellwidth * 1, cellwidth * 3, cellwidth * 3, cellwidth * 2,
						cellwidth * 5, cellwidth * 1, cellwidth * 3, cellwidth * 3, cellwidth * 2,
						cellwidth * 5};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, (int)tableHeight * 15 / 13));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
			case 2 : {
				int columnWidth[] = {cellwidth * 3, cellwidth * 3, cellwidth * 5,
						cellwidth * 3, cellwidth * 3, cellwidth * 5};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, tableHeight));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
			case 3 : {
				int columnWidth[] = {cellwidth * 5, cellwidth * 3, cellwidth * 3,
						cellwidth * 5, cellwidth * 3, cellwidth * 3};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, tableHeight));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
			case 4 : {
				int columnWidth[] = {cellwidth * 1, cellwidth * 3, cellwidth * 3, cellwidth * 2,
						cellwidth * 5, cellwidth * 2, cellwidth * 2, cellwidth * 3/2, cellwidth * 3/2,
						cellwidth * 3/2};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, tableHeight));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
			case 5 : {
				int columnWidth[] = {cellwidth * 1, cellwidth * 3, cellwidth * 3, cellwidth * 3,
						cellwidth * 5 / 4 , cellwidth * 5, cellwidth * 5, cellwidth * 5 / 4,
						cellwidth * 1};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, tableHeight));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
			case 6 : {
				int columnWidth[] = {cellwidth * 1, cellwidth * 5, cellwidth * 3, cellwidth * 3,
						cellwidth * 5 / 4 , cellwidth * 3, cellwidth * 3, cellwidth * 5 /4 ,
						cellwidth * 1};
				tableWidth = calculateTableWidth(columnWidth);
				this.jspane.setPreferredSize(new Dimension(tableWidth, tableHeight));

				int i = 0;
				for (int width : columnWidth) {
					TableColumn column = this.table.getColumnModel().getColumn(
							i++);
					column.setMaxWidth(width);
					column.setMinWidth(width);
					column.setPreferredWidth(width);
				}
				break;
			}
		}
	}

	private int calculateTableWidth(int[] columnWidth) {
		int total = 0;
		for (int i : columnWidth) {
			total += i;
		}
		if(tableModelType == 2 || tableModelType ==3 ){
			total -= 15;
		}
		return total + 15;
	}
	
	class VocaTableCellRenderer extends DefaultTableCellRenderer{
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, 	int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			setHorizontalAlignment(SwingConstants.LEFT);
			if ( tableModelType == 5 || tableModelType == 6){
				if( ! isSelected ){
					setBackground( new Color(255,255,255));	
				}
				
				if( table.getColumnName(column).matches("採点") ){
					setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					setHorizontalAlignment(SwingConstants.CENTER);
					if( ((String)value).matches("O") ){
						setBackground( new Color(115,164,209));
					}else if( ((String)value).matches("X")  ){
						setBackground( new Color(205,114,115));
					}
				}
			}
			return this;
		}
	}
	
	class TableMouseAdapter extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			int btn = e.getButton();
			if (btn == MouseEvent.BUTTON3) {

				if (tableModelType == 4 || tableModelType == 5 || tableModelType == 6) {
					int reaction = JOptionPane.showConfirmDialog(null,
							"選択した行を削除しますか。", "削除の確認",
							JOptionPane.OK_CANCEL_OPTION);
					if (reaction == 0) {
						int row = table.getSelectedRow();
						if (row == -1) {
							return;
						}
						Vocabulary voca = tablemodel.vocabularies.get(row);
						if( tableModelType == 5 || tableModelType == 6 ){
							tablemodel.checkedBoxColumn.remove(row);
							tablemodel.scoreBoxColumn.remove(row);
							tablemodel.typedVocas.remove(row);	
						}
						//tablemodel.setFlagtoeven();
						tablemodel.deleteVocabulary(voca);
						tablemodel.vocaCounts.remove(row);

						MainProgram.observers.get(0).tablemodel.fireTableDataChanged();
						
					} else if (reaction == 2) {
						JOptionPane.showMessageDialog(null, "削除を取り消しました。");
					}
				}else if(tableModelType == 1 ){
					String[] choices = {"取り消し","左の単語を削除", "右の単語を削除"};
					int reaction = JOptionPane.showOptionDialog(null, "選択した行の一つの単語を削除しますか。", "削除の確認", JOptionPane.DEFAULT_OPTION, 
							JOptionPane.QUESTION_MESSAGE , null, choices, "取り消し" );
					if(reaction == 0 || reaction == -1) { //取り消し and dispose button
						JOptionPane.showMessageDialog(null, "削除を取り消しました。");
					}else if(reaction == 1 ){ // left voca
						int row = table.getSelectedRow();
						if (row == -1) {
							return;
						}
						String wletter = null;
						String meaning = null;

						wletter = (String) table.getValueAt(row , 1);
						meaning = (String) tablemodel.getValueAt(row , 4);
						
						tablemodel.deleteVocabulary(wletter, meaning);
						
					} else if (reaction == 2) { //  right voca
						int row = table.getSelectedRow();
						if (row == -1) {
							return;
						}
						String wletter = null;
						String meaning = null;

						wletter = (String) table.getValueAt(row , 6);
						meaning = (String) tablemodel.getValueAt(row , 9);

						tablemodel.deleteVocabulary(wletter, meaning);
					}
				}
			}
		}
	}
}