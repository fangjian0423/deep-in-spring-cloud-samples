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

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.spi.app.AppScaleRequest;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.app.DeploymentState;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.local.LocalAppDeployer;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalTaskLauncher;
import org.springframework.cloud.deployer.spi.task.LaunchState;
import org.springframework.cloud.deployer.spi.task.TaskStatus;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class SpringCloudDeployerLocalApplication {

    public static void main(String[] args) {
        //taskLaunch();
        appDeploy();
    }

    private static void appDeploy() {
        LocalAppDeployer deployer = new LocalAppDeployer(new LocalDeployerProperties());
        String deploymentId = deployer.deploy(createAppDeploymentRequest());
        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // ignore
            }
            AppStatus status = deployer.status(deploymentId);
            System.out.println("app status: " + status);
            if (status.getState() == DeploymentState.deployed) {
                System.out.println("app is deployed");
                break;
            }
        }
        deployer.scale(new AppScaleRequest(deploymentId, 2, createNacosProperties(8081)));
        System.out.println("app will be cancel after 30s");
        try {
            Thread.sleep(30000L);
            deployer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void taskLaunch() {
        LocalTaskLauncher launcher = new LocalTaskLauncher(new LocalDeployerProperties());
        String launchId = launcher.launch(createAppDeploymentRequest());
        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // ignore
            }
            TaskStatus status = launcher.status(launchId);
            System.out.println("task status: " + status);
            if (status.getState() == LaunchState.running) {
                System.out.println("task is running");
                break;
            }
        }
        System.out.println("task will be cancel after 30s");
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            // ignore
        }
        launcher.cancel(launchId);
    }

    private static AppDeploymentRequest createAppDeploymentRequest() {
        MavenResource resource = new MavenResource.Builder()
            .artifactId("spring-cloud-alibaba-nacos-provider")
            .groupId("deep.in.spring.cloud")
            .version("0.0.1-SNAPSHOT")
            .build();
        AppDefinition definition = new AppDefinition("nacos-provider", createNacosProperties(8080));
        AppDeploymentRequest request = new AppDeploymentRequest(definition, resource);
        return request;
    }

    private static Map<String, String> createNacosProperties(int port) {
        Map<String, String> properties = new HashMap<>();
        properties.put("server.port", String.valueOf(port));
        properties.put("spring.application.name", "spring-cloud-deployer-provider");
        properties.put("spring.cloud.nacos.discovery.server-addr", "localhost:8848");
        return properties;
    }

}
