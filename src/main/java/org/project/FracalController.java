package org.project;

import org.project.core.Template;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class FracalController {
    private final Fractal fractal;

    public FracalController(Fractal fractal) {
        this.fractal = fractal;
    }

    public String fractalData(Request req, Response res){
        Map<String, Object> model = new HashMap<>();
        model.put("fractalValues", fractal.getFractal());
        model.put("width", fractal.getWidth());
        model.put("height", fractal.getHeight());
        return Template.render("fractal.html",model);
    }
}