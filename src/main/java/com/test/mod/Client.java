package com.test.mod;

import com.fair.preload.Preloader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {




    // 测试
    public static void main(String[] args) throws Exception {
        Preloader.connect("127.0.0.1", 9999);
        String res = Preloader.sendAndWait("run!");
        System.out.println(res);
        Preloader.disconnect();
    }
}
