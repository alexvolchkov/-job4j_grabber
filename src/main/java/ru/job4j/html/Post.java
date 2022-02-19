package ru.job4j.html;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private int id;
    private String title;
    private String description;
    private LocalDateTime created;

    public Post(String title, String description, LocalDateTime created) {
        this.title = title;
        this.description = description;
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id
                && Objects.equals(title, post.title)
                && Objects.equals(description, post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", created=" + created.format(DTF)
                + '}';
    }
}
