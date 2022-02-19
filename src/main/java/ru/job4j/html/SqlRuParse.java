package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

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

    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element parent = doc.getElementsByClass("msgTable").get(0);
        String title = parent
                .getElementsByClass("messageHeader")
                .get(0)
                .childNode(1)
                .toString().trim().split("&nbsp;")[1];
        String description = parent.getElementsByClass("msgBody").get(1).text();
        LocalDateTime created = dateTimeParser.parse(
                parent.getElementsByClass("msgFooter").get(0)
                        .childNode(0).toString().trim().split("&")[0]);
        return new Post(title, link, description, created);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = doc.select(".postslisttopic");
        row.forEach(el -> rsl.add(detail(el.child(0).attr("href"))));
        return rsl;
    }

}
