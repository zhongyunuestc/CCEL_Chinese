package Recognition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ICTCLAS.I3S.AC.*;

public class MergeWordTest {
	public static void main(String[] args) throws IOException {
		SegmentProcess sp=new SegmentProcess();
		String txt="国民美女";
		txt=sp.segmentmain(txt);//输出分词的结果
		System.out.println("分词的结果--------------------------");
		System.out.println(txt);
		//----------合并名词。的结构---------------------------
		MergeWord mw = new MergeWord();
		txt = mw.Merge(txt);
		System.out.println("合并的结果--------------------------");
		System.out.println(txt);
       List<String> ls=new ArrayList<String>();
       ls.add("香港");
       ls.add("钟景辉");
       ls.add("夏雨");
       System.out.println(ls.contains("夏雨"));
	}
}
