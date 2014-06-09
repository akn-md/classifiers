package nayak.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleTable implements Serializable {

	public String eol = "\n";
	public String delimiter;
	public String[] columnNames;
	public HashMap<String, Integer> nameToIndex = null;;
	public int numDataRows;
	public int numDataColumns;
	public boolean printHeader = true;

	public String[][] stringMatrix;

	public String getString(String name, int rowIndex) {
		return stringMatrix[rowIndex][nameToIndex.get(name)];
	}

	public String getString(int rowIndex, int columnIndex) {
		return stringMatrix[rowIndex][columnIndex];
	}

	public char getChar(String name, int rowIndex) {
		return stringMatrix[rowIndex][nameToIndex.get(name)].charAt(0);
	}

	public char getChar(int rowIndex, int columnIndex) {
		return stringMatrix[rowIndex][columnIndex].charAt(0);
	}

	public int getInt(int rowIndex, int columnIndex) {
		return Integer.parseInt(stringMatrix[rowIndex][columnIndex]);
	}

	public long getLong(int rowIndex, int columnIndex) {
		return Long.parseLong(stringMatrix[rowIndex][columnIndex]);
	}

	public double getDouble(int rowIndex, int columnIndex) {
		return Double.parseDouble(stringMatrix[rowIndex][columnIndex]);
	}

	public float getFloat(int rowIndex, int columnIndex) {
		return Float.parseFloat(stringMatrix[rowIndex][columnIndex]);
	}

	public int getNumRows() {
		return numDataRows;
	}

	public int getNumColumns() {
		return numDataColumns;
	}

	public int getNumColumns(int rowIndex) {
		return stringMatrix[rowIndex].length;
	}

	public String[] getColumnAsStringArray(int columnIndex) {

		String[] stringArray = new String[numDataRows];

		for (int i = 0; i < numDataRows; i++) {
			stringArray[i] = stringMatrix[i][columnIndex];
		}

		return stringArray;
	}

	public String[] getRowAsStringArray(int rowIndex) {

		String[] stringArray = new String[stringMatrix[rowIndex].length];

		for (int i = 0; i < stringMatrix[rowIndex].length; i++) {
			stringArray[i] = stringMatrix[rowIndex][i];
		}

		return stringArray;
	}

	public ArrayList<String> getColumnAsArrayList(int columnIndex) {

		ArrayList<String> arrayList = new ArrayList<String>();

		for (int i = 0; i < numDataRows; i++) {
			arrayList.add(stringMatrix[i][columnIndex]);
		}

		return arrayList;
	}

	public ArrayList<String> getRowAsArrayList(int rowIndex) {

		ArrayList<String> arrayList = new ArrayList<String>();

		for (int i = 0; i < numDataColumns; i++) {
			arrayList.add(stringMatrix[rowIndex][i]);
		}

		return arrayList;
	}

	public void addRow(String[] stringArray) {

		String[][] newStringMatrix = new String[numDataRows + 1][];

		for (int i = 0; i < numDataRows; i++) {
			newStringMatrix[i] = stringMatrix[i];
		}

		newStringMatrix[numDataRows] = stringArray.clone();

		numDataRows++;

		stringMatrix = newStringMatrix;

	}

	public void writeToFile(String filePath) {

		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			// write header
			for (int j = 0; j < numDataColumns; j++) {
				if (j > 0)
					file.writeBytes("\t");
				file.writeBytes(columnNames[j]);
			}
			file.writeBytes("\n");

			// write data
			for (int i = 0; i < numDataRows; i++) {

				for (int j = 0; j < numDataColumns; j++) {

					if (j > 0)
						file.writeBytes("\t");
					file.writeBytes(stringMatrix[i][j]);

				}
				file.writeBytes("\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
