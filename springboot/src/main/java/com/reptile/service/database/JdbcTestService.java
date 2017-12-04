package com.reptile.service.database;

import com.reptile.dao.MapperInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JdbcTestService {
    @Autowired
    MapperInterface map;
    public void test() {
        System.out.println("12312312313");
        List<String> someOne = map.getSomeOne("1");
        System.out.println(someOne);
    }
}
