package com.uniasia;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectionPoolTimeoutException;

import com.uniasia.solr.Page;
import com.uniasia.solr.SolrServerFactory;
import com.uniasia.solr.opt.SolrSearcher;
import com.uniasia.solr.opt.SolrUpdate;


public class Test {
	public static void main(String[] args) {
		pageSearch();
		factorySearch();
		commonSearch();
	}
	
	private static int process = 0;
	
	private static void factorySearch(){
		
		final long start = new Date().getTime();
		
		final SolrServerFactory factory = SolrServerFactory.getInstance();
		String[][] servers = {
				{"collection1Search", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_SEARCH},
				{"collection1Update", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_UPDATE}};
				factory.loadingServers(servers);	
		
		
			for(int i=0;i<10000;i++){
				new Thread(new Runnable() {					
				@Override
				public void run() {
					SolrSearcher solrSearcher = factory.getSolrSearcher("collection1Search");
					solrSearcher.searchByParams(0,5,"foodName:超级",null,new String[]{"_version_ desc"},"foodName");
					process++;
					System.out.println("Total cost process:"+process);
					if(process==1000){
						long finish = new Date().getTime();
						System.out.println("Total cost factorySearch:"+(finish-start));
					}
					
				}
				}).start();	
			}
	
	}
	
	private static void commonSearch(){		
		final long start = new Date().getTime();
		
		for(int i=0;i<1000;i++){
			new Thread(new Runnable() {					
				@Override
				public void run() {
				SolrSearcher solrSearcher = new SolrSearcher("http://192.168.1.99:8080/solr-4.8.0/collection1");
				solrSearcher.searchByParams(0,5,"foodName:超级",null,new String[]{"_version_ desc"},"foodName,foodDesc");
				process++;
				System.out.println("Total cost process:"+process);
					if(process==1000){
						long finish = new Date().getTime();
						System.out.println("Total cost commonSearch:"+(finish-start));
					}
				}
			}).start();	
		}
	}
	
	
	private static void pageSearch(){
		final SolrServerFactory factory = SolrServerFactory.getInstance();
		String[][] servers = {
				{"collection1Search", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_SEARCH},
				{"collection1Update", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_UPDATE}};
				factory.loadingServers(servers);		
				SolrSearcher solrSearcher = factory.getSolrSearcher("collection1Search");
				Page<Map<String, Object>> page = solrSearcher.searchForPage(214,5,"foodName:黄瓜",null,new String[]{"_version_ desc"},"foodName");
				System.out.println("共"+page.getTotalRow()+"条记录,分为"+page.getTotalPage()+"页,"+"每页"+page.getPageSize()+"条"+"当前第"+page.getPageNumber()+"页");
			
	}
	
	private static void deleteTest(SolrUpdate solrUpdate){
		solrUpdate.deleteDocumentById("9999999");
	}
	
	//id相同会自动更新
	private static void addOrUpdateTest(SolrUpdate solrUpdate){
		List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", "9999998");
		map2.put("mainIngred", "空间理论");
		map2.put("foodDesc", "美国科幻大片");
		map2.put("foodName", "超级星际穿越2");
		docList.add(map2);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "9999999");
		map.put("mainIngred", "能量");
		map.put("foodDesc", "来自外星球的力量");
		map.put("foodName", "超级赛亚人2");
		docList.add(map);		
		
		solrUpdate.addOrUpdateDocuments(docList);
	}
	
	private static void searchTest(SolrSearcher solrSearcher){
				solrSearcher.searchByParams(0,100,"foodName:超级",null,new String[]{"_version_ desc"},"foodName");
	}
	
	
	
}
