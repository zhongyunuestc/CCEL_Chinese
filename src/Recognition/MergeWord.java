package Recognition;
import java.util.ArrayList;

public class MergeWord {
	/*
	 * 
	 * 函数功能 1相连的两个名词合并在一起，最后用名词标 如 尤克/nrf 杯/ng 输出 尤克杯/n 2“的”字的处理
	 * （1）“的”前面如果是动词就不处理 （2）“的”后面是名词 合并 最后用名词标注 （3）“的”后面是形容词+名次 合并 最后用名词标注
	 * 
	 * @author xwchen
	 */
	public String Merge(String s) {
		String[] t = s.split(" ");
		String ret = "";
		ArrayList<DWord> sentence = new ArrayList<DWord>();
		for (int i = 0; i < t.length; ++i) {
			DWord dw = new DWord();
			dw = dw.TransforStringToWordAndType(t[i]);
			sentence.add(dw);
		}
		// 相连的两个名词合并在一起，最后用名词标
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < sentence.size() - 1; ++i) {
				String tp1 = sentence.get(i).getType();
				String tp2 = sentence.get(i + 1).getType();
				String w1 = sentence.get(i).getWord();
				String w2 = sentence.get(i + 1).getWord();
				//System.out.println(tp1 + "  " + tp2);
				//System.out.println(w1+" "+w2);
				if (ISNoun(tp1) && ISNoun(tp2)) {
					change = true;
					sentence.get(i).setWord(w1 + w2);
					sentence.get(i).setType("nn");
					sentence.remove(i + 1);
				}
			}
		}
		
		for (int i = 0; i < sentence.size(); ++i) {
			ret += sentence.get(i).getWord() + "/" + sentence.get(i).getType();
			if (i != sentence.size() - 1)
				ret += " ";
		}
		return ret;
	}

	public boolean ISNoun(String type) {
		if (type == null || type.length() < 1)
			return false;
		if (type.length() >0&&type.charAt(0) == 'n'&&!type.equals("nr")&&!type.equals("ns")
			&&!type.equals("nt")&&!type.equals("nz")&&!type.equals("nn"))
			return true;
		else if (type.length() == 2)
			return type.equals("vn");
		else
			return false;
	}

	public boolean ISAdj(String type) {
		if (type == null || type.length() < 1)
			return false;
		return type.charAt(0) == 'a';
	}

	public boolean ISVerb(String type) {
		if (type == null || type.length() < 1)
			return false;
		return type.charAt(0) == 'v';
	}
}