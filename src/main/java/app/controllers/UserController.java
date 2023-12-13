package app.controllers;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.models.Material;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.UserMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController
{
    User user;
    public static void login(Context ctx, ConnectionPool connectionPool)
    {

        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try
        {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("frontpage.html");


        }
        catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    public static void createuser(Context ctx, ConnectionPool connectionPool)
    {
        String name = ctx.formParam("name");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String adresse = ctx.formParam("adresse");
        String email = ctx.formParam("email");
        String city = ctx.formParam("city");
        int zip = Integer.parseInt(ctx.formParam("zip"));


        // Validering af passwords - at de to matcher
        if (password1.equals(password2))
        {
            try
            {
                UserMapper.createuser(name, password1, adresse, email, city, zip, connectionPool);
                ctx.attribute("message", "Du er nu oprettet. Log på for at komme i gang.");
                ctx.render("frontpage.html");

            }
            catch (DatabaseException e)
            {
                ctx.attribute("message", e.getMessage());
                ctx.render("createuser.html");
            }
        } else
        {
            ctx.attribute("message", "Dine password matcher ikke!");
            ctx.render("createuser.html");
        }

    }
    //Checks if the user is logged in
    public static boolean checkUserLoggedIn(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null;
    }
    public static void logout(Context ctx)
    {
        // Invalidate session
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
