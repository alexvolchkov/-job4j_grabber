package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.TriggerBuilder.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class AlertRabbit {

    public static void main(String[] args) {
        try (Connection cn = init()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException se) {
            se.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getInterval() {
        Properties config = new Properties();
        try (FileReader in = new FileReader("src/main/resources/rabbit.properties")) {
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.parseInt(config.getProperty("rabbit.interval"));
    }

    private static Connection init() {
        Connection cn = null;
        try (FileReader fr = new FileReader("src/main/resources/rabbit.properties")) {
             Properties config = new Properties();
             config.load(fr);
            Class.forName(config.getProperty("jdbc.driver"));
            cn = DriverManager.getConnection(
                            config.getProperty("jdbc.url"),
                            config.getProperty("jdbc.username"),
                            config.getProperty("jdbc.password"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cn;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) jobExecutionContext.
                    getJobDetail().getJobDataMap().get("connect");
            try (PreparedStatement ps = connection.prepareStatement(
                         "Insert into rabbit (created_date) values(?)")
            ) {
                    ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
