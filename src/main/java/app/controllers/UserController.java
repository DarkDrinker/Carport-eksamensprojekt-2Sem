package app.controllers;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.http.Context;


public class UserController {

    //Simple login function, that checks against DB to match any user. and set them at currentUser
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

    //Create a user function
    public static void createuser(Context ctx, ConnectionPool connectionPool) {
            //pulls info from the form.
            String name = ctx.formParam("name");
            String password1 = ctx.formParam("password1");
            String password2 = ctx.formParam("password2");
            String adresse = ctx.formParam("adresse");
            String email = ctx.formParam("email");
            String city = ctx.formParam("city");
            int zip = Integer.parseInt(ctx.formParam("zip"));

            //Checks against the DB to either create or spit out error
            if (password1.equals(password2)) {
                try {
                    UserMapper.createuser(name, password1, adresse, email, city, zip, connectionPool);

                    ctx.render("login.html");
                    ctx.sessionAttribute("message", "Du er nu oprettet. Log på for at komme i gang.");
                } catch (DatabaseException e) {
                    ctx.sessionAttribute("message", e.getMessage());
                    ctx.render("createuser.html");
                }
            } else {
                ctx.sessionAttribute("message", "Dine passwords matcher ikke!");
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
