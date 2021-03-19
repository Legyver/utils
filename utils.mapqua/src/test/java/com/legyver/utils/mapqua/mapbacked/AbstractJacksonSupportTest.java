package com.legyver.utils.mapqua.mapbacked;

import com.legyver.core.exception.CoreException;
import com.legyver.utils.jackiso.JacksonObjectMapper;

import java.util.Map;

public class AbstractJacksonSupportTest {

	protected String getJson(Map map) throws CoreException {
		return JacksonObjectMapper.INSTANCE.writeValueAsString(map);
	}
}
