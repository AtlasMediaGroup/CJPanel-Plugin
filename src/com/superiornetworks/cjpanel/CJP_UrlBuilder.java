package com.superiornetworks.cjpanel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;


public class CJP_UrlBuilder
{
     public static class URLBuilder
    {

        private final String requestPath;
        private final Map<String, String> queryStringMap = new HashMap<>();

        public URLBuilder(String requestPath)
        {
            this.requestPath = requestPath;
        }

        public URLBuilder addQueryParameter(String key, String value)
        {
            queryStringMap.put(key, value);
            return this;
        }

        public URL getURL() throws MalformedURLException
        {
            List<String> pairs = new ArrayList<>();
            Iterator<Map.Entry<String, String>> it = queryStringMap.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<String, String> pair = it.next();
                pairs.add(pair.getKey() + "=" + pair.getValue());
            }

            return new URL(requestPath + "?" + StringUtils.join(pairs, "&"));
        }
    }
}
