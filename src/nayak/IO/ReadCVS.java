package nayak.IO;

public class ReadCVS {

	public static String[] readHeader(String filepath) {
		TextIO.readFile(filepath);
		String s = TextIO.getln();
		return s.split(",");
	}

	public static String[] readColumn(String filepath, int col) {
		TextIO.readFile(filepath);
		// skip first row
		TextIO.getln();
		int count = 0;
		while (!TextIO.eof()) {
			TextIO.getln();
			count++;
		}

		String[] data = new String[count];
		TextIO.readFile(filepath);
		TextIO.getln();
		count = 0;
		while (!TextIO.eof()) {
			String s = TextIO.getln();
			String[] row = s.split(",");
			data[count] = row[col];
			count++;
		}

		return data;
	}

	/**
	 * Reads specified rows and columns (all inclusive) to 2D double array. 
	 * @param startCol (first column is 0)
	 * @param endCol
	 * @param startRow (first row is 0)
	 * @param endRow
	 * @return
	 */
	public static double[][] readData(int startCol, int endCol, int startRow, int endRow, String filepath) {
		int numCols = endCol - startCol + 1;
		int numRows = endRow - startRow + 1;
		double[][] data = new double[numRows][numCols];

		TextIO.readFile(filepath);
		TextIO.getln();

		int count = 0, dataCount = 0;
		while (!TextIO.eof()) {

			if (count < startRow) {
				TextIO.getln();
			} else if (count >= startRow && count <= endRow) {

				String s = TextIO.getln();
				String[] row = s.split(",");

				for (int i = 0; i < numCols; i++) {
					data[dataCount][i] = Double.parseDouble(row[i + startCol]);
				}
				dataCount++;
			} else if (count > endRow) {
				break;
			}
			count++;
			
		}

		return data;
	}
}
