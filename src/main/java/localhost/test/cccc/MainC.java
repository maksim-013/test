package localhost.test.cccc;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainC {

  @PostMapping("/")
  public String n(String d) {
    return "123";
  }
}
