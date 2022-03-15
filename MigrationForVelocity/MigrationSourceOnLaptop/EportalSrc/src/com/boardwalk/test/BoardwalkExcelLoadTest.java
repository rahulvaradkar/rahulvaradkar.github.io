
public class BoardwalkExcelLoadTest
{

		ExcelExportBuffer  exportBuffer = null;
		ExcelExportResponseBuffer exportResponseBuffer = null;
		String datafileName = "";
		String resultfileName = "";
		String logFileName = "";




		public static void main(String arguments())
		{
				Properties defaultProps = new Properties();
				FileInputStream in = new FileInputStream("test.properties");
				defaultProps.load(in);
				in.close();
				// create program properties with default
				Properties applicationProps = new Properties(defaultProps);


			}

};



