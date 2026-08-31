package com.dentalclinic.util;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import org.junit.jupiter.api.Test;

public class DBConnectionTest {

    @Test
    public void testGetConnection() throws Exception {

        Connection conn = DBConnection.getConnection();

        // if we got a connection object back, and it's not closed, the connection worked
        assertNotNull(conn);
        assertFalse(conn.isClosed());

        conn.close();
    }
}