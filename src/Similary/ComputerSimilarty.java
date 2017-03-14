package Similary;
import java.util.*; 

public class ComputerSimilarty {
	  
	public double compute(List<String>list1,List<String>list2){
		List<String>list=new ArrayList<String>();
		int k=0;
		String[] array1 = (String[])list1.toArray(new String[0]);
		String[] array2 = (String[])list2.toArray(new String[0]);
//		System.out.println(list1);
//		System.out.println(list2);
		list.addAll(list1);
		list.addAll(list2);
		//删除WordList中相同元素
		list=(ArrayList<String>)removeDuplicateWithOrder(list);
		//System.out.println(list);
		//遍历合并后的list1
		int[] wordNum1 = new int[list.size()];//初始化里面的值全是0
		int[] wordNum2 = new int[list.size()];
		ListIterator<String> It=list.listIterator();
		while(It.hasNext()){
			String word = It.next();
			for(int m=0;m<array1.length;m++){
				if(word.equals(array1[m]))
					wordNum1[k]++;
			}
			for(int m=0;m<array2.length;m++){
				if(word.equals(array2[m]))
					wordNum2[k]++;
			}
			k++;
		}
		int numerator = 0;
		int denominator1 = 0;
		int denominator2 = 0;
		for(int m=0;m<list.size();m++){
			numerator+=wordNum1[m]*wordNum2[m];
			denominator1+=Math.pow(wordNum1[m],2);
			denominator2+=Math.pow(wordNum2[m],2);
			}
		double sim = numerator/(Math.sqrt(denominator1)*Math.sqrt(denominator2)); //余弦相似度
		//double sim = numerator/(Math.sqrt(denominator1)+Math.sqrt(denominator2)-numerator);//jacard相似度
		//System.out.println(sim);	
		return sim;
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
	/*
	 * 找最大数
	 */
	 public double findmax(Double array[]){
		 double max=array[0];
		  for(int i=0;i<array.length;i++){
			 if(array[i]>max) max=array[i];
		  }
		 return max;
	 }
	public static void main(String arg[]){
//		ExtractBaiduInfo info=new ExtractBaiduInfo();
		
	    ComputerSimilarty cs=new ComputerSimilarty();
	    List<String>result1=new ArrayList<String>();//存储实体指称项的属性
        List<String>result2=new ArrayList<String>();//存储候选实体的属性
        List<String>result3=new ArrayList<String>();//存储实体指称项的属性
        List<String>result4=new ArrayList<String>();//存储候选实体的属性
        
        result1.add("陈金[体育舞蹈运动员]");
        result1.add("男");
        result1.add("中国");
        result1.add("体育舞蹈运动员");
        result2.add("陈金");
        result2.add("现代");
        result2.add("男");
        result3.add("亚洲");
        result3.add("运动会");
        result3.add("亚运会");
        result3.add("人物");
        result3.add("广州");
        result4.add("亚洲");
        result4.add("运动会");
        result4.add("亚洲");
        result4.add("舞蹈");
        result4.add("亚运会");
        result4.add("人物");
        result4.add("中国");
        System.out.println(0.6*cs.compute(result1, result2)+0.4*cs.compute(result3, result4));
//        result2.add("");
//        result2.add("");
//        result2.add("");
////	    List<String>result=info.extratinfo("E:/graduate/hudong/5.txt");
////		List<String>result1=ex.extraInfobox("E:/graduate/hudong/李娜[网球运动员].txt");
////		List<String>result2=ex.extraInfobox("E:/graduate/hudong/1.txt");
////		List<String>result3=ex.extraInfobox("E:/graduate/hudong/2.txt");
////		List<String>result4=ex.extraInfobox("E:/graduate/hudong/3.txt");
////		List<String>result5=ex.extraInfobox("E:/graduate/hudong/4.txt");
//		double d1=cs.compute(result, result2);
//		double d2=cs.compute(result, result3);
//		double d3=cs.compute(result, result4);
//		double d4=cs.compute(result, result5);
//		ArrayList<Double> numlist=new ArrayList<Double>();
//	    numlist.add(d1);
//		numlist.add(d2);
//		numlist.add(d3);
//		numlist.add(d4);
//		//System.out.println(numlist);
//		Double[] array= (Double[])numlist.toArray(new Double[0]);
//		System.out.println("李娜与文档1的相似度:"+d1);
//		System.out.println("李娜与文档2的相似度:"+d2);
//	    System.out.println("李娜与文档3的相似度:"+d3);
//		System.out.println("李娜与文档4的相似度:"+d4);
//		System.out.println("最大的相似度值为:"+cs.findmax(array));
	}

}
