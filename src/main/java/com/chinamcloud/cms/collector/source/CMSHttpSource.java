package com.chinamcloud.cms.collector.source;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.source.http.HTTPBadRequestException;
import org.apache.flume.source.http.HTTPSourceHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMSHttpSource implements HTTPSourceHandler {

    private final Type mapType = new TypeToken<Map<String,Object>>(){}.getType();


    Gson gson = new Gson();


    @Override
    public List<Event> getEvents(HttpServletRequest httpServletRequest) throws HTTPBadRequestException, Exception {
        String charset = httpServletRequest.getCharacterEncoding();
        if (charset == null) {
            charset = "utf-8";
        }
        httpServletRequest.setCharacterEncoding(charset);
        BufferedReader reader = httpServletRequest.getReader();
        Map<String,Object> eventBody = gson.fromJson(reader, mapType);
        String tenant = eventBody.get("tenant").toString();
        if (tenant == null) {
            throw new HTTPBadRequestException("租户信息为空");
        }
        String action = eventBody.get("action").toString();
        if (action == null) {
            throw new HTTPBadRequestException("Action为空");
        }
        Event event = new JSONEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("tenant", tenant);
        headers.put("action", action);
        event.setBody(gson.toJson(eventBody).getBytes());
        List<Event> events = new ArrayList<>(1);
        events.add(event);
        return events;
    }


    @Override
    public void configure(Context context) {

    }
}
