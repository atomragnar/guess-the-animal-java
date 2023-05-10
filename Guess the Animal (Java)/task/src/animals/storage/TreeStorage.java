package animals.storage;

import animals.tree.AnimalFactTree;
import animals.tree.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;

public class TreeStorage {

    private String fileName = "animals";
    private ObjectMapper objectMapper;

    public TreeStorage(String type) {
        String lang = System.getProperty("user.language");
        String extension = lang.equals("en") ? "" : "_" + lang;
        this.fileName = fileName + extension + "." + type;
        switch (type) {
            case "yaml" -> objectMapper = new YAMLMapper();
            case "xml" -> objectMapper = new XmlMapper();
            default -> objectMapper = new JsonMapper();
        }
    }


    public void loadTree(AnimalFactTree tree) {
        try {
            File file = new File(fileName);
            tree.setRoot(file.exists()
                    ? objectMapper.readValue(file, Node.class)
                    : new Node());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveTree(AnimalFactTree tree) {
        try {
            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(new File(fileName), tree.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
