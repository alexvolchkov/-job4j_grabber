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
        Properties config = getConfig();
        try (Connection cn = init(config)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(config.getProperty("rabbit.interval")))
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Properties getConfig() {
        Properties config = new Properties();
        try (FileReader fr = new FileReader("src/main/resources/rabbit.properties")) {
            config.load(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static Connection init(Properties config) throws ClassNotFoundException, SQLException {
        Class.forName(config.getProperty("jdbc.driver"));
        Connection cn = DriverManager.getConnection(
                            config.getProperty("jdbc.url"),
                            config.getProperty("jdbc.username"),
                            config.getProperty("jdbc.password"));
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
                    ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
