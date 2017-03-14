package Recognition;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//http://blog.sina.com.cn/s/blog_58c23dea0100cx4i.html
/**
 * 实体识别模块
 * @author zhongyun
 *
 * 2017年3月13日
 */
public class EntityRecogtion {

	public List<String> entityrecognize(String text) throws UnsupportedEncodingException{
		String regex="\\《.*?\\》";//正则表达式
		String regex1="(《)|(》)";
		//	String regex2="[a-zA-Z]+";
		String []sentence=null;//存储文本中的句子
		String[] entity=new String[]{};
		SegmentDic sd=new SegmentDic();
		List<String> allEntitylist=new ArrayList<String>();
		/*
		 * 取书名号里面的部分
		 */
		if(text.contains("《")){
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(text);
			while(m.find()){
				String param=m.group().replaceAll(regex1,""); 
				if(!allEntitylist.contains(param)){
					allEntitylist.add(param);
				}

			}//end while
		}

		/*
		 * 获取里面的字母部分
		 */
		//			Pattern p=Pattern.compile(regex2);
		//			Matcher m=p.matcher(text);
		//			while(m.find()){
		//				String param=m.group(); 
		//				if(!allEntitylist.contains(param)){
		//				 allEntitylist.add(param);
		//				}
		//		      }
		String segresult=sd.Split(text,1);
		//System.out.println(segresult);
		entity=segresult.split(" ");
		//System.out.println(entity[0]);
		for(int j=0;j<entity.length;j++)
		{
			// System.out.println(entity[j]);	
			//识别人名
			if(entity[j].endsWith("/nr"))
			{
				//System.out.println(entity[j]);
				String sub=entity[j].substring(0,entity[j].indexOf("/nr"));
				// System.out.println(sub);
				if(!allEntitylist.contains(sub)){
					allEntitylist.add(sub);
				}
			}//end if

			//识别地名
			else if(entity[j].endsWith("/ns"))
			{
				//System.out.println(entity[j]);
				String sub=entity[j].substring(0,entity[j].indexOf("/ns"));
				// System.out.println(sub);
				if(!allEntitylist.contains(sub)){
					allEntitylist.add(sub);
				}
			}//end if

			//识别机构名
			else if(entity[j].endsWith("/nt"))
			{
				//System.out.println(entity[j]);
				String sub=entity[j].substring(0,entity[j].indexOf("/nt"));
				// System.out.println(sub);
				if(!allEntitylist.contains(sub)){
					allEntitylist.add(sub);
				}
			}//enn if

			//			    //其他专有名词实体
			//			     else if(entity[j].endsWith("/nz"))
			//			     {
			//			    	 //System.out.println(entity[j]);
			//			    	 String sub=entity[j].substring(0,entity[j].indexOf("/nz"));
			//			    	// System.out.println(sub);
			//			    	 if(!allEntitylist.contains(sub)){
			//			    		 allEntitylist.add(sub);
			//			    	   }
			//			       }//end if

			//识别一些外文字符(有些中文字符后面也带/x)
			//			     else if(entity[j].endsWith("/x"))
			//			     {
			//			    	 //System.out.println(entity[j]);
			//			    	 String sub=entity[j].substring(0,entity[j].indexOf("/x"));
			//			    	// System.out.println(sub);
			//			    	 if(!allEntitylist.contains(sub)){
			//			    		 allEntitylist.add(sub);
			//			    	   }
			//			       }//end if

			//字典中自己增加的词项
			else if(entity[j].endsWith("/un"))
			{
				//System.out.println(entity[j]);
				String sub=entity[j].substring(0,entity[j].indexOf("/un"));
				// System.out.println(sub);
				if(!allEntitylist.contains(sub)){
					allEntitylist.add(sub);
				}
			}//end if
		}

		/*
		 * 过滤以及纠正，建立一个hashmap存储外国人名
		 */    
		List<String> entitylist=new ArrayList<String>();
		entitylist.addAll(allEntitylist);
		Dic dic=new Dic();
		HashMap<String,String> hashdic=dic.getDic();
		List<String>keyset=new ArrayList<String>();
		keyset.addAll(hashdic.keySet());
		//   System.out.println(keyset);
		for(int i=0;i<allEntitylist.size();i++){
			String str=allEntitylist.get(i);
			Boolean flag=str.contains("0")||str.contains("1")||str.contains("2")||str.contains("3")||str.contains("4")
					||str.contains("5")||str.contains("6")||str.contains("7")||str.contains("8")||str.contains("9")
					||str.startsWith("老");

			if(flag&&str.length()<4){
				entitylist.remove(str);
			}

			if(keyset.contains(str)){
				entitylist.set(i, hashdic.get(str));
			}

		}
		System.out.println("实体指称项集合:" + entitylist);//只包括人名，地名，机构名，专有名,外文名，特殊的名词
		return entitylist;
	}



	public static void main(String[] args) throws UnsupportedEncodingException{	
		String text=null;
		try { 
			FileInputStream fin=new FileInputStream("text.txt");
			BufferedReader innet = new BufferedReader(new InputStreamReader(fin));
			while((text=innet.readLine())!=null)
			{ 
				System.out.println("文本为："+text);
				//				 byte[] buf=new byte[1000000];//缓存的大小决定文本的大小
				//				 int len=fin.read(buf);//从text.txt中读出内容
				//				 text=new String(buf,0,len);
				List<String>result=new ArrayList<String>();
				EntityRecogtion er = new EntityRecogtion();
				result=er.entityrecognize(text);
				System.out.println();
			}
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}//end try
	}	//end while
}