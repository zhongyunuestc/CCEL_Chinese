package Candidate;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import Similary.TextSimilar;
import Similary.TextProcessing;

/**
 * 候选生成模块
 * @author zhongyun
 *
 * 2017年3月13日
 */
public class Candidate {
	private static final String DB_PATH = "E:/graduate/all_ontology1.0.db";//知识库名称和地址路径
	public String greeting;
	static GraphDatabaseService graphDb;
	Relationship relationship;



	//定义关系类型为ISA关系
	public static enum RelTypes implements RelationshipType{
		ISA
	}

	/**
	 * 通过tag索引找到同名实体节点
	 * 输入：tag[String]
	 * 输出：同名(tag)节点列表[ArrayList<Node>]
	 * 注明必须在建立数据库的时候建立索引才有有效
	 */

	public List<Node> getNodesByTag(String tag){
		List<Node> nodesList = new ArrayList<Node>();
		Transaction tx = graphDb.beginTx();
		try {

			IndexManager indexManager = graphDb.index();
			Index<Node>  entity_index = indexManager.forNodes( "entity_index" );
			//通过索引查找
			IndexHits<Node> hits = entity_index.get("tag",tag);
			IndexHits<Node> hits1 = entity_index.get("tag1",tag);
			//  IndexHits<Node> hits2 = entity_index.get("tag2",tag);
			while(hits.hasNext())
			{
				Node entity = hits.next();
				nodesList.add(entity);
			}

			while(hits1.hasNext())
			{
				Node entity = hits1.next();
				nodesList.add(entity);
			}

			tx.success();
		} finally {
			tx.finish();
		}
		return nodesList;
	}

	//获取候选实体 list是候选列表，text是输入文本语料
	public HashMap<String,Double>getCandidate(List<String> list,String text) throws UnsupportedEncodingException{
		Transaction tx = graphDb.beginTx();//连接知识库
		HashMap <String,Double> simhash = new HashMap<String,Double>();
		TextProcessing tp = new TextProcessing();
		TextSimilar ts=new TextSimilar();
		List<String>input_list = tp.SegmentProcess(text);
		for(int i = 0; i<list.size();i++){
			List<Node> candidatenode=getNodesByTag(list.get(i));
			//System.out.println(candidatenode.size());
			if(candidatenode.size() == 0 ){
				String name = "<"+list.get(i)+",NIL"+">";
				simhash.put(name, 0.0);
				continue;
			}
			for(int j =0; j<candidatenode.size();j++){
				List<String>can_des_list=new ArrayList<String>();//存储候选实体描述文本的分词结果
				String nodename=null;
				Node candidate= candidatenode.get(j);
				//System.out.println(j);
				String abs=null;  //存储候选实体描述文本
				double sim = 0; //存储输入文本与候选实体描述文本间的余弦相似度
				if(candidate.hasProperty("name")){
					nodename=candidate.getProperty("name").toString();//存储节点名字
					if(nodename.equals(list.get(i))&&candidatenode.size()>1){
						continue;
					}
					//System.out.println(nodename);
				}//end if
				if(candidate.hasProperty("ABSTRACT")){
					abs=candidate.getProperty("ABSTRACT").toString();
					//System.out.println(abs);
					can_des_list = tp.SegmentProcess(abs);
				}

				//属性信息
				Iterator<String>ProIt=candidate.getPropertyKeys().iterator();
				while(ProIt.hasNext()){
					String pronode=ProIt.next(); 
					String value=candidate.getProperty(pronode).toString();
					//System.out.println(pronode+ "  "+value);
					if(!value.equals(abs)&&!can_des_list.contains(value)&&!pronode.equals("URI")&&!pronode.equals("tag")&&!pronode.equals("URL")&&!pronode.equals("name")&&!pronode.equals("中文名")){
						if(value.length()<10){
							can_des_list.add(value);
						}
						else{
							List<String>v_list = tp.SegmentProcess(value);
							can_des_list = intersection(can_des_list,v_list);
						}
					}//end if
				} //end while

				//出链关系
				Iterator<Relationship>out_RelIt=candidate.getRelationships(Direction.OUTGOING).iterator();
				while(out_RelIt.hasNext()){
					Relationship re=out_RelIt.next();
					String Relname=re.getType().name();
					//System.out.println(Relname);
					if(!Relname.equals("国籍")){
						Node neibo=re.getEndNode();
						String value="";
						if(neibo.hasProperty("name")){
							value=neibo.getProperty("name").toString();
						}
						if(value.length()<10){
							can_des_list.add(value);
						}
						else{
							List<String>v_list = tp.SegmentProcess(value);
							can_des_list = intersection(can_des_list,v_list);
						}
					}//end if
				}//end while

				//入链关系
				Iterator<Relationship>in_RelIt=candidate.getRelationships(Direction.INCOMING).iterator();
				while(in_RelIt.hasNext()){
					Relationship re=in_RelIt.next();
					String Relname=re.getType().name();
					//System.out.println(Relname);
					if(!Relname.equals("国籍")){
						Node neibo=re.getStartNode();
						String value="";
						if(neibo.hasProperty("name")){
							value=neibo.getProperty("name").toString();
						}
						if(value.length()<10){
							can_des_list.add(value);
						}
						else{
							List<String>v_list = tp.SegmentProcess(value);
							can_des_list = intersection(can_des_list,v_list);
						}
					}//end if
				}//end while

				//System.out.println(can_des_list);
				//计算相似度大小
				sim = ts.compute_list(input_list, can_des_list);
				if (sim<0.2){
					sim = (sim+0.2)/2;
				}

				String name = "<"+list.get(i)+","+nodename+">";
				simhash.put(name, sim);	
			}// end for
		} //end for
		return simhash;
	}

