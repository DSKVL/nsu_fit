package ru.nsu.fit.project20202.midjira;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:parameters.properties")
@ComponentScan
public class Context {

    @Bean(initMethod="setUpReportTask")
    @Scope("singleton")
    public MidpointTaskLauncher midpointTaskLauncher() {
        return new MidpointTaskLauncher();
    }

    @Bean
    @Scope("singleton")
    public JiraSurveyor jiraSurveyer() {
        return new JiraSurveyor(midpointTaskLauncher());
    }

    @Bean
    @Scope("singleton")
    public JiraReportAttacher jiraReportAttacher() {
        return new JiraReportAttacher();
    }
}
