import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SetUp {

    public SetUp(String dataPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataPath));
            String str;
            while ((str = br.readLine()) != null) {
                // TODO read seller and buyer details from a file and use that to create objects.
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
