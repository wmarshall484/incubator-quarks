/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2015,2016 
*/
package quarks.tests.connectors.wsclient.javax.websocket;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KeystorePath {
    
    public static String getStorePath(String storeLeaf) {
        String pathStr = System.getProperty("user.dir");
        // Under eclipse/junit: path to project in repo: <repo>/connectors
        // Under ant/junit: <repo>/connectors/<project>/unittests/testrunxxxxxxx
        // Get the path to the <repo>/connectors
        Path path = new File(pathStr).toPath();
        do {
            if (path.endsWith("connectors"))
                break;
            path = path.getParent();
        } while (path != null);
        path = path.resolve(Paths.get("wsclient-javax.websocket",
                            "src", "test", "keystores", storeLeaf));
        if (!path.toFile().exists())
            throw new IllegalArgumentException("File does not exist: "+path);
        return path.toString();
    }

}
