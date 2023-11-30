package app.controllers;

import app.exceptions.DatabaseException;
import app.models.Material;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {


    public static void allMaterial(Context ctx, ConnectionPool connectionPool){

        try {
            List<Material> materialList =  new ArrayList<>( MaterialMapper.getAllMaterial(connectionPool).values());
            ctx.attribute("materialList", materialList);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

}

