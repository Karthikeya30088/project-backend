@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}