package Similary;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import ICTCLAS.I3S.AC.*;
import Recognition.SegmentDic;

public class TextProcessing {
	/**
	 * 分词并删除停用词
	 * @throws UnsupportedEncodingException 
	 */
	public List<String> SegmentProcess(String zhaiYao) throws UnsupportedEncodingException{

		String line=null;
		HashSet<String> StopWord=new HashSet();//记录停用词
		ArrayList<String> WordList=new ArrayList();//用于存储特征词
		String regex="\\《.*?\\》";//正则表达式
		String regex1="(《)|(》)";
		String regex2="[a-zA-Z]+";
		try {
			InputStream in = new FileInputStream("Data/StopWord.txt");//读取文件上的数据
			//指定编码方式，将字节流向字符流的转换
			InputStreamReader isr = new InputStreamReader(in,"UTF-8");
			//创建字符流缓冲区
			BufferedReader bufr = new BufferedReader(isr);
			while((line = bufr.readLine())!=null){
				//				 System.out.println(line);
				StopWord.add(line);
			}
			isr.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//分词
		SegmentDic sd=new SegmentDic();
		String str=sd.Split(zhaiYao,1);
		//		SegmentProcess sp = new SegmentProcess();
		//		sp.init();
		//		//String str =sp.paragraphProcess(zhaiYao, 0);
		//		String str =sp.paragraphProcess(zhaiYao, 1);
		// System.out.println(str);
		String word[] =str.split("\\s+");
		/*
		 * 用文本中的实体和名词当特征
		 */
		ArrayList<String>nounlist=new ArrayList<String>();//储存文本中的名词特征

		/*
		 * 取书名号里面的部分,一般是作品名
		 */
		if(zhaiYao.contains("《")){
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(zhaiYao);
			while(m.find()){
				String param=m.group().replaceAll(regex1,""); 
				if(!nounlist.contains(param)){
					nounlist.add(param);
				}

			}
		}

		/*
		 * 获取里面的字母部分
		 */
		Pattern p=Pattern.compile(regex2);
		Matcher m=p.matcher(zhaiYao);
		while(m.find()){
			String param=m.group(); 
			if(!nounlist.contains(param)){
				nounlist.add(param);
			}
		}


		for(int i=0;i<word.length;i++){
			if(word[i].contains("n")){
				// if(word[i].endsWith("/n")){
				String sub=word[i].substring(0, word[i].indexOf("/"));
				if(!sub.equals("、")&&!sub.equals("-")&&sub.length()>1)
					nounlist.add(sub);
			}
		}
		return nounlist;
		//		 for(int i=0;i<nounlist.size();i++){
		//			if(StopWord.contains(nounlist.get(i))){
		//				nounlist.get(i).replaceAll(nounlist.get(i),"");
		////				System.out.println("删除停用词:"+word[i]);
		////				word[i].replaceAll(word[i], "");
		//			}
		//			else{
		//				WordList.add(nounlist.get(i));
		//			}
		//		 }
		//		return WordList;
	}
	/*
	 * 统计文本里面的关键字
	 */
	public List<String> selectKey(List<String>list){

		List<String>endlist=new ArrayList<String>();
		HashMap<String,Integer>hash=new HashMap<String,Integer>();
		for(int i=0;i<list.size();i++){
			String str=list.get(i);
			int sum=0;
			for(int j=0;j<list.size();j++){
				if(str.equals(list.get(j)))
					sum++;
			}//end for
			hash.put(str, sum);
			//if(sum>4) endlist.add(str);
		}//end for	 

		List<Map.Entry<String, Integer>> infoIds =new ArrayList<Map.Entry<String, Integer>>(hash.entrySet());

		//排序  
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {  
			public int compare(Map.Entry<String, Integer> o1,  
					Map.Entry<String, Integer> o2) {  
				return ( o1.getValue()-o2.getValue());  
			}  
		});  

		if(infoIds.size()>8){//有些页面的文本很短
			for (int i=infoIds.size()-1;i>infoIds.size()-7;i--) {  
				Entry<String,Integer> ent=infoIds.get(i);  
				endlist.add(ent.getKey());
				//  System.out.println(ent.getKey()+"="+ent.getValue());       
			}   
		}
		else{
			for (int i=infoIds.size()-1;i>=0;i--) {  
				Entry<String,Integer> ent=infoIds.get(i);  
				endlist.add(ent.getKey());
				//System.out.println(ent.getKey()+"="+ent.getValue());       
			}   
		}
		return endlist;
	}
	/**
	 * 删除List中重复元素
	 */
	public List removeDuplicateWithOrder(List list) {
		Set set = new HashSet();  //集合具有唯一性，利用这一点特点我们可以确保元素的唯一性
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		} 
		list.clear();
		list.addAll(newList);
		return list;
	}

	public static void main(String[]arg) throws IOException{

		String st="网络是年轻人的天下，发布会上王晶称自己“年龄有些大”，但作为最具商业头脑的香港导演，王晶说自己“有颗‘90后’的心”。他也花了很多时间了解互联网电影，在解决了收费的问题后，他觉得互联网将早晚取代传统院线。";
		TextProcessing tp=new TextProcessing();
		System.out.println(tp.SegmentProcess(st));
	}


}
