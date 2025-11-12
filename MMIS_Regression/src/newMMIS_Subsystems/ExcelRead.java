package newMMIS_Subsystems;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ExcelRead extends Login {
	
	static String ZIP;
	static String AID;
	static String DOB;
	static String ADR;
	
	static ArrayList<String> data=new ArrayList<>();
	static ArrayList<String> dataexcel=new ArrayList<>();
	static Workbook workbook = null;
	static Sheet sheet=null;
	static Cell cell=null;
	static int lastrownum;
	static int lastcellnum;
	public static int rowpointer=1;

	public static void main(String[] args) {		
		while(rowpointer<getlastrownum()) {
			getspreaddata(rowpointer);
		}
	}
	
	public static void call(int rowpointer) {
		dataexcel=getspreaddata(rowpointer);
		System.out.println(dataexcel);
		dataexcel.clear();
	}
	
	/*while(rowpointer<getlastrownum()) {
		dataexcel=getspreaddata(rowpointer);
		System.out.println(dataexcel);
		dataexcel.clear();
		rowpointer++;
	}*/
	
	public static void setexcelfile(String path) {
		try {
		workbook = Workbook.getWorkbook(new File(path));
        sheet = workbook.getSheet(0);
		} catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } 
	}
	
	public static int getlastrownum() {
		lastrownum=sheet.getRows();
		return lastrownum;
	}
	
	public static int getlastcellnum() {
		lastcellnum=sheet.getColumns();
		return lastcellnum;
	}
	
	public static void getdata() {
		cell = sheet.getCell(0, 2);
        //System.out.print(cell.getContents() + "->"); 
        data.add(cell.getContents());
        cell = sheet.getCell(1, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
        cell = sheet.getCell(2, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
        cell = sheet.getCell(3, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
       /* cell = sheet.getCell(4, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
        cell = sheet.getCell(5, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
        cell = sheet.getCell(6, 2);
        //System.out.print(cell.getContents() + "->");
        data.add(cell.getContents());
        cell = sheet.getCell(7, 2);
        //System.out.println(cell.getContents());
        data.add(cell.getContents());            
        //System.out.println(data);  */
	}
	
	public static ArrayList<String> getspreaddata(int rowpointer) {
		for(int i=rowpointer;i<=rowpointer;i++) {
			for(int j=0;j<getlastcellnum();j++) {
				cell = sheet.getCell(j, i);
				data.add(cell.getContents());
			}
		}
		return data;
	}


//public static ArrayList<String[]> getspreaddata1(int rowpointer) {
//	ArrayList<String[]> rowList = new ArrayList<String[]>();
//	String[] colList=new String[getlastcellnum()]; //there are 3 columns
//
//	for(int i=rowpointer;i<=rowpointer;i++) {
//		for(int j=0;j<getlastcellnum();j++) {
//			cell = sheet.getCell(j, i);
//			colList[j]=cell.getContents().trim();
//		}
//		rowList.add(colList);
//
//	}
//	return rowList;
//}
	
}

/*fromsph=data.get(0);
tosph=data.get(1);
datetype=data.get(2);
startdate=data.get(3);
topmpid=data.get(4);
distance=data.get(5);
topcpid=data.get(6);
memberid=data.get(7);*/
