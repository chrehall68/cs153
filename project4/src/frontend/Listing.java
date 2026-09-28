package frontend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Listing {
    BufferedReader br;

    public Listing(String sourceFileName) throws IOException {
        br = new BufferedReader(new FileReader(sourceFileName));
    }

    public void print() throws IOException {
        int lineNumber = 0;
        String line = br.readLine();

        while (line != null) {
            System.out.printf("%03d %s\n", ++lineNumber, line);
            line = br.readLine();
        }

        br.close();
    }
}
