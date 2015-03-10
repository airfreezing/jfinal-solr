First you need create this plugin class.
public class SolrPlugin implements IPlugin {
	private String[][] servers;

	public SolrPlugin(String[][] servers) {
		this.servers = servers;
	}

	@Override
	public boolean start() {
		try {
			SolrServerFactory factory = SolrServerFactory.getInstance();
			factory.loadingServers(servers);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean stop() {
		return false;
	}

}

Then add this plugin in JFinalConfig.configPlugin

		String[][] servers = {
		{"collection1Search", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_SEARCH},
		{"collection1Update", "http://192.168.1.99:8080/solr-4.8.0/collection1",SolrServerFactory.SOLR_UPDATE}};
		me.add(new SolrPlugin(servers));
You can use it in your Controller like this:
	        SolrSearcher search = SolrServerFactory.getInstance().getSolrSearcher("collection1Search");
		String keywords = getPara("keywords");
		String qParam ="foodName:"+keywords;
		List<Map<String,Object>> res = search.searchByParams(0,100,qParam,null,new String[]{"_version_ desc"},"foodName");
