package Recognition;

public class DWord {
	String word;//分词后的词语
	String type;//实体类型

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void display() {
		System.out.println("word = " + word + " type = " + type);
	}

	public DWord TransforStringToWordAndType(String s) {
		DWord dw = new DWord();
		String[] wt = new String[2];
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) == '/') {
				wt[0] = s.substring(0, i);
				wt[1] = s.substring(i + 1, s.length());
				break;
			}
		}
		dw.setWord(wt[0]);
		dw.setType(wt[1]);
		// System.out.println("word = " + wt[0] + "type = " + wt[1]);
		return dw;
	}
}
