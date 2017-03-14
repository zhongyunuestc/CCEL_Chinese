package Recognition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dic {
   public HashMap<String,String>dic;

  public HashMap<String, String> getDic() {
	return dic;
  }
  public Dic(){
	  setDic();
  }
  public void setDic() {
   HashMap<String,String>dic=new HashMap<String,String>();
   String text = null;
   try
	{
		FileInputStream fin = new FileInputStream("Dict/entity_map.txt");
		BufferedReader innet = new BufferedReader(
				new InputStreamReader(fin));
		while ((text = innet.readLine()) != null)
		{
			String map[] = text.split("	");
			dic.put(map[0], map[1]);
		}
		fin.close();
	} catch (IOException e)
	{
		e.printStackTrace();
	}// end try
	 
	this.dic = dic;
}
  public static void main(String[] args) {
	  Dic d=new Dic ();
	  System.out.println(d.getDic());
	  
} 
  
}
