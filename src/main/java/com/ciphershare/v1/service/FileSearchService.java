package com.ciphershare.v1.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ciphershare.v1.entity.FileMetaData;


@Service
public class FileSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private static final String INDEX_PROVIDER = "files";

    private XContentBuilder fileMetadataToXContent(FileMetaData fileMetaData) throws IOException{

        return XContentFactory.jsonBuilder()
                                .startObject()
                                    .field("filemetaDataId",fileMetaData.getFilemetaDataId())
                                    .field("fileName",fileMetaData.getFileName())
                                    .field("uploadedBy",fileMetaData.getUploadedBy())
                                    .field("uploadedAt",fileMetaData.getUploadTime().toString())
                                    .field("storagePath",fileMetaData.getStoragePath())
                                    .field("fileSize",fileMetaData.getFileSize())
                                    .field("fileType",fileMetaData.getFileType())
                                .endObject();
    }
    public void indexFileMetaData(FileMetaData fileMetaData) {

        try{
            XContentBuilder builder = fileMetadataToXContent(fileMetaData);
            IndexRequest request = new IndexRequest(INDEX_PROVIDER)
                                            .id(fileMetaData.getFilemetaDataId().toString())
                                            .source(builder);
            restHighLevelClient.index(request,RequestOptions.DEFAULT);
        }catch(Exception e){
            throw new RuntimeException("Error while indexing file metadata "+e.getMessage());
        }
    }

    public List<FileMetaData> searchFiles(String keyword){
        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_PROVIDER);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.multiMatchQuery(keyword,"fileName","uploadedBy"));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

            List<FileMetaData> results = new ArrayList<>();
            for(SearchHit hit : searchResponse.getHits().getHits()){

                Map<String,Object> map = hit.getSourceAsMap();
                FileMetaData metadata = new FileMetaData(Long.parseLong((String)map.get("filemetaDataId")), (String) map.get("fileName"),(String) map.get("fileType"), null, (String) map.get("storagePath"),(String) map.get("uploadedBy"), LocalDateTime.parse((String) map.get("uploadedAt")));
                results.add(metadata);
            }

            return results;
        } catch (Exception e) {
            throw new RuntimeException("Error while searching files "+e.getMessage());
        }
    }
}
