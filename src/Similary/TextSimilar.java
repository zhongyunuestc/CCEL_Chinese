package Similary;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class TextSimilar {
	public double compute(String text1,String text2){
		List<String>list = new ArrayList<String>();
		TextProcessing tp = new TextProcessing();
		List<String> list1 = null;
		List<String> list2 = null;
		try
		{
			list1 = tp.SegmentProcess(text1);
			list2 = tp.SegmentProcess(text2);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TextProcessing Tp=new TextProcessing();
		int k=0;
		String[] array1 = (String[])list1.toArray(new String[0]);
		String[] array2 = (String[])list2.toArray(new String[0]);
		//	String[] array2 = (String[])Tp.selectKey(list2).toArray(new String[0]);
		//    	System.out.println(list1);
		//	    System.out.println(list2);
		//		list.addAll(list1);
		//		list.addAll(list2);
		//选择网页中的关键字
		//  list=Tp.selectKey(list2);
		//System.out.println(list1.get(0)=="");
		list=(ArrayList<String>)removeDuplicateWithOrder(list1);
		//  System.out.println(list);
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
		double sim =0.0;
		if(Math.sqrt(denominator1)*Math.sqrt(denominator2)!=0){
			sim = numerator/(Math.sqrt(denominator1)*Math.sqrt(denominator2)); //余弦相似度
		} 
		//double sim = numerator/(Math.sqrt(denominator1)+Math.sqrt(denominator2)-numerator);//jacard相似度
		//System.out.println(sim);	
		return sim;
	}


	public double compute_list(List<String> list1,List<String> list2){
		List<String>list = new ArrayList<String>();
		int k=0;
		String[] array1 = (String[])list1.toArray(new String[0]);
		String[] array2 = (String[])list2.toArray(new String[0]);
		//	String[] array2 = (String[])Tp.selectKey(list2).toArray(new String[0]);
		//    	System.out.println(list1);
		//	    System.out.println(list2);
		//		list.addAll(list1);
		//		list.addAll(list2);
		//选择网页中的关键字
		//  list=Tp.selectKey(list2);
		//System.out.println(list1.get(0)=="");
		list=(ArrayList<String>)removeDuplicateWithOrder(list1);
		//  System.out.println(list);
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
		double sim =0.0;
		if(Math.sqrt(denominator1)*Math.sqrt(denominator2)!=0){
			sim = numerator/(Math.sqrt(denominator1)*Math.sqrt(denominator2)); //余弦相似度
		} 
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
		//System.out.println(list);
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		} 
		//System.out.println(newList);
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

	/**
	 * 求并集
	 * @param mention
	 * @return
	 */
	public List<String> intersection(List<String> list1, List<String> list2){
		List<String> list3 = new ArrayList<String>();
		list3.addAll(list1);
		list3.removeAll(list2);
		list3.addAll(list2);
		return list3;
	}
	public static void main(String args[]) throws IOException{
		List<String> list1 = Arrays.asList("1","2","3");
		List<String> list2 = Arrays.asList("3","4","5");
		TextSimilar ts=new TextSimilar();
		list1 = ts.intersection(list1, list2);
		System.out.println(list1);

	}
}
