package com.online.search.dao.impl;

import com.online.search.dao.SearchDao;
import com.online.search.pojo.SearchItem;
import com.online.search.pojo.SearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class SearchDaoImpl implements SearchDao {

    @Autowired
    private SolrServer solrServer;

    @Override
    public SearchResult search(SolrQuery solrQuery) throws Exception{
        //执行查询
        QueryResponse response = solrServer.query(solrQuery);
        //获取查询列表
        SolrDocumentList solrDocumentsList = response.getResults();
        List<SearchItem> itemList = new ArrayList<>();
        for(SolrDocument solrDocument : solrDocumentsList){
            SearchItem item = new SearchItem();
            item.setId((String) solrDocument.get("id"));
            item.setCategory_name((String) solrDocument.get("item_category_name"));
            item.setImage((String) solrDocument.get("item_image"));
            item.setPrice((Long) solrDocument.get("item_price"));
            item.setSell_point((String) solrDocument.get("item_sell_point"));
            //设置高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String itemTitle = "";
            if(list != null && list.size()>0){
                //取高亮后的结果
                itemTitle= list.get(0);
            }else{
                itemTitle = (String) solrDocument.get("item_title");
            }

            item.setTitle(itemTitle);
            //添加到列表
            itemList.add(item);
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setItemList(itemList);
        searchResult.setRecordCount(solrDocumentsList.getNumFound());
        return searchResult;
    }
}
