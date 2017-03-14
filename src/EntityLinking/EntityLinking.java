package EntityLinking;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Candidate.Candidate;
import Deal.Maxprlist;
import Deal.Normalize;
import Deal.PreDeal;
import PageRank.Graph;
import PageRank.MatrixGraph;
import PageRank.PageRank;
import PageRank.ToMatrix;
import Recognition.EntityRecogtion;
import Relation.Relations;

/**
 * 实体链接算法的入口类
 * @author zhongyun
 *
 * 2017年3月13日
 */
public class EntityLinking
{

	/**
	 * 全局变量 k,候选实体过滤阀值
	 * 权重变量theta,相似比较阀值
	 */
	public final int k = 5;
	public final double theta = 0.1;

	/**
	 * 求出每个实体指称项对应的实体对象
	 * @param text:输入文本语料
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public HashMap<String,String> get_True_Object(String text) throws UnsupportedEncodingException{

		/**
		 * 存储链接结果
		 */
		HashMap<String,String> result = new HashMap<String,String>();
		Normalize nor = new Normalize();
		Maxprlist ma = new Maxprlist();
		PageRank PR=new PageRank();
		ToMatrix tm=new ToMatrix();

		/**
		 * 实体识别
		 */
		EntityRecogtion er = new EntityRecogtion();
		List<String> entitylist = er.entityrecognize(text);  //实体指称项列表
		//System.out.println(entitylist);

		/**
		 * 获取候选实体
		 */
		Candidate can = new Candidate();
		can.createDb();
		HashMap<String,Double> can_hash = can.getCandidate(entitylist, text);
		//System.out.println("候选集合:"+can_hash);

		/**
		 * 对候选实体进行过滤，保证候选实体集合的大小（K=5）
		 * 如果全部过滤掉，则就认为该候选实体指称项为空集
		 * 需要把同一实体指称项对应的所有候选实体聚集在一起排序
		 */
		PreDeal pd = new PreDeal();
		HashMap<String,Double> can_tmp_hash = new HashMap<String,Double>(); //存储候选实体相似度的中间值
		for(int i =0; i<entitylist.size();i++){
			String mention = entitylist.get(i);
			HashMap<String,Double> tmp_hash = new HashMap<String,Double>(); //用于存储同一实体指称项的所有候选实体对象

			/**
			 * 求同一实体指称项的所有实体
			 */
			for(String key:can_hash.keySet()){
				if(mention.equals(pd.getMen(key))){
					tmp_hash.put(key,can_hash.get(key));
				}
			}

			//System.out.println("中间值:"+tmp_hash);

			/**
			 * 如果是NIL实体
			 */
			if (tmp_hash.size()==1){
				for(String tkey:tmp_hash.keySet()){
					if(tmp_hash.get(tkey)==0.0){
						result.put(mention, "NIL");
					}
				}
				//continue;
			} //end if

			/**
			 * 候选实体排序
			 */
			HashMap<String,Double> rank_hash = sort(tmp_hash);			
			//System.out.println("排列值:"+rank_hash);

			if(rank_hash.size()==0){
				result.put(mention, "NIL");
				continue;
			}
			
			/**
			 * 文本中只有一个实体指称项
			 */
			if(entitylist.size()==1&&rank_hash.size()>0){
				double max = 0;
				String true_can = null;
				for(String key:rank_hash.keySet()){
					if(rank_hash.get(key)>max){
						max = rank_hash.get(key);
						true_can = pd.getCan(key);
					}
				}
				
				result.put(mention,true_can);
				
			} //end if
			
			can_tmp_hash.putAll(rank_hash);
		}//end for
		System.out.println("Simlary:"+can_tmp_hash);

		/**
		 * 计算候选实体相关度
		 */
		Relations re = new Relations();
		HashMap<String,List<String>> rel_list = can.getAllRelationEntity(entitylist);
		//System.out.println(rel_list);
		HashMap<String,List<String>> rel_tmp_list = new HashMap<String,List<String>>();

		/**
		 * 仅保留那些没有被过滤掉的候选实体
		 */
		for(String key:rel_list.keySet()){
			if(can_tmp_hash.keySet().contains(key)){
				rel_tmp_list.put(key, rel_list.get(key));
			}
		}
		HashMap<String, HashMap<String,Double>> rel_hash= re.getRelValue(rel_tmp_list);
		System.out.println("Relations_value:"+rel_hash);
		//can.shutDown();

