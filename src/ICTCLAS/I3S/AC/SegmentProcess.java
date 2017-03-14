package ICTCLAS.I3S.AC;
/*
 对输入语句进行分词处理
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SegmentProcess {
   // static String[] str2=new String[]{};
	//static String str=null;
	private ICTCLAS50 ictclas = new ICTCLAS50();
	public boolean init(){
		String argu=".";
		try{
			if (  ictclas.ICTCLAS_Init(argu.getBytes("utf-8")  )==false){
				System.out.println("Init Fail!");
				System.out.println("----------");
				return false;
			}else{
//				
				int nCount=0;
				String usrdir="userdict.txt";
				byte[] usrdirb = usrdir.getBytes();
				nCount = ictclas.ICTCLAS_ImportUserDictFile(usrdirb, 3);

				if (ictclas.ICTCLAS_SetPOSmap(0)==1){
					return true;
				}else{
					System.out.println("设置失败");
					return false;
				}
			}
		}catch(Exception ex){
			return false;
		}
	}
	
	
	public String fileProcess(String src, int istag, String ecodeName){
		return "";
	}
	
	
	public String paragraphProcess(String src, int istag){
		byte[] rt = null;
		String rtStr = null;
		try{
			rt=ictclas.ICTCLAS_ParagraphProcess(src.getBytes("UTF-8"), 3, istag);//设置格式为utf-8
			rtStr = new String(rt,0,rt.length,"UTF-8");
		}catch(UnsupportedEncodingException e1){
			System.out.println("编码异常，分词失败");
			e1.printStackTrace();
		}
		
	   // rtStr=rtStr.replaceAll("/\\w+", "");
		         return rtStr.trim();
	}
	
	public boolean exit(){
		return ictclas.ICTCLAS_Exit();
	}
	
	public String segmentmain(String str) throws IOException{
		SegmentProcess sp = new SegmentProcess();
		sp.init();
		String segmentresult=sp.paragraphProcess(str, 1);
		return segmentresult;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		String str="李娜的丈夫姜山是网球运动员";
		SegmentProcess sp = new SegmentProcess();
		sp.init();
		//String str =sp.paragraphProcess("他经常和我一起在学校打台球",0);
		String str1 = sp.paragraphProcess(str,0);
		//System.out.println(str);
		System.out.println(str1.replace(" ","/"));
			
	}
}