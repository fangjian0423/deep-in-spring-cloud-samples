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

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${goods.file.name:classpath:turnover.json}")
    private Resource goodsResource;

    @Bean
    public Job bullJob(ItemReader<Goods> reader,
        ItemProcessor<Goods, Bill> itemProcessor, ItemWriter<Bill> writer) {
        Step step = stepBuilderFactory.get("BillProcessing")
            .<Goods, Bill>chunk(2)
            .reader(reader)
            .processor(itemProcessor)
            .writer(writer)
            .build();

        return jobBuilderFactory.get("BillJob")
            .incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }

    @Bean
    public JsonItemReader<Goods> jsonItemReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<Goods> jsonObjectReader =
            new JacksonJsonObjectReader<>(Goods.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Goods>()
            .jsonObjectReader(jsonObjectReader)
            .resource(goodsResource)
            .name("GoodsJsonItemReader")
            .build();
    }

    @Bean
    ItemProcessor itemProcessor() {
        return new BillItemProcessor();
    }

    @Bean
    public ItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
        JdbcBatchItemWriter<Bill> writer = new JdbcBatchItemWriterBuilder<Bill>()
            .beanMapped()
            .dataSource(dataSource)
            .sql("INSERT INTO BILLS (name, amount) VALUES " +
                "(:name, :amount)")
            .build();
        return writer;
    }

    class BillItemProcessor implements ItemProcessor<Goods, Bill> {

        @Override
        public Bill process(Goods item) throws Exception {
            return new Bill(item.getName(), item.getCount() * item.getPrice());
        }
    }

    static class Bill {
        private String name;
        private double amount;

        public Bill() {
        }

        public Bill(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }

    static class Goods {
        private String name;
        private double price;
        private int count;

        public Goods() {
        }

        public Goods(String name, double price, int count) {
            this.name = name;
            this.price = price;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

}
