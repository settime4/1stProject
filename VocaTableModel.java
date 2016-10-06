package myProject;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import myProject.Vocabulary.WordClass;

public class VocaTableModel extends AbstractTableModel{
	protected VocabularyDAO dao = null;
	private Vector<String> columnNames = null;
	protected Vector<Vocabulary> vocabularies = null;
	protected Vector<Integer> vocaCounts = null;
	protected Vector<Vector<String>> typedVocas = null;
	static Vector<Vocabulary> test1_vocabularies = new Vector<Vocabulary>();
	static Vector<Vocabulary> test2_vocabularies = new Vector<Vocabulary>();
	static Vector<Vector<String>> test1_typedVocas = null;
	static Vector<Vector<String>> test2_typedVocas = null;
	protected Vector<Boolean> checkedBoxColumn = null;
	protected Vector<Vector<String>> scoreBoxColumn = null;
	private int tableModelType = 0;
	private boolean flagtoeven = false;

	public VocaTableModel(int tableModelType) {
		this.tableModelType = tableModelType;
		this.dao = new VocabularyDAO();
		this.columnNames = dao.getColumnNames(tableModelType);

		switch (tableModelType) {
			case 1 :
				this.vocabularies = dao
						.getVocabularies("select * from vocabularies");
				this.vocaCounts = getVocaCount();
				break;

			case 2 :
				this.vocabularies = dao.getVocasRandom();
				this.typedVocas = this.getTypedVocas();

				VocaTableModel.test1_vocabularies.addAll(vocabularies);
				VocaTableModel.test1_typedVocas = this.typedVocas;
				break;

			case 3 :
				this.vocabularies = dao.getVocasRandom();
				this.typedVocas = this.getTypedVocas();

				VocaTableModel.test2_vocabularies.addAll(vocabularies);
				VocaTableModel.test2_typedVocas = this.typedVocas;
				break;

			case 4 :
				break;

			case 5 :
				this.vocabularies = VocaTableModel.test1_vocabularies;
				this.typedVocas = VocaTableModel.test1_typedVocas;
				this.scoreBoxColumn = getScoreBoxColumn();
				this.checkedBoxColumn = getCheckedBoxColumn();
				this.vocaCounts = getVocaCount();
				break;

			case 6 :
				this.vocabularies = VocaTableModel.test2_vocabularies;
				this.typedVocas = VocaTableModel.test2_typedVocas;
				this.scoreBoxColumn = getScoreBoxColumn();
				this.checkedBoxColumn = getCheckedBoxColumn();
				this.vocaCounts = getVocaCount();
				break;

		}
	}
	public int insertVocabulary(Vocabulary vocabulary) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		cnt = this.dao.insertVocabulary(vocabulary);
		if (cnt == -1) {
			String message = "The word which has typed already is in DB";
			JOptionPane.showMessageDialog(null, message);
			cnt = VocaInterface.ERROR_DEFAULT;
		} else if (cnt != VocaInterface.ERROR_DEFAULT) {
			if (tableModelType == 1) {
				this.vocabularies.clear();
				this.vocabularies = dao
						.getVocabularies("select * from vocabularies");
			} else if (tableModelType == 4 || tableModelType == 5
					|| tableModelType == 6) {
				MainProgram.updateObserver(1);
			}
			super.fireTableRowsInserted(0, this.getRowCount());
		}
		return cnt;
	}

	public int updateVocabulary(Vocabulary vocabulary) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		cnt = this.dao.updateVocabulary(vocabulary);
		if (cnt == -1) {
			String message = "Error has occured, on updateVocabulary in TableModel";
			JOptionPane.showMessageDialog(null, message);
			cnt = VocaInterface.ERROR_DEFAULT;
		} else if (cnt != VocaInterface.ERROR_DEFAULT) {
			if (tableModelType == 1) {
				this.vocabularies.clear();
				this.vocabularies = dao
						.getVocabularies("select * from vocabularies");
			} else if (tableModelType == 4 || tableModelType == 5
					|| tableModelType == 6) {
				MainProgram.updateObserver(1);
			}
		}
		super.fireTableCellUpdated(0, this.getRowCount());
		return cnt;
	}
	public int updateVocabulary(Vocabulary vocabulary, String ex_Wletter,
			String ex_Meaning) {
		int cnt = VocaInterface.ERROR_DEFAULT;
		cnt = this.dao.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
		if (cnt == -1) {
			String message = "Error has occured, on updateVocabulary in TableModel";
			JOptionPane.showMessageDialog(null, message);
			cnt = VocaInterface.ERROR_DEFAULT;
		} else if (cnt != VocaInterface.ERROR_DEFAULT) {
			if (tableModelType == 1) {
				this.vocabularies.clear();
				this.vocabularies = dao
						.getVocabularies("select * from vocabularies");
			} else if (tableModelType == 4 || tableModelType == 5
					|| tableModelType == 6) {
				MainProgram.updateObserver(1);
			}
		}
		super.fireTableCellUpdated(0, this.getRowCount());
		return cnt;
	}
	public int deleteVocabulary(String wletter, String meaning) { // for
																	// tableModel
																	// of 1
		int cnt = VocaInterface.ERROR_DEFAULT;

		cnt = this.dao.deleteVocabulary(wletter, meaning);

		if (cnt == -1) {
			String message = "Error has occured, on deleteVocabulary Method in TableModel";
			JOptionPane.showMessageDialog(null, message);
			cnt = VocaInterface.ERROR_DEFAULT;
		} else if (cnt != VocaInterface.ERROR_DEFAULT) {
			if (tableModelType == 1) {
				this.vocabularies.clear();
				this.vocabularies = dao
						.getVocabularies("select * from vocabularies");
				this.vocaCounts.clear();
				this.vocaCounts = getVocaCount();
			}
			super.fireTableRowsDeleted(0, this.getRowCount());
		}
		return cnt;
	}
	public int deleteVocabulary(Vocabulary vocabulary) { // for tableModel of 4,
															// 5, 6
		int cnt = VocaInterface.ERROR_DEFAULT;

		cnt = this.dao.deleteVocabulary(vocabulary.getWletter(),
				vocabulary.getMeaning());
		int row = vocabularies.indexOf(vocabulary);
		if (cnt == -1) {
			String message = "Error has occured, on deleteVocabularyMethod in TableModel";
			JOptionPane.showMessageDialog(null, message);
			cnt = VocaInterface.ERROR_DEFAULT;
		} else if (cnt != VocaInterface.ERROR_DEFAULT) {
			switch (tableModelType) {
				case 4 :
					vocabularies.remove(vocabulary);
					break;
				case 5 :
					vocabularies.remove(vocabulary);
					break;
				case 6 :
					vocabularies.remove(vocabulary);
					break;
			}
			super.fireTableRowsDeleted(0, this.getRowCount());
		}
		return cnt;
	}
	@Override
	public int getColumnCount() {
		return columnNames.size();
	}
	@Override
	public int getRowCount() {
		switch (tableModelType) {
			case 1 :
				return (this.vocabularies.size() % 2 == 0) ? this.vocabularies
						.size() / 2 : (this.vocabularies.size() / 2 + 1);
			case 2 :
				return (this.vocabularies.size() % 2 == 0) ? this.vocabularies
						.size() / 2 : (this.vocabularies.size() / 2 + 1);
			case 3 :
				return (this.vocabularies.size() % 2 == 0) ? this.vocabularies
						.size() / 2 : (this.vocabularies.size() / 2 + 1);
			case 4 :
				return this.vocabularies.size();
			case 5 :
				return this.vocabularies.size();
			case 6 :
				return this.vocabularies.size();
		}
		return 0;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (tableModelType == 1) {

			if (this.vocabularies.size() % 2 == 1) {
				this.vocabularies.add(new Vocabulary());
				this.vocaCounts.add(vocaCounts.size() + 1);
				this.flagtoeven = true;
			}

			Vocabulary[] vocabulary = new Vocabulary[2];

			vocabulary[0] = this.vocabularies.get(rowIndex * 2); // left voca =
																	// even 0 2
																	// 4 6
			vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);// right
																	// voca =
																	// odd 1 3 5

			if (vocabulary[1] != null) {
				switch (columnIndex) {
					case 0 :
						return vocaCounts.get(rowIndex * 2);
					case 1 :
						return (vocabulary[0].getWletter() == null)
								? vocabulary[0].getRletter()
								: vocabulary[0].getWletter();
					case 2 :
						return vocabulary[0].getRletter();
					case 3 :
						return vocabulary[0].getWordClass().getName();
					case 4 :
						return vocabulary[0].getMeaning();
					case 5 :
						return vocaCounts.get(rowIndex * 2 + 1);
					case 6 :
						return vocabulary[1].getWletter();
					case 7 :
						return vocabulary[1].getRletter();
					case 8 :
						return vocabulary[1].getWordClass().getName();
					case 9 :
						return vocabulary[1].getMeaning();
				}
			}
		} else if (tableModelType == 2) {

			if (this.vocabularies.size() % 2 == 1) {
				this.vocabularies.add(new Vocabulary());
				this.typedVocas.add(new Vector<String>());
			}

			Vocabulary[] vocabulary = new Vocabulary[2];
			vocabulary[0] = this.vocabularies.get(rowIndex * 2);
			vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);

			Vector<String>[] typedVoca = new Vector[2];
			typedVoca[0] = this.typedVocas.get(rowIndex * 2);
			typedVoca[1] = this.typedVocas.get(rowIndex * 2 + 1);
			switch (columnIndex) {
				case 0 :
					return (vocabulary[0].getWletter() == null) ? vocabulary[0]
							.getRletter() : vocabulary[0].getWletter();
				case 1 :
					if (vocabulary[0].getRletter().matches(
							vocabulary[0].getWletter())) {
						typedVoca[0].set(0, "ibid.");
					}
					return typedVoca[0].get(0);
				case 2 :
					return typedVoca[0].get(1);
				case 3 :
					return (vocabulary[1].getWletter() == null) ? vocabulary[1]
							.getRletter() : vocabulary[1].getWletter();
				case 4 :
					if (vocabulary[1].getRletter().matches(
							vocabulary[1].getWletter())) {
						typedVoca[1].set(0, "ibid.");
					}
					return typedVoca[1].get(0);
				case 5 :
					return typedVoca[1].get(1);
			}
		} else if (tableModelType == 3) {

			if (this.vocabularies.size() % 2 == 1) {
				this.vocabularies.add(new Vocabulary());
				this.typedVocas.add(new Vector<String>());
			}

			Vocabulary[] vocabulary = new Vocabulary[2];
			vocabulary[0] = this.vocabularies.get(rowIndex * 2);
			vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);

			Vector<String>[] typedVoca = new Vector[2];
			typedVoca[0] = this.typedVocas.get(rowIndex * 2);
			typedVoca[1] = this.typedVocas.get(rowIndex * 2 + 1);
			switch (columnIndex) {
				case 0 :
					return vocabulary[0].getMeaning();
				case 1 :
					return typedVoca[0].get(0);
				case 2 :
					if (vocabulary[0].getRletter().matches(
							vocabulary[0].getWletter())) {
						typedVoca[0].set(1, "ibid.");
					}
					return typedVoca[0].get(1);
				case 3 :
					return vocabulary[1].getMeaning();
				case 4 :
					return typedVoca[1].get(0);
				case 5 :
					if (vocabulary[1].getRletter().matches(
							vocabulary[1].getWletter())) {
						typedVoca[1].set(1, "ibid.");
					}
					return typedVoca[1].get(1);
			}
		} else if (tableModelType == 4) {
			Vocabulary vocabulary = this.vocabularies.get(rowIndex);

			switch (columnIndex) {
				case 0 :
					return vocaCounts.get(rowIndex);
				case 1 :
					return (vocabulary.getWletter() == null) ? vocabulary
							.getRletter() : vocabulary.getWletter();
				case 2 :
					return vocabulary.getRletter();
				case 3 :
					return vocabulary.getWordClass().getName();
				case 4 :
					return vocabulary.getMeaning();
				case 5 :
					return vocabulary.getRegdate();
				case 6 :
					return vocabulary.getMdate();
				case 7 :
					return vocabulary.getSearchCount();
				case 8 :
					return vocabulary.getRightCount();
				case 9 :
					return vocabulary.getWrongCount();
			}
		} else if (tableModelType == 5) {
			Vocabulary vocabulary = vocabularies.get(rowIndex);
			switch (columnIndex) {
				case 0 :
					return vocaCounts.get(rowIndex);
				case 1 :
					return (vocabulary.getWletter() == null) ? vocabulary
							.getRletter() : vocabulary.getWletter();
				case 2 :
					return vocabulary.getRletter();
				case 3 :
					return typedVocas.get(rowIndex).get(0);
				case 4 :
					return scoreBoxColumn.get(rowIndex).get(0);
				case 5 :
					return vocabulary.getMeaning();
				case 6 :
					return typedVocas.get(rowIndex).get(1);
				case 7 :
					return scoreBoxColumn.get(rowIndex).get(1);
				case 8 :
					return checkedBoxColumn.get(rowIndex);
			}
		} else if (tableModelType == 6) {
			Vocabulary vocabulary = vocabularies.get(rowIndex);
			switch (columnIndex) {
				case 0 :
					return vocaCounts.get(rowIndex);
				case 1 :
					return vocabulary.getMeaning();
				case 2 :
					return (vocabulary.getWletter() == null) ? vocabulary
							.getRletter() : vocabulary.getWletter();
				case 3 :
					return typedVocas.get(rowIndex).get(0);
				case 4 :
					return scoreBoxColumn.get(rowIndex).get(0);
				case 5 :
					return vocabulary.getRletter();
				case 6 :
					return typedVocas.get(rowIndex).get(1);
				case 7 :
					return scoreBoxColumn.get(rowIndex).get(1);
				case 8 :
					return checkedBoxColumn.get(rowIndex);
			}
		}

		return null;
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (tableModelType == 1) {
			Vocabulary[] vocabulary = new Vocabulary[2];
			vocabulary[0] = this.vocabularies.get(rowIndex * 2);
			vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);
			switch (columnIndex) {
				case 1 : {
					String ex_Wletter = vocabulary[0].getWletter();
					String ex_Meaning = vocabulary[0].getMeaning();
					vocabulary[0].setWletter(aValue.toString());
					this.updateVocabulary(vocabulary[0], ex_Wletter, ex_Meaning);
					break;
				}
				case 2 :
					vocabulary[0].setRletter(aValue.toString());
					this.updateVocabulary(vocabulary[0]);
					break;
				case 3 :
					vocabulary[0].setWordClass(WordClass
							.convertEnum((String) aValue));
					this.updateVocabulary(vocabulary[0]);
					break;
				case 4 : {
					String ex_Wletter = vocabulary[0].getWletter();
					String ex_Meaning = vocabulary[0].getMeaning();
					vocabulary[0].setMeaning(aValue.toString());
					this.updateVocabulary(vocabulary[0], ex_Wletter, ex_Meaning);
					break;
				}
				case 6 : {
					String ex_Wletter = vocabulary[1].getWletter();
					String ex_Meaning = vocabulary[1].getMeaning();
					vocabulary[1].setWletter(aValue.toString());
					this.updateVocabulary(vocabulary[1], ex_Wletter, ex_Meaning);
					break;
				}
				case 7 : {
					vocabulary[1].setRletter(aValue.toString());
					this.updateVocabulary(vocabulary[1]);
					break;
				}
				case 8 :
					vocabulary[1].setWordClass(WordClass
							.convertEnum((String) aValue));
					this.updateVocabulary(vocabulary[1]);
					break;
				case 9 : {
					String ex_Wletter = vocabulary[1].getWletter();
					String ex_Meaning = vocabulary[1].getMeaning();
					vocabulary[1].setMeaning(aValue.toString());
					this.updateVocabulary(vocabulary[1], ex_Wletter, ex_Meaning);
					break;
				}
			}

		} else if (tableModelType == 2) {
			Vector<String>[] typedvoca = new Vector[2];
			typedvoca[0] = typedVocas.get(rowIndex * 2);
			typedvoca[1] = typedVocas.get(rowIndex * 2 + 1);
			switch (columnIndex) {
				case 0 :
					break;
				case 1 :
					typedvoca[0].set(0, (String) aValue);
					break;
				case 2 :
					typedvoca[0].set(1, (String) aValue);
					break;
				case 3 :
					break;
				case 4 :
					typedvoca[1].set(0, (String) aValue);
					break;
				case 5 :
					typedvoca[1].set(1, (String) aValue);
					break;
			}

		} else if (tableModelType == 3) {
			Vector<String>[] typedvoca = new Vector[2];
			typedvoca[0] = typedVocas.get(rowIndex * 2);
			typedvoca[1] = typedVocas.get(rowIndex * 2 + 1);
			switch (columnIndex) {
				case 0 :
					break;
				case 1 :
					typedvoca[0].set(0, (String) aValue);
					break;
				case 2 :
					typedvoca[0].set(1, (String) aValue);
					break;
				case 3 :
					break;
				case 4 :
					typedvoca[1].set(0, (String) aValue);
					break;
				case 5 :
					typedvoca[1].set(1, (String) aValue);
					break;
			}
		} else if (tableModelType == 4) {
			Vocabulary vocabulary = this.vocabularies.get(rowIndex);
			switch (columnIndex) {
				case 1 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setWletter(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}
				case 2 : {
					vocabulary.setRletter(aValue.toString());
					this.updateVocabulary(vocabulary);
					break;
				}
				case 3 :
					vocabulary.setWordClass(WordClass
							.convertEnum((String) aValue));
					this.updateVocabulary(vocabulary);
					break;
				case 4 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setMeaning(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}
				case 5 :
					break;
				case 6 :
					break;
				case 7 :
					break;
				case 8 :
					break;
			}
		} else if (tableModelType == 5) {
			Vocabulary vocabulary = this.vocabularies.get(rowIndex);
			Vector<String> typedvoca = typedVocas.get(rowIndex);
			Vector<String> scorevoca = scoreBoxColumn.get(rowIndex);
			switch (columnIndex) {
				case 1 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setWletter(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}
				case 2 : {
					vocabulary.setRletter(aValue.toString());
					this.updateVocabulary(vocabulary);
					break;
				}
				case 3 :
					typedvoca.set(0, (String) aValue);
					break;
				case 4 :
					scorevoca.set(0, (String) aValue);
					break;
				case 5 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setMeaning(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}
				case 6 :
					typedvoca.set(1, (String) aValue);
					break;
				case 7 :
					scorevoca.set(1, (String) aValue);
					break;
				case 8 :
					if (aValue instanceof Boolean) {
						updateCheckedBoxColumn(rowIndex, (boolean) aValue);
					}
					break;
			}
		} else if (tableModelType == 6) {
			Vocabulary vocabulary = this.vocabularies.get(rowIndex);
			Vector<String> typedvoca = typedVocas.get(rowIndex);
			Vector<String> scorevoca = scoreBoxColumn.get(rowIndex);
			switch (columnIndex) {
				case 1 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setMeaning(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}
				case 2 : {
					String ex_Wletter = vocabulary.getWletter();
					String ex_Meaning = vocabulary.getMeaning();
					vocabulary.setWletter(aValue.toString());
					this.updateVocabulary(vocabulary, ex_Wletter, ex_Meaning);
					break;
				}

				case 3 :
					typedvoca.set(0, (String) aValue);
					break;
				case 4 :
					scorevoca.set(0, (String) aValue);
					break;
				case 5 : {
					vocabulary.setRletter(aValue.toString());
					this.updateVocabulary(vocabulary);
					break;
				}
				case 6 :
					typedvoca.set(1, (String) aValue);
					break;
				case 7 :
					scorevoca.set(1, (String) aValue);
				case 8 :
					if (aValue instanceof Boolean)
						updateCheckedBoxColumn(rowIndex, (boolean) aValue);
					break;
			}
		}
		super.fireTableCellUpdated(rowIndex, columnIndex);
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (tableModelType) {
			case 1 :
				if (columnIndex == 0 || columnIndex == 5) {
					return false;
				}
				return true;
			case 2 : {
				Vocabulary[] vocabulary = new Vocabulary[2];
				vocabulary[0] = this.vocabularies.get(rowIndex * 2);
				vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);

				if (vocabulary[0].getRletter().matches(
						vocabulary[0].getWletter())) {
					if (columnIndex == 1) {
						return false;
					}
				}
				if (vocabulary[1].getRletter().matches(
						vocabulary[1].getWletter())) {
					if (columnIndex == 4) {
						return false;
					}
				}
				return (columnIndex == 0 || columnIndex == 3) ? false : true;
			}
			case 3 : {
				Vocabulary[] vocabulary = new Vocabulary[2];
				vocabulary[0] = this.vocabularies.get(rowIndex * 2);
				vocabulary[1] = this.vocabularies.get(rowIndex * 2 + 1);

				if (vocabulary[0].getRletter().matches(
						vocabulary[0].getWletter())) {
					if (columnIndex == 2) {
						return false;
					}
				}
				if (vocabulary[1].getRletter().matches(
						vocabulary[1].getWletter())) {
					if (columnIndex == 5) {
						return false;
					}
				}
				return (columnIndex == 0 || columnIndex == 3) ? false : true;
			}
			case 4 :
				return (columnIndex >= 4 && columnIndex <= 7 || columnIndex == 0)
						? false
						: true;
			case 5 :
				if (columnIndex == 0) {
					return false;
				}
				if (typedVocas.get(rowIndex).get(0).matches("ibid.")) {
					if (columnIndex == 2 || columnIndex == 3) {
						return false;
					}
				}
				return true;
			case 6 :
				if (columnIndex == 0) {
					return false;
				}
				if (typedVocas.get(rowIndex).get(1).matches("ibid.")) {
					if (columnIndex == 5 || columnIndex == 6) {
						return false;
					}
				}
				return true;
		}
		return false;
	}
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class columnType = String.class;
		switch (tableModelType) {
			case 1 :
				if (columnIndex == 3 || columnIndex == 8)
					columnType = WordClass.class;
				break;
			case 4 :
				if (columnIndex == 3)
					columnType = WordClass.class;
				break;
			case 5 :
				if (columnIndex == 8)
					columnType = Boolean.class;
				break;
			case 6 :
				if (columnIndex == 8)
					columnType = Boolean.class;
				break;
		}
		return columnType;
	}

	public Vector<Vector<String>> getTypedVocas() {
		typedVocas = new Vector<Vector<String>>();
		for (int i = 0; i < vocabularies.size(); i++) {
			Vector<String> vc = new Vector<String>();
			vc.add("");
			vc.add("");
			typedVocas.add(vc);
		}
		return typedVocas;
	}
	public Vector<Vector<String>> getScoreBoxColumn() {
		scoreBoxColumn = new Vector<Vector<String>>();
		for (int i = 0; i < vocabularies.size(); i++) {
			Vector<String> vc = new Vector<String>();
			vc.add("保留");
			vc.add("保留");
			scoreBoxColumn.add(vc);
		}
		return scoreBoxColumn;
	}
	public void updateScoreBoxColumn(int rowIndex, Vector<String> vector) {
		scoreBoxColumn.set(rowIndex, vector);
	}
	public Vector<Boolean> getCheckedBoxColumn() {
		checkedBoxColumn = new Vector<Boolean>();
		for (int i = 0; i < vocabularies.size(); i++) {
			checkedBoxColumn.add(false);
		}
		return checkedBoxColumn;
	}
	public void updateCheckedBoxColumn(int rowIndex, Boolean check) {
		checkedBoxColumn.set(rowIndex, check);
	}
	public Vector<String> checkScore(String str1, String str2) {
		Vector<String> check = new Vector<String>();
		return check;
	}
	public void setFlagtoeven() {
		if (this.flagtoeven = true && this.vocabularies.size() % 2 == 0) {
			this.vocabularies.remove(vocabularies.size() - 1);
			if (tableModelType == 2 || tableModelType == 3) {
				this.typedVocas.remove(typedVocas.size() - 1);
			}
			if (tableModelType == 1 || tableModelType == 4
					|| tableModelType == 5 || tableModelType == 6) {
				this.vocaCounts.remove(vocaCounts.size() - 1);
			}
			this.flagtoeven = false;
		}
	}
	public boolean getFlagtoeven() {
		return this.flagtoeven;
	}
	static void updateTestVocas(int tablemodelType) {
		switch (tablemodelType) {
			case 5 :
				VocaTableModel.test1_vocabularies.clear();
				VocaTableModel.test1_vocabularies = MainProgram.observers
						.get(1).tablemodel.vocabularies;
				VocaTableModel.test1_typedVocas.clear();
				VocaTableModel.test1_typedVocas = MainProgram.observers.get(1).tablemodel.typedVocas;
				break;
			case 6 :
				VocaTableModel.test2_vocabularies.clear();
				VocaTableModel.test2_vocabularies = MainProgram.observers
						.get(2).tablemodel.vocabularies;
				VocaTableModel.test2_typedVocas.clear();
				VocaTableModel.test2_typedVocas = MainProgram.observers.get(2).tablemodel.typedVocas;
				break;
		}
	}
	public String setScore(String target, String subject) {
		String mark = "";
		boolean rletterFlag = true;

		char[] targetchars = subject.trim().replaceAll(" ", "").toCharArray();
		for (char c : targetchars) {
			if (c < 'あ' || c > 'ん') {
				rletterFlag = false;
			}
		}

		if (rletterFlag) {
			if (subject.trim().replaceAll(" ", "")
					.matches(target.trim().replaceAll(" ", ""))) {
				mark = "O";
			} else if (subject.matches("ibid.")) {
				mark = "保留";
			} else {
				mark = "X";
			}
		} else {
			if (subject.trim().replaceAll(" ", "")
					.indexOf(target.trim().replaceAll(" ", "")) >= 0) {

				mark = "O";

			} else if (subject.matches("ibid.")) {
				mark = "保留";
			} else {

				char[] chars = subject.trim().replaceAll(" ", "").toCharArray();

				int count = 0;
				for (char c : chars) {
					if (target.trim().replaceAll(" ", "").indexOf(c) >= 0) {
						count++;
					}
				}

				if (count > 1) {
					mark = "O";
				} else {
					mark = "X";
				}
			}
		}
		return mark;
	}
	protected Vector<Integer> getVocaCount() {
		vocaCounts = new Vector<Integer>();
		for (int i = 0; i < vocabularies.size(); i++) {
			vocaCounts.add(i + 1);
		}
		return vocaCounts;
	}
	
}