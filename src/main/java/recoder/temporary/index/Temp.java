package recoder.temporary.index;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ComponentScan
public class Temp {
	
	@RequestMapping("/")
	public String toIndex() {
		return "index";
	}

	@RequestMapping("/error")
	public String toError() {
		return "error";
	}
}
