package app.controllers;

import app.exceptions.DatabaseException;
import app.models.Remme;
import app.models.Spaer;
import app.models.Stolper;
import app.persistence.ConnectionPool;
import app.persistence.RemmeMapper;
import app.persistence.SpaerMapper;
import app.persistence.StolperMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderController {


    public static void allRemme(Context ctx, ConnectionPool connectionPool){

        try {
            List<Remme> remmeList =  new ArrayList<>( RemmeMapper.getAllRemme(connectionPool).values());
            ctx.attribute("Remme", remmeList);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void allStolper(Context ctx, ConnectionPool connectionPool){

        try {
            List<Stolper> stolperList =  new ArrayList<>( StolperMapper.getAllStolper(connectionPool).values());
            ctx.attribute("Stolper", stolperList);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void allSpaer(Context ctx, ConnectionPool connectionPool){

        try {
            List<Spaer> spaerList =  new ArrayList<>( SpaerMapper.getAllSpaer(connectionPool).values());
            ctx.attribute("Spaer", spaerList);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

}

