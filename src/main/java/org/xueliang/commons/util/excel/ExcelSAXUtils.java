/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.xueliang.commons.util.excel;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XSSF and SAX (Event API)
 */
public class ExcelSAXUtils {


	int countrows = 0;
	private OPCPackage opcPackage;
	private int minColumns;
	
	/**
	 * The type of the data value is indicated by an attribute on the cell. The
	 * value is usually in a "v" element within the cell.
	 */
	enum XSSFDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	}

	public ExcelSAXUtils(OPCPackage opcPackage) {
		this.opcPackage = opcPackage;
	}

	public void processOneSheet(StylesTable styles, ReadOnlySharedStringsTable readOnlySharedStringsTable, InputStream sheetInputStream)
			throws Exception {
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();

		ContentHandler handler = new SheetHandler(styles, readOnlySharedStringsTable);
		InputSource sheetSource = new InputSource(sheetInputStream);
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);
		sheetInputStream.close();
	}

	public void processAllSheets() throws Exception {
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.opcPackage);
		XSSFReader xssfReader = new XSSFReader(this.opcPackage);
		StylesTable styles = xssfReader.getStylesTable();

		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
		while (iter.hasNext()) {
			System.out.println("Processing new sheet:");
			InputStream sheet = iter.next();
			processOneSheet(styles, strings, sheet);
			sheet.close();
		}
	}

	/**
	 * See org.xml.sax.helpers.DefaultHandler javadocs
	 */
	private class SheetHandler extends DefaultHandler {
		/**
		 * Table with styles
		 */
		private StylesTable stylesTable;

		/**
		 * Table with unique strings
		 */
		private ReadOnlySharedStringsTable readOnlySharedStringsTable;

		/**
		 * Number of columns to read starting with leftmost
		 */
		private final int minColumnCount = -1;

		// Set when V start element is seen
		private boolean vIsOpen;

		// Set when cell start element is seen;
		// used when cell close element is seen.
		private XSSFDataType nextDataType;

		// Used to format numeric cell values.
		private short formatIndex;
		private String formatString;
		private final DataFormatter formatter = new DataFormatter();;

		private int thisColumn = -1;
		// The last column printed to the output stream
		private int lastColumnNumber = -1;

		// Gathers characters as they are seen.
		private StringBuffer value = new StringBuffer();

		private SheetHandler(StylesTable styles, ReadOnlySharedStringsTable readOnlySharedStringsTable) {
			this.readOnlySharedStringsTable = readOnlySharedStringsTable;
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if ("row".equals(name)) {
				// We're onto a new row
//				System.out.print("第 " + countrows++ + " 行：");
			} else if ("inlineStr".equals(name) || "v".equals(name)) {
				vIsOpen = true;
				// Clear contents cache
				value.setLength(0);
			} else if ("c".equals(name)) { // c => cell
				// Get the cell reference
				String r = attributes.getValue("r");
				int firstDigit = -1;
				for (int c = 0; c < r.length(); ++c) {
					if (Character.isDigit(r.charAt(c))) {
						firstDigit = c;
						break;
					}
				}
				thisColumn = nameToColumn(r.substring(0, firstDigit));

				// Set up defaults.
				this.nextDataType = XSSFDataType.NUMBER;
				this.formatIndex = -1;
				this.formatString = null;
				String cellType = attributes.getValue("t");
				String cellStyleStr = attributes.getValue("s");
				if ("b".equals(cellType)) {
					nextDataType = XSSFDataType.BOOL;
				} else if ("e".equals(cellType)) {
					nextDataType = XSSFDataType.ERROR;
				} else if ("inlineStr".equals(cellType)) {
					nextDataType = XSSFDataType.INLINESTR;
				} else if ("s".equals(cellType)) {
					nextDataType = XSSFDataType.SSTINDEX;
				} else if ("str".equals(cellType)) {
					nextDataType = XSSFDataType.FORMULA;
				} else if (cellStyleStr != null) {
					// It's a number, but almost certainly one
					// with a special style or format
					int styleIndex = Integer.parseInt(cellStyleStr);
					XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null) {
						this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {

			String thisStr = null;
			// v => contents of a cell
			if ("v".equals(name)) {
				// Process the value contents as required.
				// Do now, as characters() may be called more than once
				switch (nextDataType) {
				case BOOL:
					char first = value.charAt(0);
					thisStr = first == '0' ? "FALSE" : "TRUE";
					break;
				case ERROR:
					thisStr = "\"ERROR:" + value.toString() + '"';
					break;
				case FORMULA:
					// A formula could result in a string value,
					// so always add double-quote characters.
					thisStr = '"' + value.toString() + '"';
					break;
				case INLINESTR:
					// TODO: have seen an example of this, so it's untested.
					XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
					thisStr = '"' + rtsi.toString() + '"';
					break;
				case SSTINDEX:
					String sstIndex = value.toString();
					try {
						int idx = Integer.parseInt(sstIndex);
						XSSFRichTextString rtss = new XSSFRichTextString(readOnlySharedStringsTable.getEntryAt(idx));
						thisStr = '"' + rtss.toString() + '"';
					} catch (NumberFormatException ex) {
						// output.println("Failed to parse SST index '" +
						// sstIndex + "': " + ex.toString());
					}
					break;
				case NUMBER:
					String n = value.toString();
					if (this.formatString != null) {
						thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex,
								this.formatString);
					} else {
						thisStr = n;
					}
					break;
				default:
					thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
					break;
				}
				// Output after we've seen the string contents
				// Emit commas for any fields that were missing on this row
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
				for (int i = lastColumnNumber; i < thisColumn; ++i) {
					// 空单元格
					System.out.print(",");
				}
				// output.print(',');

				// Might be the empty string.
				// output.print(thisStr);
				System.out.print(thisStr);

				// Update column
				if (thisColumn > -1) {
					lastColumnNumber = thisColumn;
				}
			} else if ("row".equals(name)) {
				// Print out any missing commas if needed
				if (minColumns > 0) {
					// Columns are 0 based
					if (lastColumnNumber == -1) {
						lastColumnNumber = 0;
					}
					for (int i = lastColumnNumber; i < (this.minColumnCount); i++) {
						System.out.print(",");
					}
				}
				// We're onto a new row
				System.out.println(" 结束");
				lastColumnNumber = -1;
			}
		}

		/**
		 * Captures characters only if a suitable element is open. Originally
		 * was just "v"; extended for inlineStr also.
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (vIsOpen) {
				value.append(ch, start, length);
			}
		}

		/**
		 * Converts an Excel column name like "C" to a zero-based index.
		 *
		 * @param name
		 * @return Index corresponding to the specified name
		 */
		private int nameToColumn(String name) {
			int column = -1;
			for (int i = 0; i < name.length(); ++i) {
				int c = name.charAt(i);
				column = (column + 1) * 26 + c - 'A';
			}
			return column;
		}
	}
}