		/**
		 * 构建实体相关图
		 */
		if(can_tmp_hash.size()>0&&entitylist.size()>1){
			List<String>graphnode=new ArrayList<String>();//图节点集合(数值)
			List<String>backuplist=new ArrayList<String>();
			List<String>backuplist1=new ArrayList<String>();
			List<Double>priorlist=new ArrayList<Double>();//用于存储实体的先验概率，文章中就用相似度代替
			List<Double>Iconlist=new ArrayList<Double>();//存储初始化矩阵U以及初始化置信度值
			for(String key:can_tmp_hash.keySet()){
				graphnode.add(key);
				backuplist.add(key);
				backuplist1.add(key);
				Iconlist.add(can_tmp_hash.get(key));
				priorlist.add(can_tmp_hash.get(key));//就用文本相似度代替先验置信度
			}

			/**
			 * 对节点的先验置信度进行归一化
			 */
			List<Double>norlist=nor.normalize(entitylist, graphnode, priorlist);
			System.out.println("归一化先验置信度:"+norlist);

			/**
			 * 建立图顶点和顶点间的关系
			 */
			String[] nodearray = (String[])graphnode.toArray(new String[0]);
			Graph graph = new MatrixGraph(nodearray);
			for(String nkey:rel_hash.keySet()){
				HashMap<String,Double>inhash= rel_hash.get(nkey);
				// System.out.println("当前节点"+nodekey1);
				for(String inkey:inhash.keySet()){
					/*
					 * 满足的条件是不是同一实体指称项的候选，且之间不存在边,且满足文本的距离限制
					 */
					if(!graph.exist(nkey, inkey)){
						graph.addEdge(nkey, inkey, inhash.get(inkey));
					}//end if
				}//end for
			}//end for  

			/**
			 * 在图上使用PageRank算法计算每个节点的可信度
			 */
			String regraph=graph.printGraph();
			//System.out.println("实体相关图矩阵:\n"+regraph);
			List<List<Double>>weight=tm.listToDouble(nodearray, regraph); //权重矩阵
			List<Double> Crlist=PR.calPageRank(weight,Iconlist,norlist,PR.ALPHA); //运行PR算法得到节点的可信度
			PR.printVec(Crlist);

			//得到每个实体指向项候选实体中可信度最高的候选实体
			HashMap<String,Double>Maxprhash=ma.Max(entitylist, graphnode, Crlist);
			List<Double> Maxprlist=new ArrayList<Double>();
			for(String key:Maxprhash.keySet()){
				Maxprlist.add(Maxprhash.get(key));
			}//end for

			/**
			 * 计算候选实体与输入文本的语义一致性
			 * 选择每个实体指称项中可信度最大的候选实体代表输入文本
			 * 可信度的加权和
			 */
			List<List<Double>>wlist=new ArrayList<List<Double>>();
			for(int i=0;i<graphnode.size();i++){ 	
				List<Double>w=new ArrayList<Double>();
				for(String key:Maxprhash.keySet()){
					int j=graphnode.indexOf(key);
					w.add(weight.get(i).get(j));
				}//end for 
				wlist.add(w);	 
			} //end for  graphnode 
			List<Double>Doc_rel=PR.vectorMulMatrix(wlist, Maxprlist); //候选实体与输入文本的语义相关性

			/**
			 * 计算每个候选实体与对应实体指称项的语义一致性
			 */
			HashMap<String,Double>SCC=new HashMap<String,Double>();
			// System.out.println(graphnode);
			for(int i=0;i<Doc_rel.size();i++){
				String nodename=graphnode.get(i);
				double scon=Crlist.get(i)+Doc_rel.get(i);
				SCC.put(nodename, scon);
			}
			System.out.println("语义一致性SCC:"+SCC);

			/**
			 * 集成实体链接
			 * 选择每个实体指称项对应候选集合中语义一致性最大的候选实体作为链接对象
			 */
			for(int i=0;i<entitylist.size();i++){
				String mention=entitylist.get(i);
				HashMap<String,Double>tmp_hash=new HashMap<String,Double>();
				double total=0.0;
				for(String key:SCC.keySet()){
					//获取实体指称项对应的候选实体的语义一致性
					if(mention.equals(pd.getMen(key))){
						tmp_hash.put(pd.getCan(key), SCC.get(key));
						//计算同一实体指称项所对应候选实体的语义一致性之和
						total+=SCC.get(key); 
					}
				}//end for SCC

				/**
				 * 如果只有一个候选实体
				 */
				if(tmp_hash.size()==1){
					for(String tkey:tmp_hash.keySet()){
						if(tmp_hash.get(tkey)!=0.0){
							result.put(mention,tkey);
						}//end if 
						else if(tmp_hash.get(tkey)==0.0){
							result.put(mention, "NIL");
						}
					}//end for
				}//end if

				/**
				 * 如果有多个候选实体，需要排序
				 * 选择语义一致性最大的，且大于设定阀值的
				 */
				if(tmp_hash.size()>1){
					String ne=null;
					double max=0.0;
					for(String tkey:tmp_hash.keySet()){
						double sc=tmp_hash.get(tkey)/total;
						//  System.out.println(sc);
						if(sc>=max){
							max=sc;
							ne=tkey;
						}//end if
					} //end for

					if(ne.length()==0){
						result.put(mention, "NIL"); 
					}
					else{
						result.put(mention,ne); 
					} 
				}//end if
			}//end for

		} //end can_tmp_hash'if 

