package com.baizhi.service;

import com.baizhi.entity.Poetry;

import java.io.IOException;
import java.util.List;

public interface PoertyService {

    List<Poetry> queryAll();

    //根据诗人查询
    List<Poetry> queryPoetryShow(String name) throws IOException;

}
