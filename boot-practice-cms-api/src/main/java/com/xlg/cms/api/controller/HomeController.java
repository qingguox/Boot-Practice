package com.xlg.cms.api.controller;

import static com.xlg.cms.api.model.Result.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.xlg.cms.api.config.IpConfiguration;

@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    IpConfiguration ip;

    @RequestMapping(value = "/", method = {HEAD, GET, POST})
    public String heathCheck() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("端口号"+address.getHostAddress()+",ip:"+ip.getPort());
        return "ok";
    }

    @RequestMapping(value = "/check", method = {POST, GET, HEAD})
    private Object healthCheck() {
        return ok(Maps.newHashMap());
    }
}