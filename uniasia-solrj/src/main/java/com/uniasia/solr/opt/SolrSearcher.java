package com.uniasia.solr.opt;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.uniasia.solr.Page;

public class SolrSearcher extends OptBase{
	private HttpSolrServer server;	
	
	public SolrSearcher(String solrUrl) {
		this.server = createServer(solrUrl);
	}

	/**
	 * 根据参数查询
	 * @param start
	 * @param rows
	 * @param q     一次过滤条件
	 * @param fq    二次过滤条件
	 * @param sort  排序
	 * @param light 高亮的字段,逗号分割
	 * @return
	 */
	public List<Map<String, Object>> searchByParams(Integer start,Integer rows,String q,String[] fq,String[] sort,String light) {
		try{ 	        
	        SolrQuery solrQuery = new SolrQuery("*:*");
	        solrQuery.set("qt", "/select"); 
	        solrQuery.set("q",q);
	        solrQuery.set("fq",fq);
	        solrQuery.set("sort",sort);
	        if(light!=null){
		        solrQuery.setHighlight(true);
		        solrQuery.addHighlightField(light);// 高亮字段
		        solrQuery.setHighlightSimplePre("<font color='red'>");//设置开头
		        solrQuery.setHighlightSimplePost("</font>");//设置行数
		        solrQuery.setHighlightFragsize(100);  
		        solrQuery.setStart(start);
		        solrQuery.setRows(rows);  	 
	        }
	        
	        QueryResponse resp = server.query(solrQuery);  
	        SolrDocumentList docList = resp.getResults();  
	        //获取高亮的字段
	        Map<String,Map<String, List<String>>>  map = null;
	        if(light!=null){
	        	map = resp.getHighlighting(); 
	        }
	        
			return convertDocumentList(docList,map);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * 根据参数查询
	 * @param start
	 * @param rows
	 * @param q     一次过滤条件
	 * @param fq    二次过滤条件
	 * @param sort  排序
	 * @param light 高亮的字段,逗号分割
	 * @return
	 */
	public Page<Map<String, Object>> searchForPage(Integer pageNumber,Integer pageSize,String q,String[] fq,String[] sort,String light) {
		try{ 	        
	        SolrQuery solrQuery = new SolrQuery("*:*");
	        solrQuery.set("qt", "/select"); 
	        solrQuery.set("q",q);
	        solrQuery.set("fq",fq);
	        solrQuery.set("sort",sort);
	        if(light!=null){
		        solrQuery.setHighlight(true);
		        solrQuery.addHighlightField(light);// 高亮字段
		        solrQuery.setHighlightSimplePre("<font color='red'>");
		        solrQuery.setHighlightSimplePost("</font>");
		        solrQuery.setHighlightFragsize(100);  		        	 
	        }
	        solrQuery.setStart((pageNumber-1)*pageSize);
	        solrQuery.setRows(pageSize); 
	        QueryResponse resp = server.query(solrQuery);  
	        SolrDocumentList docList = resp.getResults();  
	        Long totalRow = docList.getNumFound();
	        //获取高亮的字段
	        Map<String,Map<String, List<String>>>  map = null;
	        if(light!=null){
	        	map = resp.getHighlighting(); 
	        }
	        int totalPage = (totalRow.intValue()  +  pageSize  - 1) / pageSize;  
			return new Page<Map<String,Object>>(convertDocumentList(docList,map), pageNumber, pageSize, totalPage, totalRow.intValue());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}