/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deep.in.spring.cloud;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTask
public class SpringCloudTaskSimpleApplication {

    private final Logger logger = LoggerFactory.getLogger(SpringCloudTaskSimpleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudTaskSimpleApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sleep.time:10000}")
    private Long sleepTime;

    private String[] fileLocation = new String[] {
        //"spring-cloud/spring-cloud-netflix",
        "alibaba/spring-cloud-alibaba"
    };

    @Bean
    @Order(50)
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.info("sleepTime: " + sleepTime);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " +
                "GITHUB_REPO ( " +
                "repo_name varchar(50), commits int, " +
                "issues int, forks int, releases int)");
        };
    }

    @Bean
    @Order(100)
    CommandLineRunner runner() {
        return (args) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Arrays.stream(fileLocation).forEach(githubRepo -> {
                int releases = calculate(
                    "https://api.github.com/repos/" + githubRepo + "/releases?page={page}&per_page=100", 1);
                int commits = calculate(
                    "https://api.github.com/repos/" + githubRepo + "/commits?page={page}&per_page=100", 1);
                int forks = calculate("https://api.github.com/repos/" + githubRepo + "/forks?page={page}&per_page=100",
                    1);
                int issues = calculate(
                    "https://api.github.com/repos/" + githubRepo + "/issues?page={page}&per_page=100", 1);
                jdbcTemplate.update(
                    "insert into GITHUB_REPO(repo_name, commits, issues, forks, releases) values ( ?,?,?,?,? )",
                    new PreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement) throws SQLException {
                            preparedStatement.setString(1, githubRepo);
                            preparedStatement.setInt(2, commits);
                            preparedStatement.setInt(3, issues);
                            preparedStatement.setInt(4, forks);
                            preparedStatement.setInt(5, releases);
                        }
                    });
            });
        };
    }

    private int calculate(String url, int page) {
        String realUrl = url.replace("{page}", String.valueOf(page));
        int realPage = page;
        List<Object> releaseResult = restTemplate.getForObject(realUrl, List.class);
        int num = releaseResult.size(), totalNum = releaseResult.size();
        logger.info("calculate [" + num + "]from [" + realUrl + "]");
        num = releaseResult.size();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // ignore
            return -1;
        }
        while (num != 0) {
            realPage++;
            realUrl = url.replace("{page}", String.valueOf(realPage));
            releaseResult = restTemplate.getForObject(realUrl, List.class);
            totalNum = totalNum + releaseResult.size();
            logger.info("calculate [" + realUrl + "]from [" + realUrl + "], totalNum is " + totalNum);
            num = releaseResult.size();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // ignore
                return -1;
            }
        }
        return totalNum;
    }
}