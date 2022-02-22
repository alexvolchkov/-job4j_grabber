package ru.job4j.gc;

public class User {

    private String name;
    private int age;
    private boolean sex;

    public User(String name, int age, boolean sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.printf("Removed %s%n", name);
    }

    public static void main(String[] args) {
        for (int i = 1; i < 40; i++) {
            new User(String.format("user%d", i),
                    i * i,
                    i % 2 == 0);
        }
    }
}
