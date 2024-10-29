package com.project.shopapp.Exception;

public class DataNotFoundException extends Exception{
    public DataNotFoundException (String message){
        super(message); // super() dùng để gọi constructor không tham s của lớp cha, chỉ được gọi trong constructor
    }
}
