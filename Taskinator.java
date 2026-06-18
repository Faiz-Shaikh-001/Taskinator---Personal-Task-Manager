import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import JsonParser.*;

public class Taskinator {

    public static final String USAGE = """
            Taskinator --- Your Personal Terminal Task Manager

            Usage: java Taskinator.java [options] [task]

            Commands: {
                -a, --add: "Adds a new task",
                -u, --update: "Updates an existing task",
                -d, --delete: "Deletes an existing task",
                -m, --mark: "Update the task status",
                -l, --list: "Lists all tasks",
                -ld, --list-done: "Lists all tasks that are completed",
                -lnd, --list-not-done: "Lists all pending tasks",
                -lp, --list-progress: "Lists all in-progress tasks",
            }

            Example:
                java Taskinator.java -a "Learn Java" OR java Taskinator.java --add "Learn Java"

            """;

    static File fetchFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("An error occured");
        }

        return file;
    }

    static String getContent(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.err.println("An error occured");
        }

        return content.toString();
    }

    static String getDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = today.format(formatter);
        return formattedDate;
    }

    static String getIdFromInput(Map<String, Object> tasks, Scanner scanner) {
        printTaskTable(tasks);
        System.out.print("Enter the task id to update: ");
        String id = scanner.nextLine();
        return id;
    }

    static void printTaskTable(Map<String, Object> tasks) {
        String format = "| %-4s | %-15s | %-30s | %-15s |%n";
        System.out.println("+------+-----------------+--------------------------------+-----------------+");
        System.out.printf(format, "ID", "STATUS", "TASK DESCRIPTION", "CREATED AT");
        System.out.println("+------+-----------------+--------------------------------+-----------------+");

        for (Map.Entry<String, Object> entry : tasks.entrySet()) {
            String id = entry.getKey();
            Object taskData = entry.getValue();

            if (taskData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> innerMap = (Map<String, Object>) taskData;
                String taskDescription = String.valueOf(innerMap.get("task"));
                String progressStatus = String.valueOf(innerMap.get("progress"));
                String createdAt = String.valueOf(innerMap.get("created-at"));
                System.out.printf(format, id, progressStatus, taskDescription, createdAt);
            }
        }
        System.out.println("+------+-----------------+--------------------------------+-----------------+");
    }

    static void updateIds(Map<String, Object> tasks, int startingId) {
        int currentId = startingId;

        while (true) {
            String nextIdStr = String.valueOf(currentId + 1);

            if (!tasks.containsKey(nextIdStr)) {
                break;
            }

            Object taskData = tasks.remove(nextIdStr);

            String currentIdStr = String.valueOf(currentId);
            tasks.put(currentIdStr, taskData);

            currentId++;
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Map<String, Object> tasks;
        String id;
        Object taskContent;
        String filePath = "tasks.json";
        File file = fetchFile(filePath);

        Scanner scanner = new Scanner(System.in);

        String content = getContent(file);
        JsonLexer lexer = new JsonLexer(content.toString());
        List<Token> tokens = lexer.tokenize();
        JsonParser parser = new JsonParser(tokens);
        tasks = parser.parseMap();

        if (args.length < 1) {
            System.out.println(USAGE);
            return;
        }

        String flag = args[0];

        switch (flag) {
            case "-a":
            case "--add":
                try {
                    String index = String.valueOf(tasks.size() + 1);

                    if (args.length < 2) {
                        System.out.println("USAGE: java Taskinator.java -a \"Add message\"");
                        return;
                    }
                    String task = args[1];

                    Map<String, Object> newTask = new HashMap<String, Object>();
                    newTask.put("task", task);
                    newTask.put("progress", "TODO");
                    newTask.put("created-at", getDate());

                    tasks.put(index, newTask);

                    System.out.println("Task added successfully");
                    printTaskTable(tasks);
                } catch (Exception e) {
                    System.err.println("An error occured");
                    e.printStackTrace();
                }
                break;
            case "-u":
            case "--update":
                if (args.length < 2) {
                    System.out.println("USAGE: java Taskinator.java -u \"Update message\"");
                    return;
                }
                String newTask = args[1];
                id = getIdFromInput(tasks, scanner);
                if (!tasks.containsKey(id)) {
                    System.out.println("Error: Task ID " + id + " does not exist.");
                    break;
                }
                taskContent = tasks.get(id);

                if (taskContent instanceof Map) {
                    Map<String, Object> innerMap = (Map<String, Object>) taskContent;

                    innerMap.put("task", newTask);
                }
                System.out.println("Task updated successfully");
                printTaskTable(tasks);
                break;
            case "-d":
            case "--delete":
                id = getIdFromInput(tasks, scanner);

                if (!tasks.containsKey(id)) {
                    System.out.println("Error: Task ID " + id + " does not exist.");
                    break;
                }

                tasks.remove(id);
                int deletedId = Integer.parseInt(id);
                if (deletedId <= (tasks.size() + 1)) {
                    updateIds(tasks, deletedId);
                }
                System.out.println("Task removed successfully");
                printTaskTable(tasks);
                break;
            case "-m":
            case "--mark":
                String updateTo = """
                        Select the option to update to:
                        1. In Progress
                        2. Done
                        """;
                id = getIdFromInput(tasks, scanner);

                if (!tasks.containsKey(id)) {
                    System.out.println("Error: Task ID " + id + " does not exist.");
                    break;
                }

                String[] options = { "In Progress", "Done" };
                System.out.println(updateTo);
                int choice = scanner.nextInt();

                taskContent = tasks.get(id);

                if (taskContent instanceof Map) {
                    Map<String, Object> innerMap = (Map<String, Object>) taskContent;

                    innerMap.put("progress", options[choice - 1]);
                }
                System.out.println("Task progress updated successfully");
                printTaskTable(tasks);
                break;
            case "-l":
            case "--list":
                printTaskTable(tasks);
                break;
            case "-ld":
            case "--list-done":
                Map<String, Object> completedTasks = new HashMap<String, Object>();
                for (String taskId : tasks.keySet()) {
                    taskContent = tasks.get(taskId);
                    if (taskContent instanceof Map) {
                        if (String.valueOf(((Map<String, Object>) taskContent).get("progress")).equals("Done")) {
                            completedTasks.put(taskId, taskContent);
                        }
                    }
                }
                printTaskTable(completedTasks);
                break;
            case "-lnd":
            case "--list-not-done":
                Map<String, Object> incompleteTasks = new HashMap<String, Object>();
                for (String taskId : tasks.keySet()) {
                    taskContent = tasks.get(taskId);
                    if (taskContent instanceof Map) {
                        if (!String.valueOf(((Map<String, Object>) taskContent).get("progress")).equals("Done")) {
                            incompleteTasks.put(taskId, taskContent);
                        }
                    }
                }
                printTaskTable(incompleteTasks);
                break;
            case "-lp":
            case "--list-progress":
                Map<String, Object> inProgressTasks = new HashMap<String, Object>();
                for (String taskId : tasks.keySet()) {
                    taskContent = tasks.get(taskId);
                    if (taskContent instanceof Map) {
                        if (String.valueOf(((Map<String, Object>) taskContent).get("progress")).equals("In Progress")) {
                            inProgressTasks.put(taskId, taskContent);
                        }
                    }
                }
                printTaskTable(inProgressTasks);
                break;
            default:
                System.err.println("Unknown flag: " + flag);
                System.out.println(USAGE);
        }

        try {
            String updatedJson = JsonSerializer.serialize(tasks);
            Files.writeString(Path.of(filePath), updatedJson);
        } catch (IOException e) {
            System.err.println("Failed to write updated file: " + e.getMessage());
        }
        scanner.close();
    }
}