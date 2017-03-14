package Recognition;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类用于实现利用LTP云平台分词标注来识别的的webAPI调用
 */
public class Entity {
	public List<String> getEntity(String text) throws IOException {
		List<String> allentitylist = new ArrayList<String>();// 记录所有实体
		List<String>Speciallist=new ArrayList<String>();//记录特殊的名词实体，一般命名实体技术识别不出来
		String linestr=null;
		/*
		 * 取书名号中的部分
		 */
	 	 String regex="\\《.*?\\》";//正则表达式
    	 String regex1="(《)|(》)";
    	 if(text.contains("《")){
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(text);
			while(m.find()){
				String param=m.group().replaceAll(regex1,""); 
				if(!allentitylist.contains(param)){
					allentitylist.add(param);
				}
				 
			}//end while
		}
    	
		/*
		 * 将特殊词加到列表中
		 */
		try {
			InputStream in = new FileInputStream("Dict/SpecialNoun.txt");//读取文件上的数据
			//指定编码方式，将字节流向字符流的转换
			InputStreamReader isr = new InputStreamReader(in,"UTF-8");
			//创建字符流缓冲区
			BufferedReader bufr = new BufferedReader(isr);
			while((linestr = bufr.readLine())!=null){
//				 System.out.println(line);
				Speciallist.add(linestr);
			}
	        isr.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 //System.out.println(Speciallist);
	/*
	 * 开始抽取命名实体 缩写和代词的没法识别
	 */
		if (text.length() > 0) { // 判定输入的参数合法
			String api_key = "73S1i7H2ILMv1JbNzUOdibruIwhaCGNa6mcC7dyV"; // 云平台的apiID
			String pattern = "pos"; // 指定分析模式://待分析的文本
			String format = "plain"; // 指定结果类型
			text = URLEncoder.encode(text, "utf-8"); // 指定编码格式为UTF-8
			URL url = new URL("http://ltpapi.voicecloud.cn/analysis/?"
					+ "api_key=" + api_key + "&" + "text=" + text + "&"
					+ "format=" + format + "&" + "pattern=" + pattern);// 构造web访问url
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader innet = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = null;
			String segment[] = new String[] {};
			while ((line = innet.readLine()) != null) {
				line = line.replace("_", "");
			//System.out.println(line);
				segment = line.split(" ");
				for (int i = 0; i < segment.length; i++) {
					// 识别出人名
					if (segment[i].contains("nh")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("nh"));
						// System.out.println(sub);
//						if (!PeopleEntitylist.contains(sub)) {
//							PeopleEntitylist.add(sub);
//						}
						if (!allentitylist.contains(sub)) {
							allentitylist.add(sub);
						}
					}// end people
					// 识别出地名
					else if (segment[i].contains("ns")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("ns"));
						if (!allentitylist.contains(sub)) {
							allentitylist.add(sub);
						}
					}// end place
					// 识别出机构名
					else if (segment[i].contains("ni")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("ni"));
						if (!allentitylist.contains(sub)) {
							allentitylist.add(sub);
						}
					}// end place
					
					// 识别专有名词
					else if (segment[i].contains("nz")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("nz"));
						if (!allentitylist.contains(sub)) {
							allentitylist.add(sub);
						}
					}// end Unique
				  //识别出外文字母
					else if(segment[i].contains("ws")){
						String sub = segment[i].substring(0,
							 segment[i].indexOf("ws"));
						if(!allentitylist.contains(sub)){
							allentitylist.add(sub);
						}
				       
					}
					// 识别其他名词，这些名词可以当做实体用
					else if (segment[i].endsWith("n")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("n"));
						if(Speciallist.contains(sub)&&!allentitylist.contains(sub)){
							allentitylist.add(sub);
						}
					}// end other
				  //处理缩写的形式
					else if (segment[i].endsWith("j")) {
						String sub = segment[i].substring(0,
								segment[i].indexOf("j"));
						if(!allentitylist.contains(sub)){
							allentitylist.add(sub);
						}
					}// end other
				}//end for
			}// end while
			innet.close();
		}// end if
		
	System.out.println("所有实体:" + allentitylist);//只包括人名，地名，机构名，专有名,外文名，特殊的名词
		 return allentitylist;
	}

	public static void main(String[] arg) throws IOException {
		String text="杨洋参加过《快乐女生》。";
	  	Entity en = new Entity();
	  	en.getEntity(text);
	//	System.out.println(en.getEntity(text));
//		String subkey="湖南　";
//		System.out.println(subkey.replaceAll("　", "")+"yes");
		
	}

}
