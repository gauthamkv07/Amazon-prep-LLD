import java.util.*;

abstract class FileSystemNode {
    String name;

    public FileSystemNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class File extends FileSystemNode {
    String content;

    public File(String name) {
        super(name);
        this.content = "";
    }

    public void addContent(String content) {
        this.content += content;
    }

    public String getContent() {
        return content;
    }
}

class Dir extends FileSystemNode {
    TreeMap<String, FileSystemNode> children;  // Stores both files and dirs

    public Dir(String name) {
        super(name);
        children = new TreeMap<>();
    }

    public void addNode(FileSystemNode node) {
        children.put(node.getName(), node);
    }

    public TreeMap<String, FileSystemNode> getChildren() {
        return children;
    }
}

public class FileSystem {
    Dir root;

    public FileSystem() {
        root = new Dir("/");
    }

    public List<String> ls(String path) {
        FileSystemNode node = goToNode(path);
        List<String> result = new ArrayList<>();

        if (node instanceof File) {
            result.add(node.getName());
        } else {
            Dir dir = (Dir) node;
            result.addAll(dir.getChildren().keySet());
        }

        Collections.sort(result);
        return result;
    }

    public void mkdir(String path) {
        String[] parts = path.split("/");
        Dir curr = root;

        for (int i = 1; i < parts.length; i++) {
            if (!curr.children.containsKey(parts[i])) {
                curr.children.put(parts[i], new Dir(parts[i]));
            }
            curr = (Dir) curr.children.get(parts[i]);
        }
    }

    public void addContentToFile(String filePath, String content) {
        String[] parts = filePath.split("/");
        Dir curr = root;

        for (int i = 1; i < parts.length - 1; i++) {
            curr = (Dir) curr.children.get(parts[i]);
        }

        String fileName = parts[parts.length - 1];
        if (!curr.children.containsKey(fileName)) {
            curr.children.put(fileName, new File(fileName));
        }

        File file = (File) curr.children.get(fileName);
        file.addContent(content);
    }

    public String readContentFromFile(String filePath) {
        File file = (File) goToNode(filePath);
        return file.getContent();
    }

    private FileSystemNode goToNode(String path) {
        String[] parts = path.split("/");
        FileSystemNode curr = root;

        for (int i = 1; i < parts.length; i++) {
            curr = ((Dir) curr).getChildren().get(parts[i]);
        }

        return curr;
    }
}
