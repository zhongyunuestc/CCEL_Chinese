package Recognition;
/*
可以加入字典的中文实体识别，包括电影名，人名，地名，机构名，组织名
*/
import java.io.UnsupportedEncodingException;

import ICTCLAS.I3S.AC.ICTCLAS50;

public class SegmentDic {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	
	public String Split(String input,int it) throws UnsupportedEncodingException{
		ICTCLAS50 ictclas = new ICTCLAS50();
		// initial
		String argu = "."; // 当前目录
		
		if (ictclas.ICTCLAS_Init(argu.getBytes("utf-8")) == false) {
			System.err.println("Initail fail!");
		}
	//	System.out.println("Initial success!");
		
//		// 未添加词典前分词
//	//	System.out.println(input);
//	//	ictclas.ICTCLAS_SetPOSmap(ictclas.PKU_POS_MAP_FIRST);
//		byte nativeBytes[] = ictclas.ICTCLAS_ParagraphProcess(
//				input.getBytes("UTF-8"), 0, 1);//后面的1就表示是有pos的，是0就没有pos的
//		String result = new String(nativeBytes, 0, nativeBytes.length, "UTF-8");
//	//	System.out.println("未导入用户词典的分词结果是：\t" + result);

		// 添加用户词典分词
		int count = 0;
		String userDir = "Dict/seguserDict.txt"; // 用户词典路径
		byte[] userDirb = userDir.getBytes();
		count = ictclas.ICTCLAS_ImportUserDictFile(userDirb, 3);
		//System.out.println("\n导入用户词个数：\t" + count);
		//count = 0;

		// 导入用户词典后再分词
		byte[] nativeBytes1 = ictclas.ICTCLAS_ParagraphProcess(
				input.getBytes("UTF-8"), 0, it);
		String result1 = new String(nativeBytes1, 0, nativeBytes1.length,
				"UTF-8");
//		MergeWord mw = new MergeWord();
//		String txt=mw.Merge(result1);
	//	System.out.println("导入用户词典后的分词结果是：\t" + txt);

		// 退出，释放分词组件资源
		ictclas.ICTCLAS_Exit();
		return result1;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		SegmentDic sd=new SegmentDic();
		String text = "乔丹是NBA历史上最伟大的篮球运动员之一。";
		System.out.println(sd.Split(text,1));
	}
}