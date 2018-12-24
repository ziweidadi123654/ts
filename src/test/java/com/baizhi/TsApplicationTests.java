package com.baizhi;

import com.baizhi.entity.Poetry;
import com.baizhi.service.PoertyService;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TsApplicationTests {

    @Autowired
    private PoertyService poertyService;

    @Test
    public void createIndex() throws IOException {
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("G:\\index\\05"));
        IndexWriter indexWriter = new IndexWriter(fsDirectory, new IndexWriterConfig(new IKAnalyzer()));
        List<Poetry> poetries = poertyService.queryAll();
        Document document = null;
        for (Poetry poetry : poetries) {
            document = new Document();
            document.add(new IntField("id", poetry.getId(), Field.Store.YES));
            document.add(new StringField("author", poetry.getPoet().getName(), Field.Store.YES));
            document.add(new TextField("title", poetry.getTitle(), Field.Store.YES));
            document.add(new TextField("content",poetry.getContent(), Field.Store.YES));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
    }
}

