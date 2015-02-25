package org.dbrain.yaw.http.sample;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by epoitras on 11/09/14.
 */
public class SampleServlet extends HttpServlet {

    public static final String CONTENT = "Hello from sample servlet.";

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        resp.setStatus( 200 );
        resp.setHeader( "Content-Type", "text/plain" );
        resp.getWriter().print( CONTENT );
    }
}
