package deep.in.spring.cloud;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class EurekaProvider {

	public static void main(String[] args) {
		SpringApplication.run(EurekaProvider.class, args);
	}

    @RestController
    class EchoController {

        @GetMapping("/echo")
        public String echo(HttpServletRequest request) {
            return "echo: " + request.getParameter("name");
        }

    }

}
