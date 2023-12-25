package com.github.vultr;

import com.github.vultr.core.helper.SpringBeanHelper;
import com.github.vultr.core.windows.MainFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;


@SpringBootApplication
@ComponentScan("com.github.vultr.core")
public class VultrApplication implements ApplicationRunner {


    @Autowired
    private SpringBeanHelper springBeanHelper;

    private static MainFrame mainFrame = new MainFrame();

    public static void main(String[] args) {
        SpringApplication.run(VultrApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        springBeanHelper.injection(mainFrame);
        mainFrame.setVisible(true);
        mainFrame.refreshList();
    }
}
