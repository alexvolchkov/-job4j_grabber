package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;

public class SqlRuParse {

    public static void main(String[] args) throws IOException {
        String url = "https://www.sql.ru/forum/job-offers/";
        for (int i = 1; i <= 5; i++) {
            Document doc = Jsoup.connect(String.format("%s%s", url, i)).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(td.parent().child(5).text());
            }
        }
    }

    public static Post parsePage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element parent = doc.getElementsByClass("msgTable").get(0);
        String title = parent
                .getElementsByClass("messageHeader")
                .get(0)
                .childNode(1)
                .toString().trim().split("&nbsp;")[1];
        String description = parent.getElementsByClass("msgBody").get(1).text();
        LocalDateTime created = new SqlRuDateTimeParser().parse(
                parent.getElementsByClass("msgBody").get(1).text());
        return new Post(title, description, created);
    }
}
