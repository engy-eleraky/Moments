package com.example.admin.moments.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ADMIN on 3/1/2018.
 */

public class Coding {
    public String code;
    public String id;
    public Coding(){

    }
    public Coding(String code,String id){
        this.code=code;
        this.id=id;
    }

   public Map<String,Object> toMap(){

       HashMap<String,Object> result=new HashMap<>();
       result.put("code",code);
       result.put("id",id);

       return result;
   }
}
