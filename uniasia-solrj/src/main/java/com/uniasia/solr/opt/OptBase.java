package com.uniasia.solr.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class OptBase {
	
	private static final int SO_TIMEOUT = 10000;
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int MAX_PER_HOST = 5000;
	private static final int MAX_CONNECTIONS = 200;
	
	public static HttpSolrServer createServer(String solrUrl) {
		HttpSolrServer server = null;
		if (server == null) {
			server = new HttpSolrServer(solrUrl);
			server.setSoTimeout(SO_TIMEOUT);
			server.setConnectionTimeout(CONNECTION_TIMEOUT);
			server.setDefaultMaxConnectionsPerHost(MAX_PER_HOST);
			server.setMaxTotalConnections(MAX_CONNECTIONS);
			server.setFollowRedirects(false);
			server.setAllowCompression(true);
			server.setMaxRetries(1);
			// 提升性能采用流输出方式
			server.setRequestWriter(new BinaryRequestWriter());
		}
		return server;
	}
	
	public  List<Map<String, Object>> convertDocumentList(
			SolrDocumentList docList,Map<String,Map<String, List<String>>>  map) {
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;
		for (SolrDocument doc : docList) {
			item = new HashMap<String, Object>();
			for (String fieldName : doc.getFieldNames()) {
				item.put(fieldName, doc.getFieldValue(fieldName));
			}
			//将高亮字段放入map中
			if(map!=null){
				item.put("light", map.get(doc.get("id")));
			}
			resList.add(item);
		}
		return resList;
	}
}
