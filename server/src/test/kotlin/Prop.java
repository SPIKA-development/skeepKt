import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
public class Prop {
    public static void main(String[] args) throws Exception {
        Iterator<String> iterator = Arrays.stream(args).iterator();
        if (!iterator.hasNext()) return;
        String fileName = iterator.next();
        File file = new File(fileName);
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        while (true) {
            if (!iterator.hasNext()) break;
            String key = iterator.next();
            if (!iterator.hasNext()) break;
            String value = iterator.next();
            props.setProperty(key, value);
        }
        props.store(new FileOutputStream(file), null);
    }
}