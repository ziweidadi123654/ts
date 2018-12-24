package com.baizhi.service;

import com.baizhi.dao.PoetryDAO;
import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PoetryServiceImpl implements PoertyService {

    @Autowired
    private PoetryDAO poetryDAO;

    @Override
    public List<Poetry> queryAll() {
        return  poetryDAO.findAll();
    }

    @Override
    public List<Poetry> queryPoetryShow(String name) throws IOException {
        IndexReader indexReader = null;
        try {
            FSDirectory fsDirectory = FSDirectory.open(Paths.get("G:\\index\\05"));
            indexReader = DirectoryReader.open(fsDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
            Query query = queryParser.parse(name + "*");

            TopDocs topDocs = indexSearcher.search(query, 1000);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<Poetry> poetries = new ArrayList<>();
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docID = scoreDoc.doc;
                Document document = indexReader.document(docID);
                String content = document.get("content");
                Poetry poetry = new Poetry();

                poetry.setId(Integer.valueOf(document.get("id")));
                poetry.setTitle(document.get("title"));
                poetry.setContent(document.get("content"));
                poetry.setPoet(new Poet(document.get("author")));
                poetries.add(poetry);
            }

            return poetries;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (indexReader != null)
                indexReader.close();

        }
        return null;
    }

}
