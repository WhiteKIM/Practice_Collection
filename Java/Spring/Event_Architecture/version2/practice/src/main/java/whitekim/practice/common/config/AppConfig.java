package whitekim.practice.common.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppConfig {
    private boolean isAvailableProcessPayment = true;

    public void changePaymentState(boolean state) {
        this.isAvailableProcessPayment = state;
    }
}
