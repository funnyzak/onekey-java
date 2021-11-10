package com.github.funnyzak.onekey.web.config;

import org.nutz.ioc.impl.PropertiesProxy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class ConfigLoader extends PropertiesProxy {

	@PostConstruct
	public void init() {
		this.setIgnoreResourceNotFound(true);
		this.setPaths("application.properties", "config.properties");
	}

}
