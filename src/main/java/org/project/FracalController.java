package org.project;

import org.project.core.Template;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class FracalController {
    private final DataInfo dataInfo;

    public FracalController(DataInfo dataInfo) {
        this.dataInfo = dataInfo;
    }

    public String fractalData(Request req, Response res){
        Map<String, Object> model = new HashMap<>();
        model.put("dataInfo", dataInfo.getXmax());
        return Template.render("fractal.html",model);
    }
}