	/**
	 * 获取所有由关系的候选实体
	 * @param list
	 * @return
	 */
	public HashMap<String,List<String>> getAllRelationEntity(List<String>list){
		Transaction tx = graphDb.beginTx();//连接知识库
		HashMap <String,List<String>> relhash = new HashMap<String,List<String>>();
		for(int i = 0; i<list.size();i++){
			List<Node> candidatenode=getNodesByTag(list.get(i));
			//System.out.println(candidatenode.size());
			for(int j =0; j<candidatenode.size();j++){
				List<String>rel_list=new ArrayList<String>();//存储候选实体描述文本的分词结果
				List<String>rel_final_list=new ArrayList<String>();//存储候选实体描述文本的分词结果
				String nodename=null;
				Node candidate= candidatenode.get(j);
				if(candidate.hasProperty("name")){
					nodename=candidate.getProperty("name").toString();//存储节点名字
					if(nodename.equals(list.get(i))&&candidatenode.size()>1){
						continue;
					}
					//System.out.println(nodename);
				}//end if

				//出链关系
				Iterator<Relationship>out_RelIt=candidate.getRelationships(Direction.OUTGOING).iterator();
				while(out_RelIt.hasNext()){
					Relationship re=out_RelIt.next();
					String Relname=re.getType().name();
					Node neibo=re.getEndNode();
					String value="";
					if(neibo.hasProperty("name")){
						value=neibo.getProperty("name").toString();
					}
					//System.out.println(Relname+ " "+ value);
					rel_list.add(value);
				}//end while

				//入链关系
				Iterator<Relationship>in_RelIt=candidate.getRelationships(Direction.INCOMING).iterator();
				while(in_RelIt.hasNext()){
					Relationship re=in_RelIt.next();
					String Relname=re.getType().name();
					
					Node neibo=re.getStartNode();
					String value="";
					if(neibo.hasProperty("name")){
						value=neibo.getProperty("name").toString();
					}
					//System.out.println(Relname+ " "+ value);
					rel_list.add(value);
					
				}//end while

				rel_final_list = (ArrayList<String>)removeDuplicateWithOrder(rel_list);
				//System.out.println(rel_final_list);
				String name = "<"+list.get(i)+","+nodename+">";
				relhash.put(name, rel_list);	

			}// end for
		} //end for
		return relhash;
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


	/**
	 * 删除无效的节点,也就是那些没有任何信息的点，没有name属性，没有relation
	 * 输入：无
	 * 输出：无
	 */
	public void deleteInvalidNodes(String mention) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);//启动数据库
		Transaction tx = graphDb.beginTx();//连接知识库
		List<Node> candidatenode=getNodesByTag(mention);
		Node node=null;
		for(int i=0;i<candidatenode.size();i++){
			String nodename=null;
			Node candidate= candidatenode.get(i);

			if(candidate.hasProperty("name")){
				nodename=candidate.getProperty("name").toString();//存储节点名字
				if(nodename.equals(mention)){
					node=candidate;
				}
			}
		}
		//System.out.println(node.getProperty("name").toString());
		for(int i=0;i<candidatenode.size();i++){
			String nodename=null;
			Node candidate= candidatenode.get(i);
			String abs=null;
			if(candidate.hasProperty("name")){
				nodename=candidate.getProperty("name").toString();//存储节点名字
				if(nodename.contains("　　")||nodename.contains("。")){
					Iterator<Relationship>RelIt=candidate.getRelationships().iterator(); 
					while(RelIt.hasNext()){
						Relationship re=RelIt.next();
						String Relname=re.getType().name();
						//System.out.println(Relname);
						Node[] neibo=re.getNodes();
						for(int j=0;j<neibo.length;j++)
						{	String value="";
						if(neibo[j].hasProperty("name")){
							value=neibo[j].getProperty("name").toString();
						}//end if 
						relationship = neibo[j].createRelationshipTo(node,
								DynamicRelationshipType.withName(Relname));
						}
					}//end while
					candidate.delete();
				}//end if 
			}//end if
		}//end for	
		System.out.println("非法节点删除完成");
	}

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);//启动数据库
		long startTime=System.currentTimeMillis();   //获取开始时间
		Candidate neo=new Candidate();
		String mention="洛杉矶湖人队";
		String str = "乔丹是NBA历史上最伟大的篮球运动员之一。";
		System.out.println(neo.getCandidate(Arrays.asList(mention,"李娜"),str));
		System.out.println(neo.getAllRelationEntity(Arrays.asList(mention,"李娜")));
		neo.shutDown();
		long endTime=System.currentTimeMillis(); //获取结束时间
		double minute=(endTime-startTime)/1000.0;
		System.out.println("程序运行时间： "+minute+"s");
	}

	/*
	 * 打开数据库中国
	 */
	public void createDb() {
		// deleteFileOrDirectory(new File(DB_PATH));
		// START SNIPPET: startDb
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);//打开数据库
		// GraphDatabaseService graphDb=new
		// RestGraphDatabase("http://localhost:7474/db/data");
		registerShutdownHook(graphDb);

	}
	/*
	 * 断开数据库
	 */
	public void shutDown() {
		//System.out.println("Shutting down database ...");
		// START SNIPPET: shutdownServer
		graphDb.shutdown();
		// END SNIPPET: shutdownServer
	}
	// START SNIPPET: shutdownHook
	static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}
