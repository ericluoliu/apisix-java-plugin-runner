package org.apache.apisix.plugin.runner.filter;

import com.google.gson.Gson;
import org.apache.apisix.plugin.runner.HttpRequest;
import org.apache.apisix.plugin.runner.HttpResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StopRequestDemoFilter implements PluginFilter {
    @Override
    public String name() {
        return "StopRequestDemoFilter";
    }

    @Override
    public void filter(HttpRequest request, HttpResponse response, PluginFilterChain chain) {
        /*
         * If the conf you configured is of type json, you can convert it to Map or json.
         */

        String configStr = request.getConfig(this);
        Gson gson = new Gson();
        Map<String, Object> conf = new HashMap<>();
        conf = gson.fromJson(configStr, conf.getClass());

        /*
         * You can use the parameters in the configuration.
         */
        response.setStatusCode(Double.valueOf(conf.get("stop_response_code").toString()).intValue());
        response.setHeader((String) conf.get("stop_response_header_name"), (String) conf.get("stop_response_header_value"));
        /* note: The body is currently a string type.
                 If you need the json type, you need to escape the json content here.
                 For example, if the body is set as below
                 "{\"key1\":\"value1\",\"key2\":2}"
                 The body received by the client will be as below
                 {"key1":"value1","key2":2}
         */
        response.setBody((String) conf.get("stop_response_body"));

        /*  Using the above code, the client side receives the following
            header:
            HTTP/1.1 401 Unauthorized
            Content-Type: text/plain; charset=utf-8
            Connection: keep-alive
            new-header: header_by_runner
            Server: APISIX/2.6
            body:
            {"key1":"value1","key2":2}
         */
        chain.filter(request, response);
    }

    @Override
    public List<String> requiredVars() {
        return null;
    }

    @Override
    public Boolean requiredBody() {
        return null;
    }
}