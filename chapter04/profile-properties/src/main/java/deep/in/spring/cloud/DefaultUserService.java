package deep.in.spring.cloud;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@Profile(value = "default")
@Service
public class DefaultUserService implements UserService {
    @Override
    public String findAll() {
        return "User=default";
    }
}
