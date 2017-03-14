package Relation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import Candidate.Candidate;
import Deal.PreDeal;

/**
 * 建立实体间的相关性
 * @author zhongyun
 *
 * 2017年3月13日
 */
public class Relations
{
	/**
	 * 求候选实体顶点间的关系
	 * @param relhash：其key是候选实体名称，value是与之相关的实体列表
	 * @return
	 */
	public HashMap<String, HashMap<String,Double>> getRelValue(HashMap<String,List<String>> relhash){
		HashMap<String, HashMap<String,Double>> rel_value = new HashMap<String, HashMap<String,Double>>();
		PreDeal pd = new PreDeal();
		/**
		 * 遍历关系列表来建立关系，双层循环
		 */
		for(String okey:relhash.keySet()){
			String omen = pd.getMen(okey);
			String ocan = pd.getCan(okey);
			List<String> orel = relhash.get(okey);
			HashMap<String,Double> orel_hash = new HashMap<String,Double>();
			double v_rel = 0;
			for(String ikey:relhash.keySet()){
				String imen = pd.getMen(ikey);
				if(!omen.equals(imen)){
					List<String> irel = relhash.get(ikey);
					String ican = pd.getCan(ikey);
					if(orel.contains(ican)||irel.contains(ocan)){
						v_rel = 1.0;
						//orel_hash.put(ikey, v_rel);
					} //end if-else
					else{
						int number = union(orel,irel)-1;
						//int number = 1;
						if(number>0){
							double e1 = Math.log10(max(orel.size(),irel.size()))-Math.log10(number);
							double e2 = Math.log10(814784)-Math.log10(min(orel.size(),irel.size()));
							v_rel = 1-e1/e2;
							if(v_rel>1){
								v_rel = 1.0;
							}
							if(v_rel<0.0001){
								v_rel =0.0;
							}
						} //end if else
						else{
							v_rel =0.0;
						}
					}//end if-else
					orel_hash.put(ikey, v_rel);
				} 
				else{
					//v_rel =0.0;
					//orel_hash.put(ikey, v_rel);
					continue;
				}//end if-else
			} //end for
			rel_value.put(okey, orel_hash);
		} //end for
		return rel_value;
	}

	/**
	 * 求交集
	 * @param list1
	 * @param list2
	 * @return
	 */
	public int union(List<String> list1,List<String>list2){
		List<String> list = new ArrayList<String>();
		list.addAll(list1);
		list.retainAll(list2);
		return list.size();
	}

	/**
	 * 最大值
	 * @param m
	 * @param n
	 * @return
	 */
	public int max(int m, int n){
		if (m>n) return m;
		else return n;
	}

	/**
	 * 求最小值
	 * @param m
	 * @param n
	 * @return
	 */
	public int min(int m, int n){
		if(m>n) return n;
		else return m;
	}

	/**
	 * 主函数
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException
	{ 
		Candidate can = new Candidate();
		Relations re = new Relations();
		can.createDb();
		long startTime=System.currentTimeMillis();   //获取开始时间
		//String mention="李娜";
		String str = "李娜的妈妈李艳萍是全国劳动模范。";
		System.out.println(can.getCandidate(Arrays.asList("李娜","李艳萍"),str));
		System.out.println(can.getAllRelationEntity(Arrays.asList("李娜","李艳萍")));
		System.out.println(re.getRelValue(can.getAllRelationEntity(Arrays.asList("李娜","李艳萍"))));
		can.shutDown();
		long endTime=System.currentTimeMillis(); //获取结束时间
		double minute=(endTime-startTime)/1000.0;
		System.out.println("程序运行时间： "+minute+"s");
	}
}
