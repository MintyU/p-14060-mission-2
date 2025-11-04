package com.back;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        System.out.println("== 명언 앱 ==");
        int id = 0;
        List<Quote> quotes = new ArrayList<>();
        String filePath = "db/wiseSaying/";
        id = FileUtil.getDataFromFile(filePath, quotes);

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("명령) ");
            String command = sc.nextLine();
            String[] parts;
            switch (command) {
                case "등록":
                    ++id;
                    System.out.print("명언: ");
                    String quoteText = sc.nextLine();
                    System.out.print("작가: ");
                    String author = sc.nextLine();
                    Quote data = new Quote(id, quoteText, author);
                    quotes.add(data);
                    FileUtil.saveDataToFile(filePath, data);
                    System.out.println(id + "번 명언이 등록되었습니다.");
                    continue;
                case "목록":
                    System.out.println("번호 / 작가 / 명언");
                    System.out.println("---------------------");
                    for (int i = quotes.size() - 1; i >= 0; --i) {
                        Quote q = quotes.get(i);
                        System.out.println(q.getId() + " / " + q.getAuthor() + " / " + q.getQuote());
                    }
                    continue;
                case "종료":
                    System.out.println("명언 앱을 종료합니다.");
                    return;
                case String cmd when cmd.startsWith("삭제"):
                    parts = command.split("\\?");
                    if (parts.length >= 2 && parts[1].startsWith("id=")) {
                        String idPart = parts[1].substring(3);

                        int deleteId;
                        try {
                            deleteId = Integer.parseInt(idPart);
                        } catch (NumberFormatException var16) {
                            System.out.println("올바른 ID가 아닙니다.");
                            continue;
                        }

                        boolean found = false;

                        for (int i = 0; i < quotes.size(); ++i) {
                            if ((quotes.get(i)).getId() == deleteId) {
                                quotes.remove(i);
                                System.out.println(deleteId + "번 명언이 삭제되었습니다.");
                                FileUtil.deleteDataFile(filePath, deleteId);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            System.out.println(deleteId + "번 명언은 존재하지 않습니다.");
                        }
                        continue;
                    }

                    System.out.println("올바른 형식이 아닙니다. 예: 삭제?id=1");
                    continue;
                case String cmd when cmd.startsWith("수정"):
                    parts = command.split("\\?");
                    if (parts.length >= 2 && parts[1].startsWith("id=")) {
                        String idPart = parts[1].substring(3);

                        int modifyId;
                        try {
                            modifyId = Integer.parseInt(idPart);
                        } catch (NumberFormatException var15) {
                            System.out.println("올바른 ID가 아닙니다.");
                            continue;
                        }

                        Quote quoteToModify = null;

                        for (Quote q : quotes) {
                            if (q.getId() == modifyId) {
                                quoteToModify = q;
                                break;
                            }
                        }

                        if (quoteToModify == null) {
                            System.out.println(modifyId + "번 명언은 존재하지 않습니다.");
                        } else {
                            System.out.println("명언(기존): " + quoteToModify.getQuote());
                            System.out.print("명언: ");
                            String newQuoteText = sc.nextLine();
                            System.out.println("작가(기존): " + quoteToModify.getAuthor());
                            System.out.print("작가: ");
                            String newAuthor = sc.nextLine();
                            quoteToModify.setQuote(newQuoteText);
                            quoteToModify.setAuthor(newAuthor);
                            System.out.println(modifyId + "번 명언이 수정되었습니다.");
                            FileUtil.saveDataToFile(filePath, quoteToModify);
                        }
                        continue;
                    }

                    System.out.println("올바른 형식이 아닙니다. 예: 수정?id=1");
                    continue;
                case "빌드":
                    FileUtil.buildData(filePath, quotes);
                    System.out.println("data.json 파일의 내용이 갱신되었습니다.");
                    continue;


                default:
                    System.out.println("올바른 명령어가 아닙니다.");
            }
        }
    }
}

class FileUtil {
    // 프로그램 최초 실행 시 파일에서 데이터를 읽어오는 메서드
    public static int getDataFromFile(String filePath, List<Quote> quotes) {
        int lastId = 0;
        Scanner s1 = null;
        try {
            s1 = new Scanner(new File(filePath + "lastId.txt"));
        } catch (IOException e) {
            return lastId;
        }

        if (s1.hasNextLine()) {
            String line = s1.nextLine();
            lastId = Integer.parseInt(line);
        }
        s1.close();

        for (int i = 1; i <= lastId; ++i) {
            Scanner s2 = null;
            try {
                s2 = new Scanner(new File(filePath + i + ".json"));
            } catch (IOException e) {
                continue;
            }

            if (s2.hasNextLine()) {
                String line = s2.nextLine();
                String[] parts = line.replace("{", "").replace("}", "").replace("\"", "").split(", ");
                int id = 0;
                String quoteText = "";
                String author = "";
                for (String part : parts) {
                    String[] keyValue = part.split(": ");
                    switch (keyValue[0]) {
                        case "id":
                            id = Integer.parseInt(keyValue[1]);
                            break;
                        case "quote":
                            quoteText = keyValue[1];
                            break;
                        case "author":
                            author = keyValue[1];
                            break;
                    }
                }
                Quote quote = new Quote(id, quoteText, author);
                quotes.add(quote);
            }
            s2.close();
        }


        return lastId;
    }

    // 내용 수정, 추가 시 파일에 데이터를 저장하는 메서드
    public static void saveDataToFile(String filePath, Quote quote) {
        FileWriter fw1 = null;
        try {
            fw1 = new FileWriter(filePath + quote.getId() + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fw1.write("{\"id\": " + quote.getId() + ", \"quote\": \"" + quote.getQuote() + "\", \"author\": \"" + quote.getAuthor() + "\"}");
            fw1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileWriter fw2 = null;
        try {
            fw2 = new FileWriter(filePath + "lastId.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fw2.write(String.valueOf(quote.getId()));
            fw2.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 빌드 시 파일에 모든 데이터를 저장하는 메서드
    public static void buildData(String filePath, List<Quote> quotes) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath + "data.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fw.write("[\n");
            for (int i = 0; i < quotes.size(); ++i) {
                Quote q = quotes.get(i);
                fw.write("  {\"id\": " + q.getId() + ", \"quote\": \"" + q.getQuote() + "\", \"author\": \"" + q.getAuthor() + "\"}");
                if (i < quotes.size() - 1) {
                    fw.write(",\n");
                } else {
                    fw.write("\n");
                }
            }
            fw.write("]");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    // 삭제 시 해당 데이터 파일을 삭제하는 메서드
    public static void deleteDataFile(String filePath, int id) {
        File file = new File(filePath + id + ".json");
        if (file.exists()) {
            file.delete();
        }
    }
}