		System.out.println("实体链接结果:"+result);
		can.shutDown();
		return result;
	}

	/**
	 * 用于对候选实体的相似度进行排序，只保留大于theta的前k个实体指称项
	 * @param hash
	 * @return
	 */
	public HashMap<String,Double> sort(HashMap<String,Double>hash){
		//System.out.println("hash:"+hash);
		HashMap<String,Double>subhash=new HashMap<String,Double>();
		List<String> keyList = new ArrayList<String>();  
		keyList.addAll(hash.keySet());  
		List<Double> valueList = new ArrayList<Double>();  
		valueList.addAll(hash.values());  
		if(hash.size()==1){
			if(valueList.get(0)>=theta)
				subhash.put(keyList.get(0), valueList.get(0));
		}
		else if(hash.size()>1){
			for(int i=0; i<valueList.size(); i++)  
				for(int j=i+1; j<valueList.size(); j++) {  
					if(valueList.get(j)>valueList.get(i)) {  
						Double tmp=valueList.get(j);
						valueList.set(j, valueList.get(i));  
						valueList.set(i, tmp);  
						//同样调整对应的key值  
						String kk=keyList.get(j);
						keyList.set(j, keyList.get(i));  
						keyList.set(i, kk);   
					}  
				}
			if(valueList.size()>k-1){
				for (int i=0;i<k;i++) {
					if(valueList.get(i)>=theta){
						subhash.put(keyList.get(i), valueList.get(i));
					}
				}//end for
			}//end if
			else{
				for (int i=0;i<valueList.size();i++){
					if(valueList.get(i)>=theta){
						subhash.put(keyList.get(i), valueList.get(i));
					} 
				}
			}//end for
		}//end else	
		return subhash;
	}	

	/**
	 * 主函数，也是整个算法的入口函数
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		/**
		 * 测试用例1：在你们心中，科比和勒布朗谁厉害？
		 * 测试用例2：李娜的妈妈李艳萍是全国劳动模范。
		 * 测试用例3：吴越,1976年4月10日出生,大陆女演员,1997年凭借电视剧《和平年代》获得第17届金鹰奖优秀女配角。
		 * 测试用例4：李娜和刘翔都是体坛的著名人名,都拿过奥运会冠军。
		 * 测试用例5：科比和奥尼尔是湖人队历史上两个最伟大的篮球运动员。
		 */
		String text = "科比和奥尼尔是湖人队历史上两个最伟大的篮球运动员。"; 
		EntityLinking el= new EntityLinking();
		long startTime=System.currentTimeMillis();   //获取开始时间
		el.get_True_Object(text);
		//HashMap<String,String> entity_linking_result = el.get_True_Object(text);
		//System.out.println(entity_linking_result);
		long endTime=System.currentTimeMillis(); //获取结束时间
		double minute=(endTime-startTime)/1000.0;
		System.out.println("程序运行时间： "+minute+"s");
	}
}
