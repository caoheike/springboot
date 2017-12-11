package com.reptile.dao;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MapperInterface{

    void postOne(String sql);

    void deleteOne(String id);

    void putOne(String id);

    List<String> getSomeOne(String id);

}
