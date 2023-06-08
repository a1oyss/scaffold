package com.chaoxing.scaffold.common.xss.core;

import com.chaoxing.scaffold.common.xss.cleaner.XssCleaner;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class XssStringJsonSerializer extends JsonSerializer<String> {

	private final XssCleaner xssCleaner;

	public XssStringJsonSerializer(XssCleaner xssCleaner) {
		this.xssCleaner = xssCleaner;
	}

	@Override
	public Class<String> handledType() {
		return String.class;
	}

	@Override
	public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (value != null) {
			// 开启 Xss 才进行处理
			if (XssStateHolder.enabled()) {
				value = xssCleaner.clean(value);
			}
			jsonGenerator.writeString(value);
		}
	}

}
