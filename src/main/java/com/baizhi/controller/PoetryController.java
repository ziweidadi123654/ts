package com.baizhi.controller;

import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import com.baizhi.service.PoertyService;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/p")
public class PoetryController {

    @Autowired
    private PoertyService poertyService;

//    public String queryPoerty(String name, HttpServletRequest request) throws IOException {
//        List<Poetry> poetries = poertyService.queryPoetryShow(name);
//        request.setAttribute("poertyList",poetries);
//        return null;
//    }

    @RequestMapping("/poerty")
    public String sreachPoerty(String text, HttpServletRequest request, Integer pageIndex) throws IOException, ParseException, InvalidTokenOffsetsException {
        //指定索引的存储位置
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("G:\\index\\05"));
        //创建索引读入器对象
        IndexReader indexReader = DirectoryReader.open(fsDirectory);
        //创建检索器对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //查询解析器对象，解析查询表达式
        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        //声明查询对象
        Query query = null;
        //用唐诗作者查询唐诗
        //query = queryParser.parse("author:" + text);
        query  = queryParser.parse("content:"+text+" OR author:"+text);
        //做分页数据,声明每页显示条数
        int pageSize = 10;
        //声明每页要查的数据对象
        TopDocs topDocs = null;
        //如果当前页数是为空或者0，就指定页数为第一页
        if(pageIndex == null || pageIndex ==0){
            pageIndex = 1;
        }
        //如果说当前页数是第一页就展示查到的数据
        if(pageIndex == 1 || pageIndex < 1){
            topDocs = indexSearcher.search(query, pageSize);
            //如果不是第一页，就必须先获取上一页的最后一条数据
        }else if(pageIndex > 1){
            topDocs = indexSearcher.search(query, (pageIndex - 1) * pageSize);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            ScoreDoc scoreDoc = scoreDocs[scoreDocs.length - 1];
            topDocs = indexSearcher.searchAfter(scoreDoc, query, pageSize);
        }
        //查询到的总条数
        int counts = topDocs.totalHits;
        //页面总页数
        int pageCounts = 0;

        if(counts % pageSize == 0){
            pageCounts = counts / pageSize;
        }else {
            pageCounts = counts / pageSize + 1;
        }
        // 创建高亮器对象
        Scorer scorer = new QueryScorer(query);
        // 使用自定义的高亮样式
        Formatter formatter = new SimpleHTMLFormatter("<span style=\"color:red\">","</span>");
        Highlighter highlighter = new Highlighter(formatter,scorer);
        List<Poetry> poetries = new ArrayList<>();
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            //根据doc获取单个对象
            Document document = indexReader.document(doc);
            //定义高亮样式
            String highlighterBestFragment1 = highlighter.getBestFragment(new IKAnalyzer(), "author", document.get("author"));
            String highlighterBestFragment2 = highlighter.getBestFragment(new IKAnalyzer(), "content", document.get("content"));
            //获取诗人与诗歌对象
            Poetry poetry = new Poetry();
            Poet poet = new Poet();
            if(highlighterBestFragment1!=null){
                poet.setName(highlighterBestFragment1);
            }else{
                poet.setName(highlighterBestFragment2);
            }
            String title = document.get("title");
            if(highlighterBestFragment2!=null){
                poetry.setContent(highlighterBestFragment2);
            }else{
                poetry.setContent(document.get("content"));
            }
            poetry.setTitle(title);
            poetry.setPoet(poet);
            poetries.add(poetry);
        }
        //将获取到的数据存入对象
//            String title = document.get("title");
//            String content = document.get("content");
//            poetry.setTitle(title);
//            poetry.setContent(content);
//            poet.setName(highlighterBestFragment);
//            poetry.setPoet(poet);
//            poetries.add(poetry);
        request.setAttribute("poetries",poetries);
        request.setAttribute("counts",counts);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("pageCounts", pageCounts);
        request.setAttribute("text",text);
        indexReader.close();
        return "sreachAll";
    }
}
